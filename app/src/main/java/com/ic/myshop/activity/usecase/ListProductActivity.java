package com.ic.myshop.activity.usecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.product.ProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.SortField;
import com.ic.myshop.constant.TypeProduct;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class ListProductActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private long maxScore = Long.MAX_VALUE;
    private long minScore = 0;
    private boolean isScrolling = false;
    private static final DbFactory dbFactory = DbFactory.getInstance();
    private TypeProduct typeProduct;
    private SortField sortField;
    private Spinner sortSpinner, categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        typeProduct = (TypeProduct) getIntent().getSerializableExtra(InputParam.TYPE);
        sortField = (SortField) getIntent().getSerializableExtra(InputParam.FIELD);
        if (typeProduct == null) {
            typeProduct = TypeProduct.ALL;
        }
        if (sortField == null) {
            sortField = SortField.NEWEST_ARRIVALS;
        }
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rcvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentItem = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int scrollOutItem = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling && currentItem + scrollOutItem == totalItemCount) {
                    isScrolling = false;
                    loadMoreProduct();
                }
            }
        });

        // 0: ngày ra mắt, 1: nổi bật, 2: được yêu thích, 3: giá tăng dần, 4: giá giảm dần
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int sortType, long id) {
                sortField = SortField.getSortField(sortType);
                maxScore = Long.MAX_VALUE;
                minScore = 0;
                dbFactory.getProducts(typeProduct, sortField, maxScore, minScore, 6).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productAdapter.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productAdapter.addProduct(product);
                            }
                            rcvProduct.scrollToPosition(0);
                            if (!task.getResult().isEmpty()) {
                                if (sortField == SortField.PRICE_LOW) {
                                    minScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                } else {
                                    maxScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                }
                            }
                        } else {

                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int sortType, long id) {
                typeProduct = TypeProduct.getTypeProduct(sortType);
                maxScore = Long.MAX_VALUE;
                minScore = 0;
                dbFactory.getProducts(typeProduct, sortField, maxScore, minScore, 6).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productAdapter.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productAdapter.addProduct(product);
                            }
                            rcvProduct.scrollToPosition(0);
                            if (!task.getResult().isEmpty()) {
                                if (sortField == SortField.PRICE_LOW) {
                                    minScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                } else {
                                    maxScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                }
                            }
                        } else {

                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.LIST_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
        progressBar = findViewById(R.id.progress_bar);
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this);
        rcvProduct.setAdapter(productAdapter);
        sortSpinner = findViewById(R.id.sort_spinner);
        sortSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.sort)));
        sortSpinner.setSelection(SortField.getCode(sortField));
        categorySpinner = findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.type)));
        categorySpinner.setSelection(TypeProduct.getCode(typeProduct));
    }

    private void loadMoreProduct() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dbFactory.getProducts(typeProduct, sortField, maxScore, minScore, 6).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productAdapter.addProduct(product);
                            }
                            progressBar.setVisibility(View.GONE);
                            if (!task.getResult().isEmpty()) {
                                if (sortField == SortField.PRICE_LOW) {
                                    minScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                } else {
                                    maxScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(SortField.getField(sortField));
                                }
                            }
                        } else {
                            isScrolling = false;
                        }
                    }
                });
            }
        }, 2000);
    }
}