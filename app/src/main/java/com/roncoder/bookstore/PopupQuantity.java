package com.roncoder.bookstore;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.roncoder.bookstore.models.Book;

public class PopupQuantity extends Dialog {
    private int number = 1;
    private TextView quantity;
    private ImageButton remove_quantity, add_quantity;
    private Book book;
    public PopupQuantity(@NonNull Context context, int themeResId, Book book) {
        super(context, themeResId);
        this.book = book;
        setContentView(R.layout.layout_popup_quantity);
        initViews();
        quantity.setText(String.valueOf(number));
        add_quantity.setOnClickListener(v -> addQuantity(1));
        remove_quantity.setOnClickListener(v -> addQuantity(-1));
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.submit).setOnClickListener(v -> {
            // TODO implement this to add commend.
            dismiss();
        });
    }

    private void addQuantity(int number) {
        this.number += number;
        if (this.number < 1)
            this.number = 1;
        else if (this.number > book.getStock_quantity())
            this.number = book.getStock_quantity();
        quantity.setText(String.valueOf(this.number));
    }

    private void initViews() {
        quantity = findViewById(R.id.quantity);
        remove_quantity = findViewById(R.id.remove_quantity);
        add_quantity = findViewById(R.id.add_quantity);
    }

}
