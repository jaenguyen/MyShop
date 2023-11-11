package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.StatisticsGroupAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Statistics;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtTransaction;
    private Spinner timestampSpinner;
    private Spinner typeSpinner;
    private ImageButton btnBack;
    RecyclerView recyclerView;
    StatisticsGroupAdapter statisticsGroupAdapter;
    LinearLayoutManager layoutManager;
    private List<Statistics> statisticsList;
    private int typeScope = 0;
    private int typeTrans = 0;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        init();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(statisticsGroupAdapter);

        dbFactory.getStatistics(dbFactory.getUserId(), typeScope, typeTrans)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Statistics statistics = documentSnapshot.toObject(Statistics.class);
                            statisticsList.add(statistics);
                        }
                        statisticsGroupAdapter.clear();
                        statisticsGroupAdapter.setStatisticsGroupList(ConversionHelper.getStatisticsGroupList(statisticsList));
                        txtTransaction.setText(String.format("%d Giao dịch", statisticsList.size()));
                    }
                });

        timestampSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != typeScope) {
                    typeScope = position;
                    dbFactory.getStatistics(dbFactory.getUserId(), typeScope, typeTrans)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    statisticsList.clear();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Statistics statistics = documentSnapshot.toObject(Statistics.class);
                                        statisticsList.add(statistics);
                                    }
                                    statisticsGroupAdapter.clear();
                                    statisticsGroupAdapter.setStatisticsGroupList(ConversionHelper.getStatisticsGroupList(statisticsList));
                                    txtTransaction.setText(String.format("%d Giao dịch", statisticsList.size()));
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != typeTrans) {
                    typeTrans = position;
                    dbFactory.getStatistics(dbFactory.getUserId(), typeScope, typeTrans)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    statisticsList.clear();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Statistics statistics = documentSnapshot.toObject(Statistics.class);
                                        statisticsList.add(statistics);
                                    }
                                    statisticsGroupAdapter.clear();
                                    statisticsGroupAdapter.setStatisticsGroupList(ConversionHelper.getStatisticsGroupList(statisticsList));
                                    txtTransaction.setText(String.format("%d Giao dịch", statisticsList.size()));
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        txtTransaction = findViewById(R.id.txt_transaction);
        btnBack = findViewById(R.id.toolbar_back_button);
        recyclerView = findViewById(R.id.rcv_statistics);
        statisticsList = new ArrayList<>();
        timestampSpinner = findViewById(R.id.timestamp_spinner);
        timestampSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.timestamp)));
        typeSpinner = findViewById(R.id.type_spinner);
        typeSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.type_trans)));
        statisticsGroupAdapter = new StatisticsGroupAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
    }
}