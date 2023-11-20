package com.ic.myshop.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.usecase.CartActivity;
import com.ic.myshop.adapter.notify.NotifyAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Notify;

import java.util.List;

public class NotifyFragment extends Fragment {

    private TextView txtEmptyNotify, txtMallAllRead;
    private ImageView btnCart;
    private RecyclerView rcvNotify;
    private LinearLayoutManager linearLayoutManager;
    private NotifyAdapter notifyAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCart = view.findViewById(R.id.btn_cart);
        txtEmptyNotify = view.findViewById(R.id.txt_empty_notify);
        txtMallAllRead = view.findViewById(R.id.txt_mark_all_read);
        rcvNotify = view.findViewById(R.id.rcv_notify);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvNotify.setLayoutManager(linearLayoutManager);
        notifyAdapter = new NotifyAdapter(getContext());
        rcvNotify.setAdapter(notifyAdapter);


        Intent intentCart = new Intent(getContext(), CartActivity.class);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCart);
            }
        });

        db.collection(DatabaseConstant.NOTIFICATIONS)
                .whereEqualTo(InputParam.PARENT_ID, dbFactory.getUserId())
                .orderBy(InputParam.TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<Notify> notifies = value.toObjects(Notify.class);
                        notifyAdapter.clear();
                        if (notifies.isEmpty()) {
                            txtEmptyNotify.setVisibility(View.VISIBLE);
                            txtMallAllRead.setVisibility(View.GONE);
                        } else {
                            txtEmptyNotify.setVisibility(View.GONE);
                            txtMallAllRead.setVisibility(View.VISIBLE);
                            notifyAdapter.setNotifies(notifies);
                        }
                    }
                });

        txtMallAllRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbFactory.markAllReadNotify();
            }
        });
    }
}
