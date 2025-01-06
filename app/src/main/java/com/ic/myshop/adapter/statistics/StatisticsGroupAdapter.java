package com.ic.myshop.adapter.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ic.myshop.R;
import com.ic.myshop.output.StatisticsGroup;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGroupAdapter extends RecyclerView.Adapter<StatisticsGroupAdapter.StatisticsGroupViewHolder> {

    private Context context;
    private List<StatisticsGroup> statisticsGroupList;

    public StatisticsGroupAdapter(Context context) {
        this.context = context;
        this.statisticsGroupList = new ArrayList<>();
    }

    public void setStatisticsGroupList(List<StatisticsGroup> statisticsGroupList) {
        this.statisticsGroupList = statisticsGroupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_group, parent, false);
        return new StatisticsGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsGroupViewHolder holder, int position) {
        StatisticsGroup statisticsGroup = statisticsGroupList.get(position);
        if (statisticsGroup == null) return;
        holder.txtDate.setText(statisticsGroup.getDate());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rcvStatisticsGroup.getContext());
        holder.rcvStatisticsGroup.setLayoutManager(layoutManager);
        StatisticsAdapter documentAdapter = new StatisticsAdapter(context.getApplicationContext(), statisticsGroup.getStatisticsList());
        holder.rcvStatisticsGroup.setAdapter(documentAdapter);
    }

    @Override
    public int getItemCount() {
        if (statisticsGroupList != null) return statisticsGroupList.size();
        return 0;
    }

    public void clear() {
        statisticsGroupList.clear();
        notifyDataSetChanged();
    }

    public class StatisticsGroupViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;
        private RecyclerView rcvStatisticsGroup;

        public StatisticsGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            rcvStatisticsGroup = itemView.findViewById(R.id.rcv_statistics_group);
        }
    }
}
