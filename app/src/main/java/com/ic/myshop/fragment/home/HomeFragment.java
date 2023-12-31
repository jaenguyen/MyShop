package com.ic.myshop.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ic.myshop.R;
import com.ic.myshop.activity.usecase.CartActivity;
import com.ic.myshop.activity.usecase.ListProductActivity;
import com.ic.myshop.adapter.product.HomeProductAdapter;
import com.ic.myshop.adapter.filter.SliderAdapter;
import com.ic.myshop.adapter.filter.TypeProductAdapter;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.SortField;
import com.ic.myshop.constant.TypeProduct;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;
import com.ic.myshop.output.Slider;
import com.ic.myshop.output.TypeProductItem;

import me.relex.circleindicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager viewPagerSlider;
    private CircleIndicator circleIndicator;
    private SliderAdapter sliderAdapter;
    private ImageView btnCart;
    // rcv hot product
    private RecyclerView rcvHotProduct, rcvNewProduct, rcvLikedProduct, rcvTypeProduct;
    private LinearLayoutManager linearLayoutManager1, linearLayoutManager2, linearLayoutManager3,
            linearLayoutManager4;
    private HomeProductAdapter hotProductAdapter, newProductAdapter, likedProductAdapter;
    private TypeProductAdapter typeProduceAdapter;
    private TextView getAllHot, getAllNew, getAllLiked;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // slider
        viewPagerSlider = view.findViewById(R.id.view_pager_slider);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        sliderAdapter = new SliderAdapter(getContext(), getSliderPhoto());
        viewPagerSlider.setAdapter(sliderAdapter);
        sliderAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        btnCart = view.findViewById(R.id.btn_cart);
        // rcv hot product
        rcvHotProduct = view.findViewById(R.id.rcv_hot_product);
        rcvNewProduct = view.findViewById(R.id.rcv_new_product);
        rcvLikedProduct = view.findViewById(R.id.rcv_like_product);
        rcvTypeProduct = view.findViewById(R.id.rcv_category_product);
        linearLayoutManager1 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager3 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager4 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rcvHotProduct.setLayoutManager(linearLayoutManager1);
        rcvNewProduct.setLayoutManager(linearLayoutManager2);
        rcvLikedProduct.setLayoutManager(linearLayoutManager3);
        rcvTypeProduct.setLayoutManager(linearLayoutManager4);
        hotProductAdapter = new HomeProductAdapter(getContext());
        newProductAdapter = new HomeProductAdapter(getContext());
        likedProductAdapter = new HomeProductAdapter(getContext());
        typeProduceAdapter = new TypeProductAdapter(getContext(), getTypeProduct());
        rcvHotProduct.setAdapter(hotProductAdapter);
        rcvNewProduct.setAdapter(newProductAdapter);
        rcvLikedProduct.setAdapter(likedProductAdapter);
        rcvTypeProduct.setAdapter(typeProduceAdapter);

        dbFactory.getListProductByField(InputParam.SOLD_NUMBER, Long.MAX_VALUE, 10)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                hotProductAdapter.addProduct(product);
                            }
                        }
                    }
                });

        dbFactory.getProductsDefault(Long.MAX_VALUE, 10)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                newProductAdapter.addProduct(product);
                            }
                        }
                    }
                });

        dbFactory.getListProductByField(InputParam.LIKES, Long.MAX_VALUE, 10)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                likedProductAdapter.addProduct(product);
                            }
                        }
                    }
                });

        // getAll
        getAllHot = view.findViewById(R.id.get_all_hot);
        getAllNew = view.findViewById(R.id.get_all_new);
        getAllLiked = view.findViewById(R.id.get_all_liked);
        getAllHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListProductActivity.class);
                intent.putExtra(InputParam.FIELD, SortField.BEST_SELLERS);
                startActivity(intent);
            }
        });
        getAllNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListProductActivity.class);
                intent.putExtra(InputParam.FIELD, SortField.NEWEST_ARRIVALS);
                startActivity(intent);
            }
        });
        getAllLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListProductActivity.class);
                intent.putExtra(InputParam.FIELD, SortField.MOST_LIKES);
                startActivity(intent);
            }
        });

        Intent intentCart = new Intent(getContext(), CartActivity.class);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCart);
            }
        });


    }

    private List<Slider> getSliderPhoto() {
        List<Slider> sliders = new ArrayList<>();
        sliders.add(new Slider(R.drawable.slider1));
        sliders.add(new Slider(R.drawable.slider2));
        sliders.add(new Slider(R.drawable.slider3));
        return sliders;
    }

    public List<TypeProductItem> getTypeProduct() {
        List<TypeProductItem> typeProducts = new ArrayList<>();
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.FASHION), R.drawable.tp_1));
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.ELECTRONIC), R.drawable.tp_2));
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.HEALTHY), R.drawable.tp_3));
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.BEAUTY), R.drawable.tp_4));
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.SPORT), R.drawable.tp_5));
        typeProducts.add(new TypeProductItem(TypeProduct.getName(TypeProduct.OTHERS), R.drawable.tp_6));
        return typeProducts;
    }
}
