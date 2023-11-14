package com.ic.myshop.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.output.Slider;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<Slider> sliders;

    public SliderAdapter(Context context, List<Slider> sliders) {
        this.context = context;
        this.sliders = sliders;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_slider, container, false);
        ImageView sliderView = view.findViewById(R.id.slider_item);

        Slider slider = sliders.get(position);
        if (slider != null) {
            Glide.with(context).load(slider.getResourceId()).into(sliderView);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if (sliders != null) return sliders.size();
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
