package com.zxwl.frame.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zxwl.frame.App;
import com.zxwl.frame.R;
import com.zxwl.frame.bean.UserInfo;
import com.zxwl.frame.constant.Account;
import com.zxwl.frame.net.api.UserInfoApi;
import com.zxwl.frame.net.callback.RxSubscriber;
import com.zxwl.frame.net.exception.ResponeThrowable;
import com.zxwl.frame.net.http.HttpUtils;
import com.zxwl.frame.utils.ACache;
import com.zxwl.frame.utils.Toastor;
import com.zxwl.frame.utils.UserHelper;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etPwd;
    private TextView tvLogin;
    private TextView tvSaveInfo;
    private TextView tvForgetPwd;

    //是否保存用户信息
    private boolean saveInfo = false;

    //保存缓存
    private ACache mCache;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void findViews() {
        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        tvSaveInfo = (TextView) findViewById(R.id.tv_save_info);
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
    }

    @Override
    protected void initData() {
        etName.setText(PreferencesHelper.getData(Account.LOGIN_NAME));
        etPwd.setText(PreferencesHelper.getData(Account.LOGIN_PWD));

        mCache = ACache.get(App.getInstance());
    }

    @Override
    protected void setListener() {
        tvLogin.setOnClickListener(this);
        tvSaveInfo.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录
            case R.id.tv_login:
                login();
                break;

            //忘记密码
            case R.id.tv_forget_pwd:
                Toast.makeText(this, "忘记密码", Toast.LENGTH_SHORT).show();
                break;

            //保存用户信息
            case R.id.tv_save_info:
                saveInfo = !saveInfo;
                tvSaveInfo.setCompoundDrawablesWithIntrinsicBounds(saveInfo ? R.mipmap.tv_login_on : R.mipmap.tv_login_off, 0, 0, 0);
                break;

            default:
                break;
        }
    }

    private void login() {
        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new Toastor(LoginActivity.this).getSingletonToast("用户名不能为空").show();
            return;
        }

        String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            new Toastor(LoginActivity.this).getSingletonToast("用户名不能为空").show();
            return;
        }
        //登录的网络请求
        loginRequest(name, pwd);
    }

    /**
     * 登录的网络请求
     *
     * @param name 用户名
     * @param pwd  密码
     */
    private void loginRequest(String name, String pwd) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("正在登陆")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        //点击对话框以外的地方，对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        //点击对话框意外的地方和返回键，对话框都不消失
//        dialog.setCancelable(false);
        dialog.show();

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .builder(UserInfoApi.class)
                .login(name, pwd)
                .compose(this.<UserInfo>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new RxSubscriber<UserInfo>() {
                            @Override
                            public void onSuccess(UserInfo userInfo) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                //存储登录状态
                                PreferencesHelper.saveData(Account.IS_LOGIN, "true");
                                //保存用户信息
                                UserHelper.saveUser(userInfo);
                                //跳转到登录界面
                                HomeActivity.startActivity(LoginActivity.this);
                                if (saveInfo) {
                                    //保存账号和密码
                                    PreferencesHelper.saveData(Account.LOGIN_NAME, name);
                                    PreferencesHelper.saveData(Account.LOGIN_PWD, pwd);
                                } else {
                                    //保存账号和密码
                                    PreferencesHelper.saveData(Account.LOGIN_NAME, "");
                                    PreferencesHelper.saveData(Account.LOGIN_PWD, "");
                                    etPwd.setText("");
                                    etName.setText("");
                                }

                                //保存登录信息,信息过期软件不可用
                                String asString = mCache.getAsString(Account.LOGIN_TIME);
                                //如果为空则代表没有登录成功过,没有纪录过权限数据,所以需要纪录
                                if (TextUtils.isEmpty(asString)) {
                                    mCache.put(Account.LOGIN_TIME, "true", 5 * ACache.TIME_DAY);
//                                    mCache.put(Account.LOGIN_TIME, "true", 60 * 5);
                                }
                            }

                            @Override
                            protected void onError(ResponeThrowable responeThrowable) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }


}
