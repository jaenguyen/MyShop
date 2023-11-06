package com.ic.myshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.CartActivity;
import com.ic.myshop.adapter.SliderAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.model.Like;
import com.ic.myshop.output.Slider;

import me.relex.circleindicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager viewPagerSlider;
    private CircleIndicator circleIndicator;
    private SliderAdapter sliderAdapter;
    private ImageView btnCart;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerSlider = view.findViewById(R.id.view_pager_slider);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        sliderAdapter = new SliderAdapter(getContext(), getSliderPhoto());
        viewPagerSlider.setAdapter(sliderAdapter);
        sliderAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        btnCart = view.findViewById(R.id.btn_cart);
        db = FirebaseFirestore.getInstance();
        db.collection(DatabaseConstant.LIKES)
                .whereEqualTo("userId", "1")
                .whereEqualTo("productId", "1")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Like like = documentSnapshot.toObject(Like.class);
                                System.out.println(1);
                            }
                        }
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
}
