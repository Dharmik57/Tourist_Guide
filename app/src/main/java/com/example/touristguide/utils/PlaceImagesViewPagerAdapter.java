package com.example.touristguide.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.touristguide.R;

import butterknife.BindView;

public class PlaceImagesViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PlaceImagesViewAdapter";

    /*This class is used for displaying place image list on particular place page*/
    private Bitmap[] placeimages;
    private LayoutInflater inflater;

    public PlaceImagesViewPagerAdapter(Context context, Bitmap[] placeimages) {
        this.placeimages = placeimages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "destroyItem: called");
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        Log.d(TAG, "instantiateItem: called");
        View myViewPagerLAyout = inflater.inflate(R.layout.place_image_card_view, view, false);
        ImageView placeImages = myViewPagerLAyout.findViewById(R.id.placeimageview);
        placeImages.setImageBitmap(placeimages[position]);
        view.addView(myViewPagerLAyout, 0);
        return myViewPagerLAyout;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: called");
        return placeimages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        Log.d(TAG, "isViewFromObject: called");
        return view.equals(object);
    }
}
