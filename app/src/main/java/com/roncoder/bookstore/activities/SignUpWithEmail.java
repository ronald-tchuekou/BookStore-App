package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.models.User;

public class SignUpWithEmail extends AppCompatActivity {
    private TextInputLayout layout_surname, layout_name, layout_email, layout_pass, layout_confirm_pass;
    private TextInputEditText edit_surname, edit_name, edit_email, edit_pass, edit_confirm_pass;
    private Button submit_btn;
    private boolean surname_ok = false, name_ok = false, email_ok = false, pass_ok = false, confirm_pass_ok = false;
    private String surname, name, mail, pass, confirm_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);
        initViews();
        surnameWatcher();
        nameWatcher();
        emailWatcher();
        passWatcher();
        confirmPassWatcher();
        submit_btn.setOnClickListener(v->submitForm());
    }
    private void submitForm() {
        User user = new User(1, name, surname,"phone", mail, "tokent", pass, "client");
        Toast.makeText(this, "Submited : " + user,
                Toast.LENGTH_SHORT).show();
        // TODO implement the submit event of this form.
    }
    private void surnameWatcher() {
        edit_surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                surname = s.toString().trim();
                if (surname.length() >= 3){
                    layout_surname.setError(null);
                    surname_ok = true;
                }
                else {
                    layout_surname.setError(getString(R.string.invalid_surname));
                    surname_ok = false;
                }
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void nameWatcher() {
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString().trim();
                if (name.length() >= 3){
                    layout_name.setError(null);
                    name_ok = true;
                }
                else {
                    layout_name.setError(getString(R.string.invalid_name));
                    name_ok = false;
                }
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void emailWatcher() {
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mail = s.toString().trim();
                if (mail.contains("@") && Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    email_ok = true;
                    layout_email.setError(null);
                }
                else {
                    layout_email.setError(getString(R.string.invalid_mail));
                    email_ok = false;
                }
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
                if (pass.length() >= 6){
                    if (confirm_pass != null){
                        if (confirm_pass.equals(pass)){
                            layout_pass.setError(null);
                            layout_confirm_pass.setError(null);
                            pass_ok = true;
                            confirm_pass_ok = true;
                        }else{
                            layout_confirm_pass.setError(getString(R.string.invalid_confirm_password));
                            confirm_pass_ok = false;
                        }
                    }
                    layout_pass.setError(null);
                    pass_ok = true;
                }
                else {
                    layout_pass.setError(getString(R.string.invalid_password));
                    pass_ok = false;
                }
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void confirmPassWatcher() {
        edit_confirm_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirm_pass = s.toString().trim();
                if (confirm_pass.equals(pass)){
                    layout_confirm_pass.setError(null);
                    confirm_pass_ok = true;
                }
                else {
                    layout_confirm_pass.setError(getString(R.string.invalid_confirm_password));
                    confirm_pass_ok = false;
                }
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
        if (surname_ok && name_ok && email_ok && pass_ok && confirm_pass_ok) {
            submit_btn.setEnabled(true);
            submit_btn.setClickable(true);
        }
        else{
            submit_btn.setEnabled(false);
            submit_btn.setClickable(false);
        }
    }
    private void initViews() {
        layout_surname = findViewById(R.id.layout_surname);
        layout_name = findViewById(R.id.layout_name);
        layout_email = findViewById(R.id.layout_email);
        layout_pass = findViewById(R.id.layout_pass);
        layout_confirm_pass = findViewById(R.id.layout_confirm_pass);
        edit_surname = findViewById(R.id.edit_surname);
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_pass = findViewById(R.id.edit_pass);
        edit_confirm_pass = findViewById(R.id.edit_confirm_pass);
        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setEnabled(false);
        submit_btn.setClickable(false);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void backListener(View view) {
        onBackPressed();
    }
    public void setUseCondition(View view) {
        Toast.makeText(this, "Conditions Générales d'utilisation de l'application.", Toast.LENGTH_SHORT).show();
    }
}
