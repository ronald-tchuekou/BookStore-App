package com.roncoder.bookstore.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.ClassHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBook extends AppCompatActivity {

    StorageReference storage = FirebaseStorage.getInstance().getReference("Book_images");

    private static final String TAG = "AddBook";
    private ImageView front_image;
    private ImageButton pick_image;
    private EditText edit_title, edit_author, edit_editor, edit_state, edit_prise, edit_quantity;
    private AutoCompleteTextView edit_class, edit_cycle;
    private Button submit_btn;
    private List<CharSequence> classesList = new ArrayList<>();
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        initViews();
        submit_btn.setOnClickListener(v -> submitForm());
        pick_image.setOnClickListener(v -> pick_image());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }

    private void pick_image() {
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
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3, 4)
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
                            .into(front_image);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    Log.e(TAG, "onActivityResult: Error where cropping the image : ", result.getError());
        }
    }

    private void submitForm() {

        boolean title_ok = true, author_ok = true, editor_ok = true, state_ok = true, class_ok = true,
                cycle_ok = true, prise_ok = true, quantity_ok = true;

        String title = Objects.requireNonNull(edit_title.getText()).toString().trim();
        String author = Objects.requireNonNull(edit_author.getText()).toString().trim();
        String editor = Objects.requireNonNull(edit_editor.getText()).toString().trim();
        String state = Objects.requireNonNull(edit_state.getText()).toString().trim();
        String classes = Objects.requireNonNull(edit_class.getText()).toString().trim();
        String cycle = Objects.requireNonNull(edit_cycle.getText()).toString().trim();
        String prise = Objects.requireNonNull(edit_prise.getText()).toString().trim();
        String quantity = Objects.requireNonNull(edit_quantity.getText()).toString().trim();

        Book book = new Book("", title, author, editor, "book image", state, classes,
                cycle, Float.parseFloat(prise), Integer.parseInt(quantity));

        // Check validation of title
        if (title.equals("")) {
            edit_title.setError(getString(R.string.error_this_field_can_be_empty));
            title_ok = false;
        } else
            edit_title.setError(null);

        // Check validation of author
        if (author.equals("")) {
            edit_author.setError(getString(R.string.error_this_field_can_be_empty));
            author_ok = false;
        } else
            edit_author.setError(null);

        // Check validation of editor
        if (editor.equals("")) {
            edit_editor.setError(getString(R.string.error_this_field_can_be_empty));
            editor_ok = false;
        } else
            edit_editor.setError(null);

        // Check validation of state
        if (state.equals("")) {
            edit_state.setError(getString(R.string.error_this_field_can_be_empty));
            state_ok = false;
        } else
            edit_state.setError(null);

        // Check validation of class
        if (classes.equals("")) {
            edit_class.setError(getString(R.string.error_this_field_can_be_empty));
            class_ok = false;
        } else
            edit_class.setError(null);

        // Check validation of cycle
        if (cycle.equals("")) {
            edit_cycle.setError(getString(R.string.error_this_field_can_be_empty));
            cycle_ok = false;
        } else
            edit_cycle.setError(null);

        // Check validation of prise
        if (prise.equals("")) {
            edit_prise.setError(getString(R.string.error_this_field_can_be_empty));
            prise_ok = false;
        } else
            edit_prise.setError(null);

        // Check validation of quantity
        if (quantity.equals("")) {
            edit_quantity.setError(getString(R.string.error_this_field_can_be_empty));
            quantity_ok = false;
        } else
            edit_quantity.setError(null);

        // Save if all is validate
        if (title_ok && author_ok && editor_ok && state_ok && class_ok && cycle_ok && prise_ok && quantity_ok) {
            if (imageUri == null) {
                Utils.setProgressDialog(this, getString(R.string.saver_data));
                addBook(book);
            } else
                uploadBookImage(book);
        }

    }

    private void uploadBookImage(Book book) {
        if (imageUri != null) {
            String imagePath = System.currentTimeMillis() + Utils.getFileExtension(imageUri, this);
            StorageReference fileReference = storage.child(imagePath);
            UploadTask uploadTask = fileReference.putFile(imageUri);
            Utils.setProgressDialog(this, getString(R.string.saver_image_profile));
            uploadTask.addOnFailureListener(e -> {
                Utils.dismissDialog();
                if (e instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                else
                    Log.e(TAG, "Error: ", e);
            }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Utils.updateDialogMessage(getString(R.string.saver_data));
                        book.setImage1_front(uri.toString());
                        addBook(book);
                    }).addOnFailureListener(e -> {
                        if (e instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Log.e(TAG, "Error: ", e);
                    }));
        }
    }

    private void addBook(Book book) {
        BookHelper.addBook (book).addOnCompleteListener(com -> {
            if (!com.isSuccessful()) { // When this request is success happen.
                if (com.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                else
                    Log.e(TAG, "Error: ", com.getException());
                return;
            }
            String id = Objects.requireNonNull(com.getResult()).getId();
            BookHelper.getCollectionRef().document(id).update("id", id) // Set an id to this book.
                    .addOnCompleteListener(command -> {
                        Utils.dismissDialog();
                        if (!com.isSuccessful()) {
                            if (com.getException() instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                            else
                                Log.e(TAG, "Error: ", com.getException());
                            return;
                        }
                        Utils.setToastMessage(this, getString(R.string.add_successful));
                    });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ClassHelper.getAllClass().addSnapshotListener((value, error) -> {
            if (error != null) {
                    Log.e(TAG, "Error: ", error);
                return;
            }
            if (value != null) {
                classesList.clear();
                List<Classes> classes = value.toObjects(Classes.class);
                for (Classes c : classes)
                    classesList.add(c.getName());
                ArrayAdapter<CharSequence> adapter_class = new ArrayAdapter<>(this,
                        R.layout.cat_exposed_dropdown_popup_item, classesList);
                edit_class.setAdapter(adapter_class);
            }
        });
    }

    private void initViews() {
        front_image = findViewById(R.id.front_image);
        pick_image = findViewById(R.id.pick_image);
        edit_title = findViewById(R.id.edit_title);
        edit_author = findViewById(R.id.edit_author);
        edit_editor = findViewById(R.id.edit_editor);
        edit_state = findViewById(R.id.edit_state);
        edit_class = findViewById(R.id.edit_class);
        edit_cycle = findViewById(R.id.edit_cycle);
        edit_prise = findViewById(R.id.edit_prise);
        edit_quantity = findViewById(R.id.edit_quantity);
        submit_btn = findViewById(R.id.submit_btn);

        ArrayAdapter<CharSequence> adapter_cycle = new ArrayAdapter<>(this,
                R.layout.cat_exposed_dropdown_popup_item, getResources().getStringArray(R.array.cycles));
        edit_cycle.setAdapter(adapter_cycle);
    }
}