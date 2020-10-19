package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.util.Utils;

import java.util.Objects;

public class Login extends AppCompatActivity {

    // Field Views.
    private TextView text_error, btn_subscribe;
    private TextInputEditText edit_login, edit_pass;
    private Button validate;
    private ProgressBar progressBar;

    // Contents varaibles.
    private String login, pass;

    // Firebase Elements
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // init veiws.
        initViews ();

        // Listeners.
        setListeners();
    }

    private void setListeners() {
        validate.setOnClickListener(v -> submit());
        btn_subscribe.setOnClickListener(v -> {
            // Set to the subscribe form.
            startActivity(new Intent(this, SignUpWithEmail.class));
        });
    }

    private void submit() {
        if(!validate())
            return;
        validate.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(login, pass).addOnCompleteListener(com -> {
            validate.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            // If error.
            if (!com.isSuccessful()) {
               Exception e = com.getException();
               if (e instanceof FirebaseNetworkException)
                   Utils.setDialogMessage(this, R.string.network_not_allowed);
               else if (e instanceof FirebaseNoSignedInUserException || 
                            e instanceof FirebaseAuthInvalidUserException)
                   text_error.setVisibility(View.VISIBLE);
               else Utils.setToastMessage(this, getString(R.string.error_has_provide));
               Log.e("Login", "submit: Error =>", e);
           }

            Utils.setToastMessage(this, getString(R.string.connect_successful));
            onBackPressed();
        });
    }

    private boolean validate() {
        login = Objects.requireNonNull(edit_login.getText()).toString().trim();
        pass = Objects.requireNonNull(edit_pass.getText()).toString().trim();
        boolean is_valid = true;

        // Email or phone.
        if (login.equals("")) {
            edit_login.setError(getString(R.string.error_this_field_can_be_empty));
            is_valid = false;
        }
        // password.
        if (pass.equals("")) {
            edit_pass.setError(getString(R.string.error_this_field_can_be_empty));
            is_valid = false;
        }
        return is_valid;
    }

    private void initViews() {
        text_error = findViewById(R.id.text_error);
        btn_subscribe = findViewById(R.id.btn_subscribe);
        edit_login = findViewById(R.id.edit_login);
        edit_pass = findViewById(R.id.edit_pass);
        validate = findViewById(R.id.validate);
        progressBar = findViewById(R.id.progressBar);
    }

    public void backListener(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}