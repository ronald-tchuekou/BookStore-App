package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.util.Utils;

import java.util.Objects;

public class MyInformation extends AppCompatActivity {
    private static final String TAG = "MyInformation";
    private TextView user_name, login;
    private ShapeableImageView profile;
    String uId = FirebaseAuth.getInstance().getUid() == null ? "not_user" : FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user_name = findViewById(R.id.user_name);
        login = findViewById(R.id.login);
        profile = findViewById(R.id.image_profile);

        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        user_name.setText("BookStore");
        login.setText("bookstore@roncoder.com");
        UserHelper.getUserById(uId)
                .addOnCompleteListener(com -> {
                    if (!com.isSuccessful()) {
                        if (com.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        Log.e(TAG, "updateUI: ", com.getException());
                        return;
                    }
                    User user = Objects.requireNonNull(com.getResult()).toObject(User.class);
                    if (user != null) {
                        user_name.setText(user.getSurname() + " " + user.getName());
                        login.setText(user.getLogin());
                        Glide.with(this)
                                .load(user.getProfile())
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(profile);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return  true;
    }
}
