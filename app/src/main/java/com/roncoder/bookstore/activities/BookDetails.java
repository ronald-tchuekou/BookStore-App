package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roncoder.bookstore.PopupQuantity;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.utils.Utils;

public class BookDetails extends AppCompatActivity {
    public static final String TAG = "BookDetails";
    ImageView front_image;
    TextView title, book_author, book_edition, book_state, book_prise, book_quantity;
    Button commended_btn, add_to_kilt;
    private Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        initViews();
        book = getIntent().getParcelableExtra(Utils.EXTRA_BOOK);
        Log.i(TAG, "Book : " + book);
        setViewsValue();

        commended_btn.setOnClickListener(viewClick -> Utils.commendActivity(this, book));
        add_to_kilt.setOnClickListener(viewClick -> addToCart());
    }

    private void addToCart() {
        new PopupQuantity(this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog, book)
                .show();
//        new MaterialAlertDialogBuilder(this)
//                .setView(R.layout.layout_popup_quantity)
//                .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        return false;
//                    }
//                });
    }

    private void setViewsValue() {
        // The front image.
        Glide.with(this)
                .load(book.getImage1_front())
                .placeholder(R.drawable.excellence_physique_chimie_2ndc)
                .error(R.drawable.excellence_physique_chimie_2ndc)
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
