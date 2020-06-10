package com.example.touristguide.utils;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineData {
    public static FirebaseDatabase mDatabase;
    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            mDatabase.getReference().keepSynced(true);
        }
        return mDatabase;
    }
}
