package com.example.touristguide.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.touristguide.R;
import com.example.touristguide.activities.MainActivity;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class ScanImageFragment extends BaseFragment {

    /*This fragment is used for scan image of any place and identifies the place*/

    private static final String TAG = "ShareFragment";
    //automl vision variables
    private static final int PERMISSION_REQUESTS = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    private static String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    //firebase automl variables
    private static FirebaseVisionImageLabeler labeler;
    private static FirebaseVisionImage image;
    private static FirebaseVisionOnDeviceAutoMLImageLabelerOptions options;
    private static String result;
    private static String text;
    private static float confidence;
    @BindView(R.id.camerabtn)
    Button camera;
    @BindView(R.id.gallerybtn)
    Button gallery;

    public static ScanImageFragment newInstance(String placeName) {
        Log.d(TAG, "newInstance: called");
        Bundle args = new Bundle();
        tagString = placeName;
        ScanImageFragment fragment = new ScanImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * For all necessary permissions
     */
    private static boolean isPermissionGranted(Context context, String permission) {
        Log.d(TAG, "isPermissionGranted: called");
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return false;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
        initializeModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
        if (allPermissionsGranted()) {
            camera.setOnClickListener(v -> cameraButtonClick());
            gallery.setOnClickListener(v -> galleryButtonClick());
        } else {
            camera.setOnClickListener(v -> cameraButtonClick());
            gallery.setOnClickListener(v -> galleryButtonClick());
        }
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("ScanImage");
    }

    /**
     * To open camera Intent
     */
    private void startCameraIntentForResult() {
        Log.d(TAG, "startCameraIntentForResult: called");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * For getting Image from gallery
     */
    private void startChooseImageIntentForResult() {
        Log.d(TAG, "startChooseImageIntentForResult: called");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    /**
     * For firebase automl vision model
     */
    private void initializeModel() {
        Log.d(TAG, "initializeModel: called");

        final FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build();

        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder optionsBuilder;
        optionsBuilder = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel);
        options = optionsBuilder
                .setConfidenceThreshold(0.8f)  // Evaluate your model in the Firebase console
                // to determine an appropriate threshold.
                .build();
    }

    private void imageFromBitmap(Bitmap bitmap) {
        Log.d(TAG, "imageFromBitmap: called");
        image = FirebaseVisionImage.fromBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: called");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageFromBitmap(bitmap);
            getImageResult(image);

        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            // Let's read picked image data - its URI
            //variables to get scan result
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(pickedImage), filePath, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            imageFromBitmap(bitmap);
            getImageResult(image);
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    private void getImageResult(final FirebaseVisionImage image) {
        Log.d(TAG, "getImageResult: called");
        try {
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
        } catch (FirebaseMLException e) {
            String imageLablingError = "Error in process of image labeling.";
            Toast.makeText(getActivity(), imageLablingError, Toast.LENGTH_SHORT).show();
        }

        labeler.processImage(image)
                .addOnSuccessListener(labels -> {
                    if (labels.isEmpty()) {
                        Toast.makeText(getActivity(), "Sorry Not able to fetch Result, Retry Please", Toast.LENGTH_LONG).show();
                    } else {
                        for (FirebaseVisionImageLabel label : labels) {
                            text = label.getText();
                            confidence = label.getConfidence();
                            result = text + " : " + confidence;
                            System.out.println(text + " : " + confidence);
                            if (mFragmentNavigation != null) {
                                mFragmentNavigation.pushFragment(PlaceFragment.newInstance(text));
                            }
                        }
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Image label detection failed, Please Retry", Toast.LENGTH_LONG).show());
    }

    private boolean allPermissionsGranted() {
        Log.d(TAG, "allPermissionsGranted: called");
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this.getContext(), permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        Log.d(TAG, "getRequiredPermissions: called");
        try {
            PackageInfo info =
                    Objects.requireNonNull(getActivity()).getPackageManager()
                            .getPackageInfo(getActivity().getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private void getRuntimePermissions() {
        Log.d(TAG, "getRuntimePermissions: called");
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this.getContext(), permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        getRuntimePermissions();
    }

    private void cameraButtonClick() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            startCameraIntentForResult();
        } else {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[1]);
            AlertDialog.Builder blder = new AlertDialog.Builder(getContext());
            blder.setTitle("Permission Request");
            blder.setMessage("To fetch Image result from camera allow camera access");
            if (!showRationale) {
                //if permission never ask again is selected
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getContext().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                blder.setPositiveButton("Ok", (dialog, which) -> startActivity(myAppSettings));
                blder.setCancelable(false);
                AlertDialog alertErrDlg = blder.create();
                alertErrDlg.show();
            } else {
                //if permission denied normally
                blder.setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                        new String[]{permissions[1]},
                        PERMISSION_REQUESTS));
                blder.setCancelable(false);
                AlertDialog alertErrDlg = blder.create();
                alertErrDlg.show();
            }
        }
    }

    private void galleryButtonClick() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            startChooseImageIntentForResult();
        } else {
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
            AlertDialog.Builder blder = new AlertDialog.Builder(getContext());
            blder.setTitle("Permission Request");
            blder.setMessage("To fetch Image result from gallery allow read Internal storage access");
            if (!showRationale) {
                //if permission never ask again is selected
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getContext().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                blder.setPositiveButton("Ok", (dialog, which) -> startActivity(myAppSettings));
                blder.setCancelable(false);
                AlertDialog alertErrDlg = blder.create();
                alertErrDlg.show();
            } else {
                //if permission denied normally
                blder.setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                        new String[]{permissions[0]},
                        PERMISSION_REQUESTS));
                blder.setCancelable(false);
                AlertDialog alertErrDlg = blder.create();
                alertErrDlg.show();
            }
        }
    }

}
