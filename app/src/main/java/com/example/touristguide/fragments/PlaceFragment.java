package com.example.touristguide.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.touristguide.R;
import com.example.touristguide.activities.MainActivity;
import com.example.touristguide.utils.ListRating;
import com.example.touristguide.utils.OfflineData;
import com.example.touristguide.utils.PlaceImagesViewPagerAdapter;
import com.example.touristguide.utils.PlaceInfo;
import com.example.touristguide.utils.Rating;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class PlaceFragment extends BaseFragment {
    private static final String TAG = "PlaceFragment";
    private static String placeString;
    /*This fragment displays all the information of particular fragment*/
    @BindView(R.id.placeImagesViewPager)
    ViewPager placeImagesViewPager;
    @BindView(R.id.circleIndictor)
    CircleIndicator circleIndicator;
    @BindView(R.id.history)
    CardView historyCard;
    @BindView(R.id.timing)
    CardView timingCard;
    @BindView(R.id.tickets)
    CardView ticketsCard;
    @BindView(R.id.nearbyplacesfromplace)
    CardView nearByPlaceFromPlacesCard;
    @BindView(R.id.nearbyhotelsfromplace)
    CardView nearByHotelsFromPlaceCard;
    @BindView(R.id.descriptionText)
    TextView about;
    @BindView(R.id.timedescriptiontext)
    TextView timing;
    @BindView(R.id.ticketdescriptiontext)
    TextView tickets;
    @BindView(R.id.nearByPlacedescriptionText)
    TextView nearByPlaces;
    @BindView(R.id.howtorechdescriptiontext)
    TextView howToReach;
    @BindView(R.id.nearByhotelsdescriptionText)
    TextView nearByHotels;
    @BindView(R.id.reviewbtn)
    ImageView btnReview;
    @BindView(R.id.reviewText)
    TextView textReview;

    @BindView(R.id.listViewArtists)
    ListView listViewArtists;
    private Bitmap[] imagearr = new Bitmap[5];
    private List<Rating> ratingList;
    private DatabaseReference databaseRatings;
    private String ReviewPlace;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference[] storageReference = new StorageReference[5];

    public PlaceFragment() {
        // Required empty public constructor
    }

    public static PlaceFragment newInstance(String placeName) {
        Log.d(TAG, "newInstance: called");
        Bundle args = new Bundle();
        placeString = placeName;
        PlaceFragment fragment = new PlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        try {
            Log.d(TAG, "onCreate: called");
            super.onCreate(savedInstanceState);
            OfflineData.getDatabase();
            setHasOptionsMenu(true);
            ratingList = new ArrayList<>();
            databaseRatings = FirebaseDatabase.getInstance().getReference("PlaceRating");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PlaceDetails(String placename) {
        super.onStart();
        //attaching value event listener
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("PlaceDetail").child(placename);
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                dataSnapshot.getValue();
                PlaceInfo placeinfo = dataSnapshot.getValue(PlaceInfo.class);
                about.setText(Objects.requireNonNull(placeinfo).getAbout().replace("\\n", "\n"));
                timing.setText(placeinfo.getTiming().replace("\\n", "\n"));
                tickets.setText(placeinfo.getTickets().replace("\\n", "\n"));
                nearByHotels.setText(placeinfo.getNearbyhotels().replace("\\n", "\n"));
                nearByPlaces.setText(placeinfo.getNearbyplaces().replace("\\n", "\n"));
                howToReach.setText(placeinfo.getHowtoreach().replace("\\n", "\n"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private Bitmap[] getPlaceImages(String str) {
        String url = "gs://tourist-guide-72496.appspot.com/TouristGuide/PlaceImagesList";
        String temp = str.replace(" ", "_");
        final Bitmap[] bitmap = new Bitmap[5];
        for (int i = 0; i < 5; i++) {
            String placeUrl = url + "/" + temp + "/" + temp + i + ".jpg";
            storageReference[i] = storage.getReferenceFromUrl(placeUrl);
            try {
                final File file = File.createTempFile("image", "jpg");
                final int finalI = i;
                storageReference[i].getFile(file).addOnSuccessListener(taskSnapshot -> bitmap[finalI] = BitmapFactory.decodeFile(file.getAbsolutePath())).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load Images", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle(placeString);

        int Placeposition;
        switch (placeString) {
            case "Kamati Baug":
                Placeposition = 0;
                ReviewPlace = placeString;
                imagearr = getPlaceImages(placeString);
                PlaceDetails(placeString);
//                about.setText(aboutplace[Placeposition]);
//                timing.setText(timingofplace[Placeposition]);
//                tickets.setText(ticketsofplace[Placeposition]);
//                nearByHotels.setText(nearbyhotelsfromplace[Placeposition]);
//                nearByPlaces.setText(nearbyplacesfromplace[Placeposition]);
//                howToReach.setText(howtoreachplace[Placeposition]);
                break;
            case "Sur Sagar Lake":
                Placeposition = 4;
                ReviewPlace = placeString;
                imagearr = getPlaceImages(placeString);
                PlaceDetails(placeString);
//                Placeposition = 4;
//                imagearr = getPlaceImages(placeString);
//                about.setText(aboutplace[Placeposition]);
//                timing.setText(timingofplace[Placeposition]);
//                tickets.setText(ticketsofplace[Placeposition]);
//                nearByHotels.setText(nearbyhotelsfromplace[Placeposition]);
//                nearByPlaces.setText(nearbyplacesfromplace[Placeposition]);
//                howToReach.setText(howtoreachplace[Placeposition]);
                break;
            case "Planeterium":
                Placeposition = 3;
                ReviewPlace = placeString;
                imagearr = getPlaceImages(placeString);
                PlaceDetails(placeString);
//                Placeposition = 3;
//                imagearr = getPlaceImages(placeString);
//                about.setText(aboutplace[Placeposition]);
//                timing.setText(timingofplace[Placeposition]);
//                tickets.setText(ticketsofplace[Placeposition]);
//                nearByHotels.setText(nearbyhotelsfromplace[Placeposition]);
//                nearByPlaces.setText(nearbyplacesfromplace[Placeposition]);
//                howToReach.setText(howtoreachplace[Placeposition]);
                break;
            case "Lakshmi Vilas Palace":
                Placeposition = 2;
                ReviewPlace = placeString;
                imagearr = getPlaceImages(placeString);
                PlaceDetails(placeString);
//                Placeposition = 2;
//                imagearr = getPlaceImages(placeString);
//                about.setText(aboutplace[Placeposition]);
//                timing.setText(timingofplace[Placeposition]);
//                tickets.setText(ticketsofplace[Placeposition]);
//                nearByHotels.setText(nearbyhotelsfromplace[Placeposition]);
//                nearByPlaces.setText(nearbyplacesfromplace[Placeposition]);
//                howToReach.setText(howtoreachplace[Placeposition]);
                break;
            case "Kirti Stambh":
                Placeposition = 1;
                ReviewPlace = placeString;
                imagearr = getPlaceImages(placeString);
                PlaceDetails(placeString);
//                Placeposition = 1;
//                imagearr = getPlaceImages(placeString);
//                about.setText(aboutplace[Placeposition]);
//                timing.setText(timingofplace[Placeposition]);
//                tickets.setText(ticketsofplace[Placeposition]);
//                nearByHotels.setText(nearbyhotelsfromplace[Placeposition]);
//                nearByPlaces.setText(nearbyplacesfromplace[Placeposition]);
//                howToReach.setText(howtoreachplace[Placeposition]);
                break;
        }
        placeImagesViewPager.setAdapter(new PlaceImagesViewPagerAdapter(getContext(), imagearr));
        circleIndicator.setViewPager(placeImagesViewPager);

        btnReview.setOnClickListener(v -> showdialog());
        textReview.setOnClickListener(v->showdialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showdialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            LayoutInflater inflater = getLayoutInflater();
            builder.setTitle("Rate this Place");
            View dialogLayout = inflater.inflate(R.layout.ratingbar, null);
            final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
            final EditText editcomment = dialogLayout.findViewById(R.id.commentEditText);
            builder.setView(dialogLayout);
            builder.setPositiveButton("YES", (dialog, which) -> {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String useremail = currentUser.getEmail();
                    basicReadWrite(ReviewPlace, editcomment, ratingBar, useremail);
                    Toast.makeText(getContext(), "Rating is " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please Login For The Reviews ", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            builder.show();
        } catch (Exception ex) {
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void basicReadWrite(String ReviewPlace, TextView editcomment, RatingBar ratingBar, String useremail) {
        String comment = editcomment.getText().toString();
        final String star = String.valueOf(ratingBar.getRating());
        if (TextUtils.isEmpty(comment)) {
            comment = "";
        }
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();
        try {
            String id = databaseRatings.push().getKey();
            Rating rating = new Rating(useremail, comment, star);
            databaseRatings.child(ReviewPlace).child(Objects.requireNonNull(id)).setValue(rating);
            editcomment.setText("");
            Toast.makeText(getContext(), "Comment added", Toast.LENGTH_LONG).show();
            databaseRatings.child(ReviewPlace).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clearing the previous artist list
                    ratingList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Rating artist = postSnapshot.getValue(Rating.class);
                        ratingList.add(artist);
                    }
                    ListRating artistAdapter = new ListRating(getContext(), ratingList);
                    //attaching adapter to the listview
                    listViewArtists.setAdapter(artistAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "Value is: " + ex);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseRatings.child(ReviewPlace).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                ratingList.clear();
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Rating ratings = postSnapshot.getValue(Rating.class);
                    //adding artist to the list
                    ratingList.add(ratings);
                }
                ListRating artistAdapter = new ListRating(getContext(), ratingList);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
