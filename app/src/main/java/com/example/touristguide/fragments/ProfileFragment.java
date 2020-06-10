package com.example.touristguide.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.touristguide.R;
import com.example.touristguide.activities.MainActivity;
import com.example.touristguide.utils.Image;
import com.example.touristguide.utils.OfflineData;
import com.example.touristguide.utils.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";
    /*This class is used to handle user activity*/
    @BindView(R.id.editbtn)
    ImageView btnedit;
    @BindView(R.id.profilepic)
    ImageView profilepicimage;
    @BindView(R.id.traveller)
    TextView travellerText;
    @BindView(R.id.resetpasswordguidetext)
    TextView resetpasswdguidetext;
    @BindView(R.id.loginemail)
    EditText emaillogin;
    @BindView(R.id.loginpassword)
    EditText paswdlogin;
    @BindView(R.id.resetpasswordemail)
    EditText emailreset;
    @BindView(R.id.userusername)
    EditText usernameuser;
    @BindView(R.id.useremail)
    EditText emailuser;
    @BindView(R.id.registerusername)
    EditText usernameregister;
    @BindView(R.id.registeremail)
    EditText emailregister;
    @BindView(R.id.registerpassword)
    EditText passwdregister;
    @BindView(R.id.registerbtn)
    Button btnregister;
    @BindView(R.id.loginbtn)
    Button btnlogin;
    @BindView(R.id.resetpasswordbtn)
    Button btnresetpasswd;
    @BindView(R.id.updateprofilebtn)
    Button btnprofileupdate;
    @BindView(R.id.removeAccountbtn)
    Button btnremoveaccount;
    @BindView(R.id.forgetpasswordbtn)
    TextView btnforgetpasswd;
    @BindView(R.id.alreadyregisteredbtn)
    TextView btnalreadregister;
    @BindView(R.id.notregisteredbtn)
    TextView btnnotregister;
    @BindView(R.id.logoutbtn)
    TextView btnlogout;
    @BindView(R.id.guide)
    TextView guidetext;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ArrayList<String> mKeys = new ArrayList<>();
    private DatabaseReference mDatabase;

    public ProfileFragment() {
        //required
    }

    static boolean getConnectivityStatusString(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        OfflineData.getDatabase();
        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 != null) {
                userProfile();
                btnprofileupdate.setEnabled(false);
                usernameuser.setEnabled(true);
                emailuser.setEnabled(true);
                getUserData();
                btnprofileupdate.setEnabled(false);
                usernameuser.setEnabled(false);
                emailuser.setEnabled(false);
                btnedit.setEnabled(true);

            } else {
                userLogin();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*editbtn click*/
        btnedit.setOnClickListener(v -> {
            btnprofileupdate.setEnabled(true);
            usernameuser.setEnabled(true);
            emailuser.setEnabled(true);
            btnedit.setEnabled(false);
        });

        /*login button click*/
        btnlogin.setOnClickListener(v -> {
            String email = emaillogin.getText().toString();
            final String password = paswdlogin.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                paswdlogin.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            userProfile();
                            btnprofileupdate.setEnabled(false);
                            usernameuser.setEnabled(true);
                            emailuser.setEnabled(true);
                            getUserData();
                            btnprofileupdate.setEnabled(false);
                            usernameuser.setEnabled(false);
                            emailuser.setEnabled(false);
                            btnedit.setEnabled(true);
                        }
                    });
        });

        /*logout button click*/
        btnlogout.setOnClickListener(v -> {
            signOut();
            userLogin();
        });

        /*register button click*/
        btnregister.setOnClickListener(v -> {

            final String userName = usernameregister.getText().toString().trim();
            final String email = emailregister.getText().toString().trim();
            final String password = passwdregister.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(getContext(), "Enter UserName!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userName.length() > 20) {
                Toast.makeText(getContext(), "UserName must be less than 20 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() > 20) {
                Toast.makeText(getContext(), "Password too long, enter maximum 20 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                        Log.d(TAG, "onComplete: called");
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println(Objects.requireNonNull(task.getException()).getMessage());
                                Toast.makeText(getContext(), "Registration failed! " + "\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }

                        } else {
                            boolean success = AddDatabase(userName, email);
                            if (success) {
                                Toast.makeText(getContext(), "Successfully registered!", Toast.LENGTH_LONG).show();
                                userProfile();
                                btnprofileupdate.setEnabled(false);
                                usernameuser.setEnabled(true);
                                emailuser.setEnabled(true);
                                getUserData();
                                btnprofileupdate.setEnabled(false);
                                usernameuser.setEnabled(false);
                                emailuser.setEnabled(false);
                                btnedit.setEnabled(true);
                            } else {
                                Toast.makeText(getContext(), "Registration failed! Error Occured", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        });

        /*button profile update click*/
        btnprofileupdate.setOnClickListener(v -> {
            String username = usernameuser.getText().toString().trim();
            String useremail = emailuser.getText().toString().trim();
            String id = Objects.requireNonNull(auth.getCurrentUser()).getUid();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(getContext(), "Enter UserName!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.length() > 20) {
                Toast.makeText(getContext(), "UserName must be less than 20 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(useremail)) {
                Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = updateProfile(id, username, useremail);
            if (success) {
                Toast.makeText(getContext(), "Your Profile Updated", Toast.LENGTH_LONG).show();
                getUserData();
                btnprofileupdate.setEnabled(false);
                usernameuser.setEnabled(false);
                emailuser.setEnabled(false);
                btnedit.setEnabled(true);
            } else {
                Toast.makeText(getContext(), "Your Profile Not Updated, Please check Internet connection or retry ", Toast.LENGTH_LONG).show();
            }
        });

        /*button remove account click*/
        btnremoveaccount.setOnClickListener(v -> showDialouge());

        /*button already registered click*/
        btnalreadregister.setOnClickListener(v -> userLogin());

        /*button not registered click*/
        btnnotregister.setOnClickListener(v -> userRegister());

        /*button forget password click*/
        btnforgetpasswd.setOnClickListener(v -> userResetPassword());

        /*button reset password click*/
        btnresetpasswd.setOnClickListener(v -> {
            String email = emailreset.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "We have sent you instructions on your email to reset your password!", Toast.LENGTH_SHORT).show();
                            userLogin();
                        } else {
                            Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        });
    }

    private void userProfile() {
        btnedit.setVisibility(View.VISIBLE);
        usernameuser.setVisibility(View.VISIBLE);
        emailuser.setVisibility(View.VISIBLE);
        btnprofileupdate.setVisibility(View.VISIBLE);
        btnremoveaccount.setVisibility(View.VISIBLE);
        btnforgetpasswd.setVisibility(View.GONE);
        btnlogout.setVisibility(View.VISIBLE);
        resetpasswdguidetext.setVisibility(View.GONE);
        emaillogin.setVisibility(View.GONE);
        paswdlogin.setVisibility(View.GONE);
        emailreset.setVisibility(View.GONE);
        usernameregister.setVisibility(View.GONE);
        emailregister.setVisibility(View.GONE);
        passwdregister.setVisibility(View.GONE);
        btnregister.setVisibility(View.GONE);
        btnlogin.setVisibility(View.GONE);
        btnresetpasswd.setVisibility(View.GONE);
        btnalreadregister.setVisibility(View.GONE);
        btnnotregister.setVisibility(View.GONE);
        guidetext.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        btnprofileupdate.setEnabled(false);
        usernameuser.setEnabled(false);
        emailuser.setEnabled(false);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("Your Profile");
    }

    private void userRegister() {
        btnedit.setVisibility(View.GONE);
        usernameuser.setVisibility(View.GONE);
        emailuser.setVisibility(View.GONE);
        btnprofileupdate.setVisibility(View.GONE);
        btnremoveaccount.setVisibility(View.GONE);
        btnforgetpasswd.setVisibility(View.VISIBLE);
        btnlogout.setVisibility(View.GONE);
        resetpasswdguidetext.setVisibility(View.GONE);
        emaillogin.setVisibility(View.GONE);
        paswdlogin.setVisibility(View.GONE);
        emailreset.setVisibility(View.GONE);
        usernameregister.setVisibility(View.VISIBLE);
        usernameregister.setText("");
        emailregister.setVisibility(View.VISIBLE);
        emailregister.setText("");
        passwdregister.setVisibility(View.VISIBLE);
        passwdregister.setText("");
        btnregister.setVisibility(View.VISIBLE);
        btnlogin.setVisibility(View.GONE);
        btnresetpasswd.setVisibility(View.GONE);
        btnalreadregister.setVisibility(View.VISIBLE);
        btnnotregister.setVisibility(View.GONE);
        guidetext.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("Signup");
    }

    private void userResetPassword() {
        btnedit.setVisibility(View.GONE);
        usernameuser.setVisibility(View.GONE);
        emailuser.setVisibility(View.GONE);
        btnprofileupdate.setVisibility(View.GONE);
        btnremoveaccount.setVisibility(View.GONE);
        btnforgetpasswd.setVisibility(View.GONE);
        btnlogout.setVisibility(View.GONE);
        resetpasswdguidetext.setVisibility(View.VISIBLE);
        emaillogin.setVisibility(View.GONE);
        paswdlogin.setVisibility(View.GONE);
        emailreset.setVisibility(View.VISIBLE);
        usernameregister.setVisibility(View.GONE);
        emailregister.setVisibility(View.GONE);
        passwdregister.setVisibility(View.GONE);
        btnregister.setVisibility(View.GONE);
        btnlogin.setVisibility(View.GONE);
        btnresetpasswd.setVisibility(View.VISIBLE);
        btnalreadregister.setVisibility(View.VISIBLE);
        btnnotregister.setVisibility(View.GONE);
        guidetext.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("Reset Password");
    }

    private void userLogin() {
        btnedit.setVisibility(View.GONE);
        usernameuser.setVisibility(View.GONE);
        emailuser.setVisibility(View.GONE);
        btnprofileupdate.setVisibility(View.GONE);
        btnremoveaccount.setVisibility(View.GONE);
        btnforgetpasswd.setVisibility(View.VISIBLE);
        btnlogout.setVisibility(View.GONE);
        resetpasswdguidetext.setVisibility(View.GONE);
        emaillogin.setVisibility(View.VISIBLE);
        paswdlogin.setVisibility(View.VISIBLE);
        emailreset.setVisibility(View.GONE);
        usernameregister.setVisibility(View.GONE);
        emailregister.setVisibility(View.GONE);
        passwdregister.setVisibility(View.GONE);
        btnregister.setVisibility(View.GONE);
        btnlogin.setVisibility(View.VISIBLE);
        btnresetpasswd.setVisibility(View.GONE);
        btnalreadregister.setVisibility(View.GONE);
        btnnotregister.setVisibility(View.VISIBLE);
        guidetext.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emaillogin.setText("");
        paswdlogin.setText("");
        ((MainActivity) Objects.requireNonNull(getActivity())).updateToolbarTitle("Login");
    }

    private void showDialouge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setTitle("Are you sure?");
        builder.setMessage("Please Enter Password");
        builder.setView(input);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            final FirebaseUser user = auth.getCurrentUser();

            public void onClick(DialogInterface dialog, int which) {
                if (getConnectivityStatusString(Objects.requireNonNull(getContext()))) {
                    if (user != null) {
                        String id = user.getUid();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(Objects.requireNonNull(user.getEmail()), input.getText().toString());
                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(task -> {

                                    Log.d(TAG, "User re-authenticated.");
                                    if (task.isSuccessful()) {
                                        user.delete()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        //First delete from database
                                                        deleteProfile(id);
                                                        signOut();
                                                        Toast.makeText(getContext(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                                        userLogin();
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        Toast.makeText(getContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getContext(), " Password Is Incorrect!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                    }
                } else {
                    Toast.makeText(getContext(), " Please check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteProfile(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("RegisteredUser").child(id);//removing artist
        dR.removeValue();
        Toast.makeText(getContext(), "Profile Deleted", Toast.LENGTH_LONG).show();

    }

    private boolean updateProfile(String id, String name, String email) {
//        if (getConnectivityStatusString(Objects.requireNonNull(getContext()))) {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("RegisteredUser").child(id);
            Map<String, Object> updates = new HashMap<>();
            updates.put("UserName", name);
            updates.put("Email", email);
            dR.updateChildren(updates);
            FirebaseUser user = auth.getCurrentUser();
            Objects.requireNonNull(user).updateEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    });
            Toast.makeText(getContext(), "User Updated", Toast.LENGTH_LONG).show();
            return true;
//        } else {
//            return false;
//        }
    }

    //sign out method
    private void signOut() {
        auth.signOut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        if (auth.getCurrentUser() != null) {
            getUserData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void getUserData() {
        FirebaseUser userget = auth.getCurrentUser();
        String userKey = Objects.requireNonNull(userget).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("RegisteredUser");
        reference.orderByChild("UserId").equalTo(userKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                //Here we retrieve the key of the memo the user has.
                String key = dataSnapshot.getKey(); //for example memokey1
                mKeys.add(key);
                User user;
                user = dataSnapshot.getValue(User.class);
                String userId = Objects.requireNonNull(user).UserId;
                String email = user.Email;
                String username = user.UserName;
                usernameuser.setText(username.trim());
                emailuser.setText(email.trim());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private boolean AddDatabase(String userName, String email) {
        try {
          //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//            OfflineData.getDatabase();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser userget = firebaseAuth.getCurrentUser();
            String userKey = Objects.requireNonNull(userget).getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            User obj = new User();
            HashMap<String, String> dataMap = obj.firebaseMap();
            dataMap.put("UserId", userKey);
            dataMap.put("UserName", userName);
            dataMap.put("Email", email);
            mDatabase.child("RegisteredUser").child(userKey).setValue(dataMap);
            return true;
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error Occured." + ex.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
