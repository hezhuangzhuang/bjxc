package com.zxwl.frame.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentLinearLayout;
import com.zxwl.frame.R;
import com.zxwl.frame.bean.ConfBean;

import java.util.List;

/**
 * author：hw
 * data:2017/4/26 11:09
 * 参加会议列表的适配器
 */
public class AttendingListAdapter extends RecyclerView.Adapter<AttendingListAdapter.Holder> {
    private List<ConfBean> beanList;

    private boolean allSelect = false;

    public AttendingListAdapter(List<ConfBean> list) {
        this.beanList = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_join, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ConfBean bean = beanList.get(position);
        holder.tvNumber.setText("");
        if (bean.select) {
            holder.tvNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_login_check_on, 0, 0, 0);
        } else {
            holder.tvNumber.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_login_check_off, 0, 0, 0);
        }

        holder.tvUnit.setText(bean.unitIdName);
        holder.tvDeviceNumber.setText(bean.deviceNumber);
        holder.tvDeviceName.setText(bean.deviceName);
        holder.tvDelete.setText("");
        holder.tvDelete.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.list_del, 0, 0, 0);

        holder.llContent.setOnClickListener(
                v -> {
                    if (null != itemClickListener) {
                        itemClickListener.onClick(holder.getAdapterPosition());
                    }
                }
        );

        holder.tvDelete.setOnClickListener(
                v -> {
                    if (null != itemClickListener) {
                        itemClickListener.onDelete(holder.getAdapterPosition());
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return null != beanList ? beanList.size() : 0;
    }

    public ConfBean getItem(int position) {
        return beanList.get(position);
    }

    public List<ConfBean> getBeanList() {
        return beanList;
    }

    public void add(ConfBean confBean) {
        beanList.add(confBean);
        notifyDataSetChanged();
    }

    public void addAll(List<ConfBean> list) {
        beanList.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        beanList.remove(position);
        notifyDataSetChanged();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        PercentLinearLayout llContent;
        TextView tvNumber;
        TextView tvUnit;
        TextView tvDeviceNumber;
        TextView tvDeviceName;
        TextView tvDelete;

        public Holder(View itemView) {
            super(itemView);
            llContent = (PercentLinearLayout) itemView.findViewById(R.id.ll_content);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvUnit = (TextView) itemView.findViewById(R.id.tv_unit);
            tvDeviceNumber = (TextView) itemView.findViewById(R.id.tv_device_number);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }

    private onItemClickListener itemClickListener;

    public void setOnItemClickListener(AttendingListAdapter.onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface onItemClickListener {
        void onClick(int position);

        void onDelete(int position);
    }
}
