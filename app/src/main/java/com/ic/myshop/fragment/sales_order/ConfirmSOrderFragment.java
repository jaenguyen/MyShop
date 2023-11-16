package com.ic.myshop.fragment.sales_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ic.myshop.adapter.sales_order.CfSOrderAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Order;
import com.ic.myshop.model.Product;
import com.ic.myshop.output.OrderOutput;

public class ConfirmSOrderFragment extends Fragment {

    private RecyclerView rcvCfOrder;
    private LinearLayoutManager linearLayoutManager;
    private CfSOrderAdapter cfSOrderAdapter;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_sales_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvCfOrder = view.findViewById(R.id.rcv_cf_sales_order);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCfOrder.setLayoutManager(linearLayoutManager);
        cfSOrderAdapter = new CfSOrderAdapter(getContext());
        rcvCfOrder.setAdapter(cfSOrderAdapter);
        db = FirebaseFirestore.getInstance();

        db.collection(DatabaseConstant.ORDERS)
                .whereEqualTo(InputParam.SELLER_ID, dbFactory.getUserId())
                .whereEqualTo(InputParam.STATUS, 0)
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        cfSOrderAdapter.clear();
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            Order order = documentSnapshot.toObject(Order.class);
                            dbFactory.getProduct(order.getProductId())
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Product product = task.getResult().toObject(Product.class);
                                            cfSOrderAdapter.addOrder(new OrderOutput(order, product.getImageUrl(), product.getName()));
                                        }
                                    });
                        }
                    }
                });

        // TODO: FIX CODE
        db.collection(DatabaseConstant.ORDERS)
                .whereEqualTo(InputParam.SELLER_ID, dbFactory.getUserId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        boolean notStatus = true;
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            Order order = documentSnapshot.toObject(Order.class);
                            if (order.getStatus() == 0) {
                                notStatus = false;
                                break;
                            }
                        }
                        if (notStatus) {
                            cfSOrderAdapter.clear();
                            cfSOrderAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}