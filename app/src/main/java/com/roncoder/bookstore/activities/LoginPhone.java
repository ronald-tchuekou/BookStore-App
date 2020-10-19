package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hbb20.CountryCodePicker;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.util.Utils;

import java.util.List;

public class LoginPhone extends AppCompatActivity {

    // Constants
    public  static final String PHONE_NUMBER = "phone_number";
    public static final String IS_USER = "user";

    // Views
    private CountryCodePicker ccp;
    private EditText phone_number;
    private Button validate;
    private String phone_val;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        // Initialization of views.
        ccp = findViewById(R.id.ccp);
        phone_number = findViewById(R.id.phone_number);
        validate = findViewById(R.id.validate);
        progressBar = findViewById(R.id.progressBar);

        // Listeners.
        setListener();
    }

    private void setListener() {
        validate.setOnClickListener(v -> submit());
    }

    private void submit() {
        if(checkValues()) {
            phone_number.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            validate.setEnabled(false);
            UserHelper.getUserByPhone(phone_val).addOnCompleteListener(com -> {
                if (!com.isSuccessful()) {
                    validate.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Exception e = com.getException();
                    if (e instanceof FirebaseNetworkException)
                        Utils.setDialogMessage(this, R.string.network_not_allowed);
                    else
                        Utils.setDialogMessage(this, R.string.error_has_provide);
                    Log.e("LoginPhone", "submit: Error => ", e);
                }

                if (com.getResult() != null) {
                    List<DocumentSnapshot> documents = com.getResult().getDocuments();
                    boolean is_empty = !documents.isEmpty();
                    Intent confirm = new Intent(this, PhoneConfirm.class);
                    confirm.putExtra(IS_USER, is_empty);
                    confirm.putExtra(PHONE_NUMBER, phone_val);
                    startActivity(confirm);
                    finish();
                }
            });

        }else{
            phone_number.setError(getString(R.string.error_phone));
        }
    }

    private boolean checkValues() {
        ccp.registerCarrierNumberEditText(phone_number);
        phone_val = ccp.getFullNumberWithPlus();
        return phone_number.getText().toString().trim().length() >= 9;
    }
}