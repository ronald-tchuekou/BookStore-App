package com.roncoder.bookstore.administration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.dbHelpers.ClassHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Classes;
import com.roncoder.bookstore.util.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.roncoder.bookstore.activities.Administration.BOOK_TYPE;

public class AddBook extends AppCompatActivity {

    StorageReference storage;

    private static final String TAG = "AddBook";
    private ImageView front_image;
    private ImageButton pick_image;
    private TextView label_state;
    private EditText edit_title, edit_author, edit_editor, edit_state, edit_prise, edit_quantity;
    private TextInputLayout edit_class_layout, edit_cycle_layout;
    private AutoCompleteTextView edit_class, edit_cycle;
    private Button submit_btn;
    private boolean is_book = true;
    private final List<CharSequence> classesList = new ArrayList<>();
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        initViews();
        is_book = getIntent().getBooleanExtra(BOOK_TYPE, false);
        if (is_book)
            storage = FirebaseStorage.getInstance().getReference("Book_images");
        else
            storage = FirebaseStorage.getInstance().getReference("Dictionaries_images");
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

        boolean all_is_valid = true;

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
            all_is_valid = false;
        }

        // Check validation of author
        if (author.equals("")) {
            edit_author.setError(getString(R.string.error_this_field_can_be_empty));
            all_is_valid = false;
        }

        // Check validation of editor
        if (editor.equals("")) {
            edit_editor.setError(getString(R.string.error_this_field_can_be_empty));
            all_is_valid = false;
        }

        // Check validation of state
        if (state.equals("")) {
            edit_state.setError(getString(R.string.error_this_field_can_be_empty));
            all_is_valid = false;
        }

        if(is_book) {
            // Check validation of class
            if (classes.equals("")) {
                edit_class.setError(getString(R.string.error_this_field_can_be_empty));
                all_is_valid = false;
            }

            // Check validation of cycle
            if (cycle.equals("")) {
                edit_cycle.setError(getString(R.string.error_this_field_can_be_empty));
                all_is_valid = false;
            }
        }

        // Check validation of prise
        if (prise.equals("")) {
            edit_prise.setError(getString(R.string.error_this_field_can_be_empty));
            all_is_valid = false;
        }

        // Check validation of quantity
        if (quantity.equals("")) {
            edit_quantity.setError(getString(R.string.error_this_field_can_be_empty));
            all_is_valid = false;
        }

        // Save if all is validate
        if (all_is_valid) {
            Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
            getElementType(book).addOnCompleteListener(com -> {
                if (!com.isSuccessful()) {
                    Utils.dismissDialog();
                    if (com.getException() instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    else
                        Utils.setToastMessage(this, getString(R.string.failled));
                    return;
                }
                if (!is_uniqueTitle(title, Objects.requireNonNull(com.getResult()).toObjects(Book.class))) {
                    if (imageUri == null) {
                        Utils.updateDialogMessage(getString(R.string.saver_data));
                        addBook(book);
                    } else
                        uploadBookImage(book);
                } else{
                    Utils.dismissDialog();
                    if (is_book)
                        Utils.setToastMessage(this, getString(R.string.book_allready_exist));
                    else
                        Utils.setToastMessage(this, getString(R.string.dictionary_allready_exist));
                }
            });
        }
    }

    private Task<QuerySnapshot> getElementType(Book book) {
        if(is_book)
            return BookHelper.getBookByTitle (book.getTitle());
        else
            return BookHelper.getDictionaryByTitle(book.getTitle());
    }

    private boolean is_uniqueTitle(String title, List<Book> books) {
        for (Book b : books)
            if (b.getTitle().contains(title))
                return true;
        return false;
    }

    private void uploadBookImage(Book book) {
        if (imageUri != null) {
            String imagePath = System.currentTimeMillis() + Utils.getFileExtension(imageUri, this);
            StorageReference fileReference = storage.child(imagePath);
            UploadTask uploadTask = fileReference.putFile(imageUri);
            Utils.updateDialogMessage(getString(R.string.saver_image_profile));
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
                        Utils.dismissDialog();
                        if (e instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Log.e(TAG, "Error: ", e);
                    }));
        }
    }

    private void addBook(Book book) {
        getHelperType(book).addOnCompleteListener(com -> {
            if (!com.isSuccessful()) { // When this request is success happen.
                Utils.dismissDialog();
                if (com.getException() instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                else
                    Log.e(TAG, "Error: ", com.getException());
                return;
            }
            String id = Objects.requireNonNull(com.getResult()).getId();
            getCollectionType().document(id).update("id", id) // Set an id to this book.
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

    private CollectionReference getCollectionType() {
        if(is_book)
            return BookHelper.getCollectionRef();
        else
            return BookHelper.getDictionariesColRef();
    }

    private Task<DocumentReference> getHelperType(Book book) {
        if (is_book)
            return BookHelper.addBook (book);
        else
            return BookHelper.addDictionary(book);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the element to add is dictionary.
        if (!is_book) {
            edit_class_layout.setVisibility(View.GONE);
            edit_cycle_layout.setVisibility(View.GONE);
            label_state.setText(R.string.dictionary_state);
            edit_state.setHint(R.string.dictionary_state);
        }
        else {
            edit_class_layout.setVisibility(View.VISIBLE);
            edit_cycle_layout.setVisibility(View.VISIBLE);
            label_state.setText(R.string.state);
            edit_state.setHint(R.string.state);
            ClassHelper.getAllClass().addSnapshotListener((value, error) -> {
                if (error != null) {
                    Utils.setToastMessage(this, getString(R.string.failled));
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
    }

    private void initViews() {
        front_image = findViewById(R.id.front_image);
        pick_image = findViewById(R.id.pick_image);
        edit_title = findViewById(R.id.edit_title);
        edit_author = findViewById(R.id.edit_author);
        edit_editor = findViewById(R.id.edit_editor);
        label_state = findViewById(R.id.label_state);
        edit_state = findViewById(R.id.edit_state);
        edit_class = findViewById(R.id.edit_class);
        edit_cycle = findViewById(R.id.edit_cycle);
        edit_prise = findViewById(R.id.edit_prise);
        edit_class_layout = findViewById(R.id.edit_class_layout);
        edit_cycle_layout = findViewById(R.id.edit_cycle_layout);
        edit_quantity = findViewById(R.id.edit_quantity);
        submit_btn = findViewById(R.id.submit_btn);

        ArrayAdapter<CharSequence> adapter_cycle = new ArrayAdapter<>(this,
                R.layout.cat_exposed_dropdown_popup_item, getResources().getStringArray(R.array.cycles));
        edit_cycle.setAdapter(adapter_cycle);
    }
}