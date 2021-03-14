package com.example.biji;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class MyNewAdapter extends PagerAdapter {

    List<View> views; // 声明

    public MyNewAdapter(List<View> views) {
        this.views = views; // 填充
    }

    @Override
    public int getCount() {
        return views.size(); // 返回view的数量
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object; // 判断view是否来自object
    }

    @Override // 怎样来销毁选项
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView(views.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ((ViewPager)container).addView(views.get(position));
        return views.get(position);
    }
}