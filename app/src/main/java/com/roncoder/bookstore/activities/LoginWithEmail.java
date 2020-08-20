package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.roncoder.bookstore.R;

public class LoginWithEmail extends AppCompatActivity {
    private TextInputEditText edit_email, edit_pass;
    private TextView text_error;
    private Button submit_btn;
    private boolean login_ok = false, pass_ok = false;
    private String login, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);
        initViews();
        emailWatcher();
        passWatcher();
        submit_btn.setOnClickListener(c -> submitForm());
        text_error.setVisibility(View.GONE);
    }
    private void submitForm() {
        Toast.makeText(this, "Submited : email = " + login + ", pass = " + pass,
                Toast.LENGTH_SHORT).show();
        // TODO implement the submit event of this form.

    }
    private void emailWatcher() {
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login = s.toString().trim();
                login_ok = login.length() > 0;
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void passWatcher() {
        edit_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pass = s.toString().trim();
                pass_ok = pass.length() > 0;
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    /**
     * Function to activate the connection button.
     */
    private void activeConBtn() {
        if (login_ok && pass_ok) {
            submit_btn.setEnabled(true);
            submit_btn.setClickable(true);
        }
        else{
            submit_btn.setEnabled(false);
            submit_btn.setClickable(false);
        }
    }
    private void initViews() {
        edit_email = findViewById(R.id.edit_email);
        edit_pass = findViewById(R.id.edit_pass);
        submit_btn = findViewById(R.id.submit_btn);
        text_error = findViewById(R.id.text_error);
        submit_btn.setEnabled(false);
        submit_btn.setClickable(false);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void backListener(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void passForgot(View view) {
        Toast.makeText(this, "Mode de recupération du mot de passe.", Toast.LENGTH_SHORT).show();
    }
}
