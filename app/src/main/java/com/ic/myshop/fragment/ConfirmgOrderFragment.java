package com.ic.myshop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.CfOrderAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.output.OrderOutput;

public class ConfirmgOrderFragment extends Fragment {

    private RecyclerView rcvCfOrder;
    private LinearLayoutManager linearLayoutManager;
    private CfOrderAdapter cfOrderAdapter;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmg_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvCfOrder = view.findViewById(R.id.rcv_cf_order);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCfOrder.setLayoutManager(linearLayoutManager);
        cfOrderAdapter = new CfOrderAdapter(getContext());
        rcvCfOrder.setAdapter(cfOrderAdapter);
        db = FirebaseFirestore.getInstance();

        db.collection(DatabaseConstant.ORDERS)
                .whereEqualTo(InputParam.PARENT_ID, dbFactory.getUserId())
                .whereEqualTo(InputParam.STATUS, 0)
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        cfOrderAdapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            Order order = documentSnapshot.toObject(Order.class);
                            dbFactory.getProduct(order.getProductId())
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Product product = task.getResult().toObject(Product.class);
                                            cfOrderAdapter.addOrder(new OrderOutput(order, product.getImageUrl(), product.getName()));
                                        }
                                    });
                        }
                    }
                });
    }
}