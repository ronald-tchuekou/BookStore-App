package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class SignUpWithEmail extends AppCompatActivity {
    private static final String TAG = "SignUpWithEmail";
    private TextInputLayout layout_surname, layout_name, layout_email, layout_pass, layout_confirm_pass, layout_phone;
    private TextInputEditText edit_surname, edit_name, edit_email, edit_pass, edit_confirm_pass, edit_phone;
    private Button submit_btn;
    private boolean surname_ok = false, name_ok = false, email_ok = false, pass_ok = false, confirm_pass_ok = false, phone_ok = false;
    private String surname, name, mail, pass, confirm_pass, phone;
    private FirebaseAuth auth;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        initViews();
        surnameWatcher();
        nameWatcher();
        emailWatcher();
        phoneWatcher();
        passWatcher();
        confirmPassWatcher();
        submit_btn.setOnClickListener(v->submitForm());
    }

    private void submitForm() {
        user.setName(name);
        user.setSurname(surname);
        user.setPhone(phone);
        user.setPassword(pass);
        user.setLogin(mail);

        singUpUser();
    }

    private void singUpUser() {
        Utils.setProgressDialog(this, getString(R.string.wait_auth));
        auth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user1 = auth.getCurrentUser();
                        if (user1 != null) {
                            user.setId(user1.getUid());
                            Utils.updateDialogMessage(getString(R.string.saver_data));
                            UserHelper.addUser(user) // Add user to the data base.
                                    .addOnCompleteListener(com -> {
                                        Utils.dismissDialog();
                                        if (com.isSuccessful()) {
                                            user1.getIdToken(true).addOnSuccessListener(command
                                                    -> UserHelper.getCollectionRef()
                                                    .document(user1.getUid())
                                                    .update("token", command.getToken()));
                                            finalized(user1);
                                        }
                                        else {
                                            if (com.getException() instanceof FirebaseNetworkException)
                                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                                            else
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        }
                                    });
                        }
                    } else {
                        Utils.dismissDialog();
                        if (task.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    private void finalized(FirebaseUser user) {
        if (user != null) {
            Utils.setToastMessage(this, getString(R.string.connect_successful));
            finish();
        }
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
                    layout_email.setError(getString(R.string.invalid_email));
                    email_ok = false;
                }
                activeConBtn();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void phoneWatcher() {
        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = s.toString().trim();
                try {
                    PhoneNumberUtil numberUtil = PhoneNumberUtil.createInstance(SignUpWithEmail.this);
                    Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(phone, "CM");
                    boolean isValid = numberUtil.isValidNumber(phoneNumber);
                    if (isValid) {
                        phone_ok = true;
                        layout_phone.setError(null);
                    }
                    else {
                        layout_phone.setError(getString(R.string.invalid_phone));
                        phone_ok = false;
                    }
                } catch (NumberParseException e) {
                    Log.e(TAG, "Error when parsing the phone number : " + phone, e);
                } catch (Exception e) {
                    Log.e(TAG, "Phone parsing error : ", e);
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
        if (surname_ok && name_ok && email_ok && pass_ok && confirm_pass_ok && phone_ok) {
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
        layout_email = findViewById(R.id.edit_email_layout);
        layout_pass = findViewById(R.id.edit_pass_layout);
        layout_confirm_pass = findViewById(R.id.layout_confirm_pass);
        layout_phone = findViewById(R.id.edit_phone_layout);
        edit_surname = findViewById(R.id.edit_surname);
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_login);
        edit_pass = findViewById(R.id.edit_pass);
        edit_confirm_pass = findViewById(R.id.edit_confirm_pass);
        edit_phone = findViewById(R.id.edit_phone);
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
        Intent intent = new Intent(this, Help.class);
        intent.putExtra(Help.EXTRA_TYPE, Help.CGU);
        startActivity(intent);
    }
}
