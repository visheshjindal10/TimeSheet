package com.hartleylab.app.timesheet.activities;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hartleylab.app.timesheet.Model.HistoryDescription;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.DateUtil;

import java.util.ArrayList;
import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private List<HistoryDescription> historyDescriptionList = new ArrayList<>();

    public Adapter(List<HistoryDescription> historyDescriptionList) {
        this.historyDescriptionList = historyDescriptionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_items__data, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryDescription historyDescription = historyDescriptionList.get(position);
        if (historyDescription != null) {
            holder.tvProjectName.setText(historyDescription.getProjectName());
            holder.tvTicket.setText(historyDescription.getTicketNo());
            String date = DateUtil.getFormattedTime(historyDescription.getDate(),
                    DateUtil.LOGGED_DATE_FROM_API, DateUtil.LOGGED_DATE_FOR_LIST);
            if (!TextUtils.isEmpty(date)) holder.tvDate.setText(date);
            holder.tvLoggedHours.setText(historyDescription.getLoggedHours());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProjectName;
        public TextView tvTicket;
        public TextView tvDate;
        public TextView tvLoggedHours;

        public ViewHolder(View v) {
            super(v);
            tvProjectName = (TextView) v.findViewById(R.id.tvProjectName);
            tvTicket = (TextView) v.findViewById(R.id.tvTicket);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvLoggedHours = (TextView) v.findViewById(R.id.tvLoggedHours);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(v, historyDescriptionList.get(getLayoutPosition()));

                    }
                }
            });

        }
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public int getItemCount() {
        return historyDescriptionList.size();
    }

    public interface OnItemClickListener {
        void onClick(View v, HistoryDescription historyDescription);
    }
}
