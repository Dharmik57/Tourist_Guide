package com.example.touristguide.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.touristguide.R;
import com.example.touristguide.fragments.PlaceFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.touristguide.fragments.BaseFragment.mFragmentNavigation;

public class ItemListViewadapter extends RecyclerView.Adapter<ItemListViewadapter.ViewHolder> {
    private static final String TAG = "ItemListViewadapter";
    /*This class is used for displaying and handling the view of All the places*/

    private List<PlaceInfo> nearByPlaces;

    public ItemListViewadapter(List<PlaceInfo> nearByPlaces) {
        this.nearByPlaces = nearByPlaces;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        PlaceInfo placeInfo = nearByPlaces.get(position);
        holder.placeName.setText(placeInfo.getPlaceName());
        holder.placeImage.setImageResource(placeInfo.getPlaceImage());

        holder.placeImage.setOnClickListener(v -> {
            String placeName = placeInfo.getPlaceName();
            if (mFragmentNavigation != null) {
                mFragmentNavigation.pushFragment(PlaceFragment.newInstance(placeName));
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearByPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImage;
        TextView placeName;
        CardView placeCard;

        ViewHolder(View itemView) {
            super(itemView);
            placeImage = itemView.findViewById(R.id.Image);
            placeName = itemView.findViewById(R.id.imageName);
            placeCard = itemView.findViewById(R.id.itemcardview);
        }
    }
}
