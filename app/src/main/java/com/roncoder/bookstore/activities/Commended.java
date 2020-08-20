package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.utils.Utils;

public class Commended extends AppCompatActivity {
    Book book;
    TextView book_title, book_author, book_prise, book_edition, total_prise, book_quantity;
    ImageView front_image;
    ImageButton remove_quantity, add_quantity;
    Button finish;
    private int number = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commended);
        // Get book.
        book = getIntent().getParcelableExtra(Utils.EXTRA_BOOK);
        initViews();
        setViewValue();
        finish.setOnClickListener(v -> setTheCommend());
        add_quantity.setOnClickListener(v -> addQuantity(1));
        remove_quantity.setOnClickListener(v -> addQuantity(-1));
    }
    private void updateTotalPrise (){
        book_quantity.setText(String.valueOf(number));
        double totalPrise = number * book.getUnit_prise();
        total_prise.setText(Utils.formatPrise(totalPrise));
    }
    private void setTheCommend() {
        // TODO implement the validation of the commend.
        onBackPressed();
    }

    private void addQuantity(int number) {
        this.number += number;
        if (this.number < 1)
            this.number = 1;
        else if (this.number > book.getStock_quantity())
            this.number = book.getStock_quantity();

        updateTotalPrise();
    }

    private void setViewValue() {
        String edition = "Edition : " + book.getEditor();
        book_title.setText(book.getTitle());
        book_author.setText(book.getAuthor());
        book_edition.setText(edition);
        book_prise.setText(Utils.formatPrise(book.getUnit_prise()));
//        book_quantity.setText(String.valueOf(0));
//        total_prise.setText(Utils.formatPrise(totalPrise));
        updateTotalPrise();
        Glide.with(this)
                .load(book.getImage1_front())
                .placeholder(R.drawable.bg_image)
                .error(R.drawable.bg_image)
                .into(front_image);
    }

    private void initViews() {
        book_title = findViewById(R.id.book_title);
        book_author = findViewById(R.id.book_author);
        book_prise = findViewById(R.id.book_prise);
        book_edition = findViewById(R.id.book_edition);
        total_prise = findViewById(R.id.total_prise);
        front_image = findViewById(R.id.front_image);
        remove_quantity = findViewById(R.id.remove_quantity);
        add_quantity = findViewById(R.id.add_quantity);
        finish = findViewById(R.id.finish);
        book_quantity = findViewById(R.id.book_quantity);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    public void backListener(View view) {
        onBackPressed();
    }
}