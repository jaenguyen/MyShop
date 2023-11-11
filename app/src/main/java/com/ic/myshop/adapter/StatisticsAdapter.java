package com.ic.myshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Statistics;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    private Context context;
    private List<Statistics> statisticsList;

    public StatisticsAdapter(Context context, List<Statistics> statisticsList) {
        this.context = context;
        this.statisticsList = statisticsList;
    }

    public StatisticsAdapter(Context context) {
        this.context = context;
        statisticsList = new ArrayList<>();
    }

    public void addStatistics(Statistics statistics) {
        this.statisticsList.add(statistics);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics, parent, false);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        Statistics statistics = statisticsList.get(position);
        if (statistics == null) return;
        int type = statistics.getType();
        holder.txtOrderId.setText(String.format("Mã đơn hàng: %s", statistics.getOrderId()));
        if (type == 0) {
            holder.txtTitle.setText(Constant.PAYMENT_ORDER);
            holder.txtAmount.setText(String.format("- %s VNĐ", ConversionHelper.formatNumber(statistics.getPrice())));
            holder.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        if (type == 1) {
            holder.txtTitle.setText(Constant.RECEIVE_ORDER);
            holder.txtAmount.setText(String.format("+ %s VNĐ", ConversionHelper.formatNumber(statistics.getPrice())));
            holder.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.green_pastel));
        }
    }

    @Override
    public int getItemCount() {
        if (statisticsList != null) return statisticsList.size();
        return 0;
    }

    public List<Statistics> getStatisticsList() {
        return statisticsList;
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtOrderId;
        private TextView txtAmount;

        public StatisticsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txt_title);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtAmount = itemView.findViewById(R.id.txt_amount_0);
        }
    }
}
