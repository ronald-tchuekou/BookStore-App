package com.roncoder.bookstore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class UserMoreInfo extends AppCompatActivity {

    private static final String TAG = "UserMoreInfo";
    private ImageView profile_image;
    private TextInputLayout layout_name, layout_mail, layout_surname;
    private TextInputEditText edit_name, edit_surname, edit_mail;
    private Uri imageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_user_more_info);

        // Init views.
        ImageButton choose_image = findViewById(R.id.choose_image);
        profile_image = findViewById(R.id.profile_image);
        edit_mail = findViewById(R.id.edit_email);
        edit_name = findViewById(R.id.edit_name);
        edit_surname = findViewById(R.id.edit_surname);
        layout_mail = findViewById(R.id.edit_email_layout);
        layout_name = findViewById(R.id.edit_name_layout);
        layout_surname = findViewById(R.id.edit_surname_layout);

        // Listeners.
        choose_image.setOnClickListener(c -> pickImage());
        findViewById(R.id.submit_btn).setOnClickListener(c -> submitForm());
    }

    private void submitForm() {
        boolean name_ok = true, surname_ok = true, email_ok = true;
        if (Objects.requireNonNull(edit_name.getText()).toString().trim().equals("")) {
            layout_name.setError(getString(R.string.error_name));
            name_ok = false;
        }
        if (Objects.requireNonNull(edit_surname.getText()).toString().trim().equals("")) {
            layout_surname.setError(getString(R.string.error_surname));
            surname_ok = false;
        }
        if (Objects.requireNonNull(edit_mail.getText()).toString().trim().equals("")) {
            layout_mail.setError(getString(R.string.error_mail));
            email_ok = false;
        }
        if (name_ok && surname_ok && email_ok) {
            if (imageUri != null)
                addUser();
            else
                uploadImageProfile();
        }

    }

    private void addUser() {
        // TODO
    }

    private void uploadImageProfile() {
        // TODO
    }

    private void pickImage() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, getString(R.string.chose_app)),
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                Log.i(TAG, "Content of image uri : " + imageUri);
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null)
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    Glide.with(this)
                            .load(imageUri)
                            .into(profile_image);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    Log.e(TAG, "onActivityResult: Error where cropping the image : ", result.getError());
        }
    }

}
