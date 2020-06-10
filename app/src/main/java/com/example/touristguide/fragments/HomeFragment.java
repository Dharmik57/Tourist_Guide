package com.example.touristguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.touristguide.R;
import com.example.touristguide.activities.MainActivity;
import com.example.touristguide.utils.ItemListViewadapter;
import com.example.touristguide.utils.PlaceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends BaseFragment {
    /*This fragment is used to display all the places list*/
    @BindView(R.id.nearByPlaceText)
    TextView nearByPlacestextView;
    @BindView(R.id.NearByPlaces)
    RecyclerView nearByPlaceRecyclerview;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        nearByPlaceRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        nearByPlaceRecyclerview.setLayoutManager(linearLayoutManager);
        List<PlaceInfo> nearByPlaces = new ArrayList<>();
        nearByPlaces.add(new PlaceInfo("Kamati Baug", R.drawable.kamatibaug));
        nearByPlaces.add(new PlaceInfo("Sur Sagar Lake", R.drawable.sursagarlake));
        nearByPlaces.add(new PlaceInfo("Planeterium", R.drawable.planeterium));
        nearByPlaces.add(new PlaceInfo("Lakshmi Vilas Palace", R.drawable.lvp));
        nearByPlaces.add(new PlaceInfo("Kirti Stambh", R.drawable.kirtistambh));
        ItemListViewadapter nearbyplaceAdapter = new ItemListViewadapter(nearByPlaces);
        nearByPlaceRecyclerview.setAdapter(nearbyplaceAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("Tourist Guide");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
