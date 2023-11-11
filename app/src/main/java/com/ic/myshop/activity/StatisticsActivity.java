package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.StatisticsAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Statistics;

public class StatisticsActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    RecyclerView recyclerView;
    StatisticsAdapter statisticsAdapter;
    LinearLayoutManager layoutManager;
    FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        init();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(statisticsAdapter);

        db.collection(DatabaseConstant.STATISTICS)
                .whereEqualTo(InputParam.PARENT_ID, dbFactory.getUserId())
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Statistics statistics = documentSnapshot.toObject(Statistics.class);
                            statisticsAdapter.addStatistics(statistics);
                        }
                    }
                });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.STATISTICS);
        btnBack = findViewById(R.id.toolbar_back_button);
        recyclerView = findViewById(R.id.rcv_statistics);
        statisticsAdapter = new StatisticsAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        db = FirebaseFirestore.getInstance();
    }
}