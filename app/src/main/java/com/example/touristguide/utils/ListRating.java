package com.example.touristguide.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.touristguide.R;

import java.util.List;

public class ListRating extends ArrayAdapter<Rating> {

    List<Rating> artists;
    private Context context;

    // private static ArrayList<Rating> listContact;
    public ListRating(Context context, List<Rating> artists) {
        super(context, R.layout.layout_ratinglist, artists);
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.layout_ratinglist, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);
        RatingBar ratelist = listViewItem.findViewById(R.id.listrate);

        Rating artist = artists.get(position);
        textViewName.setText(artist.getId());
        textViewGenre.setText(artist.getComment());
        ratelist.setRating(Float.parseFloat(artist.getRatestar()));

        return listViewItem;
    }
}
