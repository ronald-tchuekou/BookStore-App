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
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.dbHelpers.CommendHelper;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        commended_btn.setOnClickListener(viewClick -> {
            Utils.commendActivity(this, book);
            finish();
        });

        CommendHelper.userHasCommendThis(Utils.getCurrentUser().getId(), book.getId())
                .enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Result result = response.body();

                if (result == null)
                    return;

                if (result.getError()) {
                    Utils.setDialogMessage(BookDetails.this, result.getMessage());
                    Log.e(TAG, "Error process : " + result.getMessage(), null);
                }
                else if (result.getSuccess()) {
                    boolean state = Boolean.parseBoolean(result.getValue());
                    if (state){
                        commended_btn.setEnabled(false);
                        add_to_kilt.setEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(BookDetails.this, t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
        add_to_kilt.setOnClickListener(viewClick -> addToCart());
    }

    private void addToCart() {
        PopupQuantity popupQuantity = new PopupQuantity(this,
                R.style.Theme_MaterialComponents_Light_BottomSheetDialog, book);
        popupQuantity.setOnSubmitListener((quantity, dialog) -> Utils.addToCart(this,
                new Commend(0, book, quantity, Calendar.getInstance().getTime(),
                false, false)).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Result result = response.body();
                if (result.getError()) {
                    Utils.setDialogMessage(BookDetails.this, result.getMessage());
                    Log.e(TAG, "Error process : " + result.getMessage(), null);
                }
                else if (result.getSuccess()) {
                    Utils.setToastMessage(BookDetails.this, getString(R.string.add_successful));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.setDialogMessage(BookDetails.this, t.getMessage());
                Log.e(TAG, "onFailure: " + call, t);
            }
        }));
        popupQuantity.show();
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
