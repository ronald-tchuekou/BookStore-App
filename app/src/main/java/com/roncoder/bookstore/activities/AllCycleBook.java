package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.BookHelper;
import com.roncoder.bookstore.fragments.Home;
import com.roncoder.bookstore.models.Book;
import com.roncoder.bookstore.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.roncoder.bookstore.util.Utils.ListTypes.DICTIONARY;
import static com.roncoder.bookstore.util.Utils.ListTypes.FIRST_CYCLE;
import static com.roncoder.bookstore.util.Utils.ListTypes.PRIMARY_CYCLE;
import static com.roncoder.bookstore.util.Utils.ListTypes.SECOND_CYCLE;

public class AllCycleBook extends AppCompatActivity {
    private static final String TAG = "AllCycleBook";
    int commend_count = 0;
    TextView cart_badge;
    GridView layout_grid;
    ImageView empty;
    ProgressBar progress;
    private Utils.ListTypes types;
    private boolean is_francophone;
    private GridAdapter adapter;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cycle_book);
        commend_count = MainActivity.commend_count;
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            types = (Utils.ListTypes) Objects.requireNonNull(extraIntent.getExtras()).get(Home.EXTRA_CYCLE);
            is_francophone = extraIntent.getBooleanExtra(Home.EXTRA_IS_FRANCOPHONE,true);
        }
        // Adapted the grid_layout vew.
        books = new ArrayList<>();
        layout_grid = findViewById(R.id.grid);
        progress = findViewById(R.id.progress);
        empty = findViewById(R.id.empty);
        adapter = new GridAdapter();
        layout_grid.setAdapter(adapter);
        managedActionbar();
    }

    /**
     * Function to get the content books.
     */
    private void setBookContent() {
        
        String cycle = "";
        if (is_francophone){
            if (types == PRIMARY_CYCLE)
                cycle = Utils.NURSERY_F;
            else if (types == FIRST_CYCLE)
                cycle = Utils.PRIMARY_F;
            else if (types == SECOND_CYCLE)
                cycle = Utils.SECONDARY_F;
        }else {
            if (types == PRIMARY_CYCLE)
                cycle = Utils.NURSERY_A;
            else if (types == FIRST_CYCLE)
                cycle = Utils.PRIMARY_A;
            else if (types == SECOND_CYCLE)
                cycle = Utils.SECONDARY_A;
        }

        progress.setVisibility(View.VISIBLE);
        if(types == DICTIONARY)
            BookHelper.getDictionaries().addSnapshotListener((value, error) -> {
                progress.setVisibility(View.GONE);
                if (error != null) {
                    Utils.setDialogMessage(this, R.string.error_has_provide);
                    Log.e(TAG, "setDictionaryContent: ", error);
                    return;
                }
                if (value != null) {
                    books.clear();
                    books.addAll(value.toObjects(Book.class));
                    if (books.isEmpty())
                        empty.setVisibility(View.VISIBLE);
                    else
                        empty.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            });
        else
            BookHelper.getBooksByCycle(cycle).addSnapshotListener((value, error) -> {
                progress.setVisibility(View.GONE);
                if (error != null) {
                    Utils.setDialogMessage(this, R.string.error_has_provide);
                    Log.e(TAG, "setNurseryContent: ", error);
                    return;
                }
                if (value != null) {
                    books.clear();
                    books.addAll(value.toObjects(Book.class));
                    if (books.isEmpty())
                        empty.setVisibility(View.VISIBLE);
                    else
                        empty.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookContent();
    }

    /**
     * Function to managed the action bar.
     */
    private void managedActionbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = getString(R.string.all_cycle_book) + " ( " + getCorrespondCycle() + " )";
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Function to get the correspond cycle type.
     * @return Value.
     */
    private String getCorrespondCycle () {
        String[] class_group = getResources().getStringArray(R.array.cycles);
        String value;
        if (is_francophone)
            if (types == PRIMARY_CYCLE) value = class_group[1];
            else if (types == FIRST_CYCLE) value = class_group[3];
            else value = class_group[5];
        else
            if (types == PRIMARY_CYCLE) value = class_group[0];
            else if (types == FIRST_CYCLE) value = class_group[2];
            else value = class_group[4];
        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kilt_option, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();
        cart_badge = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    /**
     * Function set the value off the badge.
     */
    private void setupBadge() {
        if (cart_badge != null) {
            if (commend_count == 0) {
                if (cart_badge.getVisibility() != View.GONE) {
                    cart_badge.setVisibility(View.GONE);
                }
            } else {
                cart_badge.setText(commend_count >= 100 ? 99 + "+" : commend_count + "");
                if (cart_badge.getVisibility() != View.VISIBLE) {
                    cart_badge.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent mainIntent = new Intent();
            mainIntent.putExtra(Utils.EXTRA_FRAG_TYPE, Utils.FRAG_KILT);
            setResult(RESULT_OK, mainIntent);
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Class to adapted the grid_layout elements in the grid_layout view.
     */
    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public  Book getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }

            ImageView book_image = itemView.findViewById(R.id.book_image);
            TextView book_title = itemView.findViewById(R.id.book_title);
//            TextView book_prise = itemView.findViewById(R.id.book_prise);

            Book book = books.get(position);
            String title = book.getAuthor() + " : " + book.getTitle();
//            book_prise.setText(Utils.formatPrise(book.getUnit_prise()));
            book_title.setText(title);
            Glide.with(AllCycleBook.this)
                    .load(book.getImage1_front())
                    .placeholder(R.drawable.bg_image)
                    .error(R.drawable.bg_image)
                    .into(book_image);

//            Button buy = itemView.findViewById(R.id.buy_button);
//            buy.setOnClickListener(view -> Utils.commendActivity(AllCycleBook.this, book));
            itemView.setOnClickListener(view -> Utils.bookDetail(AllCycleBook.this, books.get(position),
                    book_image, Home.TRANSITION_IMAGE_NAME));
            return itemView;
        }
    }
}

