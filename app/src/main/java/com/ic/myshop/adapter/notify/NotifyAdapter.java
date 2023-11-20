package com.ic.myshop.adapter.notify;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ic.myshop.R;
import com.ic.myshop.adapter.statistics.StatisticsAdapter;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Notify;
import com.ic.myshop.output.StatisticsGroup;

import java.util.ArrayList;
import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private Context context;
    private List<Notify> notifies;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    public NotifyAdapter(Context context) {
        this.context = context;
        this.notifies = new ArrayList<>();
    }

    public void setNotifies(List<Notify> notifies) {
        this.notifies = notifies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        Notify notify = notifies.get(position);
        if (notify == null) return;
        holder.txtBody.setText(notify.getBody());
        holder.txtTime.setText(ConversionHelper.formatDateTime(notify.getTimestamp()));

        if (notify.isUnread()) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
            holder.txtBody.setTypeface(null, Typeface.BOLD);
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.color.primary_color_pastel));
        } else {
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
            holder.txtBody.setTypeface(null, Typeface.NORMAL);
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.color.white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUnread(notify);
            }
        });
    }

    private void updateUnread(Notify notify) {
        notify.setUnread(false);
        dbFactory.updateNotify(notify);
    }

    @Override
    public int getItemCount() {
        if (notifies != null) return notifies.size();
        return 0;
    }

    public void clear() {
        notifies.clear();
        notifyDataSetChanged();
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle,txtBody, txtTime;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtBody = itemView.findViewById(R.id.txt_body);
            txtTime = itemView.findViewById(R.id.txt_time);
        }
    }
}
