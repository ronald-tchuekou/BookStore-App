package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.roncoder.bookstore.R;

import java.util.Objects;

public class Help extends AppCompatActivity {

    public static final String EXTRA_TYPE = "Extra_type";
    public static final String HELP = "Help_type";
    public static final String CGU = "CGU_type";

    private boolean is_help = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        is_help = Objects.equals(getIntent().getStringExtra(EXTRA_TYPE), HELP);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(is_help ? R.string.help : R.string.cgu);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return  true;
    }
}
