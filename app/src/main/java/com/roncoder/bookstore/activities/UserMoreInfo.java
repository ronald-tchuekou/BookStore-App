package com.roncoder.bookstore.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class UserMoreInfo extends AppCompatActivity {

    StorageReference storage = FirebaseStorage.getInstance().getReference("User_Profiles");

    private static final String TAG = "UserMoreInfo";
    private ImageView profile_image;
    private TextInputLayout layout_name, layout_mail, layout_surname;
    private TextInputEditText edit_name, edit_surname, edit_mail;
    private String name, surname, mail;
    private Uri imageUri;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        name = Objects.requireNonNull(edit_name.getText()).toString().trim();
        mail = Objects.requireNonNull(edit_mail.getText()).toString().trim();
        surname = Objects.requireNonNull(edit_surname.getText()).toString().trim();
        // Check validation of name
        if (name.equals("") || name.length() < 3) {
            layout_name.setError(getString(R.string.error_name));
            name_ok = false;
        } else
            layout_name.setError(null);
        // Check validation of surname
        if (surname.equals("") || surname.length() < 3) {
            layout_surname.setError(getString(R.string.error_surname));
            surname_ok = false;
        } else
            layout_surname.setError(null);
        // Check validation of mail
        if (mail.equals("")  || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            layout_mail.setError(getString(R.string.error_mail));
            email_ok = false;
        } else
            layout_mail.setError(null);

        // Save if all is validate
        if (name_ok && surname_ok && email_ok) {
            if (imageUri == null) {
                Utils.setProgressDialog(this, getString(R.string.saver_data));
                addUser(null);
            } else
                uploadImageProfile();
        }

    }

    private void addUser(String imageUri) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setLogin(mail);
        user.setPhone(Objects.requireNonNull(firebaseUser).getPhoneNumber());
        user.setId(firebaseUser.getUid());
        user.setProfile(imageUri == null ? "not image" : imageUri);

        firebaseUser.getIdToken(true).addOnCompleteListener(com -> {
            Utils.dismissDialog();
           if (com.isSuccessful()) {
                user.setToken(Objects.requireNonNull(com.getResult()).getToken());
               UserHelper.addUser(user).addOnCompleteListener(command -> {
                    Utils.setToastMessage(this, getString(R.string.connect_successful));
                    finish();
               });
           } else {
               if (com.getException() instanceof FirebaseNetworkException)
                   Utils.setDialogMessage(this, R.string.network_not_allowed);
               Log.e(TAG, "User token info : ", com.getException());
           }
        });
    }

    private void uploadImageProfile() {
        if (imageUri != null) {
            String imagePath = System.currentTimeMillis() + Utils.getFileExtension(imageUri, this);
            StorageReference fileReference = storage.child(imagePath);
            UploadTask uploadTask = fileReference.putFile(imageUri);
            Utils.setProgressDialog(this, getString(R.string.saver_image_profile));
            uploadTask.addOnFailureListener(e -> {
                Utils.dismissDialog();
                if (e instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                Log.e(TAG, "Image profile is fail : ", e);
            }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Utils.updateDialogMessage(getString(R.string.saver_data));
                        addUser(uri.toString());
                    }).addOnFailureListener(e -> {
                        if (e instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        Log.e(TAG, "Image uri is fail : ", e);
                    }));
        }
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
                Uri imageUri = data.getData();
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
