package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.roncoder.bookstore.PopupQuantity;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import java.util.Calendar;
import java.util.Objects;

public class BookDetails extends AppCompatActivity {
    public static final String TAG = "BookDetails";
    ImageView front_image;
    TextView title, book_author, book_edition, book_state, book_prise, book_quantity;
    Button commended_btn, add_to_kilt;
    private Book book;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        // initialisation of views.
        initViews();
        book = getIntent().getParcelableExtra(Utils.EXTRA_BOOK);
        Log.i(TAG, "Book : " + book);
        setViewsValue();

        commended_btn.setOnClickListener(viewClick -> {
            Utils.commendActivity(this, book);
            finish();
        });

        add_to_kilt.setOnClickListener(viewClick -> addToCart());
    }

    private void addToCart() {
        PopupQuantity popupQuantity = new PopupQuantity(this,
                R.style.Theme_MaterialComponents_Light_BottomSheetDialog, book);
        popupQuantity.setOnSubmitListener((quantity, dialog) -> {
            Utils.setProgressDialog(this, getString(R.string.saver_data));
            Task<DocumentReference> task = Utils.addToCart(this, new Commend(book.getId(), firebaseUser.getUid(),
                    quantity, Calendar.getInstance().getTime(), quantity * book.getUnit_prise()));
            if (task != null)
                task.addOnCompleteListener(com -> {
                        if (com.isSuccessful()) {
                            String id = Objects.requireNonNull(com.getResult()).getId();
                            CommendHelper.getCollectionRef().document(id).update("id", id).addOnCompleteListener(command -> {
                                Utils.dismissDialog();
                                Utils.setCmdCountBroadcast(1, false, this);
                                Utils.setToastMessage(BookDetails.this, getString(R.string.add_successful));
                                finish();
                            });
                        } else {
                            if (com.getException() instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                            else
                                Utils.setToastMessage(this, getString(R.string.error_has_provide));
                            Log.e(TAG, "Error where add commend to cart", com.getException());
                        }
                    });
                }
        );
        popupQuantity.show();
    }

    private void setViewsValue() {
        // The front image.
        Glide.with(this)
                .load(book.getImage1_front())
                .placeholder(R.drawable.bg_image)
                .error(R.drawable.bg_image)
                .into(front_image);
        title.setText(book.getTitle());
        book_author.setText(book.getAuthor());
        book_edition.setText(book.getEditor());
        book_state.setText(book.getBook_state());
        String quantity = "" + book.getStock_quantity();
        book_prise.setText(Utils.formatPrise(book.getUnit_prise()));
        book_quantity.setText(quantity);
    }

    private void initViews() {
        front_image = findViewById(R.id.front_image);
        title = findViewById(R.id.title);
        book_author = findViewById(R.id.book_author);
        book_edition = findViewById(R.id.book_edition);
        book_state = findViewById(R.id.book_state);
        book_prise = findViewById(R.id.book_prise);
        book_quantity = findViewById(R.id.book_quantity);
        commended_btn = findViewById(R.id.commended_btn);
        add_to_kilt = findViewById(R.id.add_to_kilt);
    }

    public void comeback(View view) {
        onBackPressed();
    }

}
