package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.util.Utils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class LoginWithEmail extends AppCompatActivity {
    private static final String TAG = "LoginWithEmail";
    private TextInputLayout edit_email_layout, edit_pass_layout;
    private TextInputEditText edit_email, edit_pass;
    private TextView text_error;
    private Button submit_btn, btn_login_mode;
    private boolean login_ok = false, pass_ok = false, phone_is_valid = false;
    private String login, pass;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean is_phone_type = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback;
    private  AlertDialog codeAlert;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);
        initViews();
        emailWatcher();
        passWatcher();
        auth.useAppLanguage();

        // Button submit
        submit_btn.setOnClickListener(c -> submitForm());
        btn_login_mode.setOnClickListener(c -> toggleLoginTyp());
        text_error.setVisibility(View.GONE);

        //Phone verification callback.
        verificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                if (codeAlert != null) codeAlert.dismiss();
                verifyCredential(phoneAuthCredential);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (codeAlert != null) codeAlert.dismiss();

                Log.w(TAG, "phone verification failed : ", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException)
                    Utils.setDialogMessage(LoginWithEmail.this, getString(R.string.error_code_verification));
                else if (e instanceof FirebaseNetworkException)
                    Utils.setDialogMessage(LoginWithEmail.this, getString(R.string.network_not_allowed));
            }
            @Override
            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken fToken) {
                super.onCodeSent(id, fToken);
                verificationId = id;
                Utils.setToastMessage(LoginWithEmail.this, getString(R.string.sms_code_send));
            }
        };
    }

    /**
     * Function qui permet de choisir le type d'authentification.
     */
    private void toggleLoginTyp() {
        if (is_phone_type) {
            is_phone_type = false;
            btn_login_mode.setText(R.string.login_with_phone);
            if (edit_pass_layout.getVisibility() == View.GONE)
                edit_pass_layout.setVisibility(View.VISIBLE);
            edit_email_layout.setHint(getString(R.string.enter_your_email));
            edit_email.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            findViewById(R.id.forgot_pass).setVisibility(View.VISIBLE);
        } else {
            is_phone_type = true;
            btn_login_mode.setText(R.string.login_with_email);
            if (edit_pass_layout.getVisibility() == View.VISIBLE)
                edit_pass_layout.setVisibility(View.GONE);
            edit_email_layout.setHint(getString(R.string.enter_your_phone));
            edit_email.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            findViewById(R.id.forgot_pass).setVisibility(View.GONE);
        }
        edit_email.getEditableText().clear();
        edit_pass.getEditableText().clear();
    }

    /**
     * Function that verify the credential.
     * @param phoneAuthCredential credential.
     */
    private void verifyCredential(PhoneAuthCredential phoneAuthCredential) {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                FirebaseUser user1 = auth.getCurrentUser();
                if (user1 != null) {
                    Utils.updateDialogMessage(getString(R.string.verification));
                    user1.getUid();
                    UserHelper.getUserById(user1.getUid())
                            .addOnCompleteListener(com -> {
                                Utils.dismissDialog();
                                if (com.isSuccessful()) {
                                    if (Objects.requireNonNull(com.getResult()).toObject(User.class) == null)
                                        getMoreInfoAboutUser();
                                    else {
                                        Utils.setToastMessage(this, getString(R.string.connect_successful));
                                        finish();
                                    }
                                }else{
                                    Utils.dismissDialog();
                                    Log.e(TAG, "Get user info : ", command.getException());
                                }
                            });
                }else {
                    Utils.dismissDialog();
                    Utils.setDialogMessage(this, getString(R.string.error_has_provide));
                }
            }else{
                Utils.dismissDialog();
                Log.e(TAG, "Verify Credential : ", command.getException());
            }
        });
    }

    /**
     * Function to get more information about current user.
     */
    private void getMoreInfoAboutUser() {
        startActivity(new Intent(this, UserMoreInfo.class));
        finish();
    }

    private void sendSmsVerificationCode () {
        PhoneAuthProvider.getInstance(FirebaseAuth.getInstance()).verifyPhoneNumber(login, 60, TimeUnit.SECONDS, this,
                verificationCallback
        );
        codeAlert = new MaterialAlertDialogBuilder(this, R.style.AppTheme_Dialog)
                .setView(R.layout.layout_pass_forgot_sms_verification_code)
                .setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    AlertDialog alert = (AlertDialog) dialog;
                    EditText editText = alert.findViewById(R.id.edit_code);
                    assert editText != null;
                    String code = Objects.requireNonNull(editText.getText()).toString();

                    if (!code.trim().equals("")) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        verifyCredential(credential);
                    } else {
                        editText.setError(getString(R.string.invalid_code));
                    }

                })
                .show();
    }

    private void submitForm() {
        if (is_phone_type){
            if (!phone_is_valid) {
                text_error.setText(R.string.invalid_phone);
                return;
            }
            sendSmsVerificationCode();
        }
        else{
            Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
            auth.signInWithEmailAndPassword(login, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            finalized(user);
                        } else {
                            if (task.getException() instanceof FirebaseNetworkException)
                                Utils.setDialogMessage(this, R.string.network_not_allowed);
                            else if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                text_error.setVisibility(View.VISIBLE);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        Utils.dismissDialog();
                    });
        }
    }

    private void finalized(FirebaseUser user) {
        if (user != null) {
            Utils.setToastMessage(this, getString(R.string.connect_successful));
            finish();
        }
    }

    private void emailWatcher() {
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login = s.toString().trim();
                login_ok = login.length() > 0;
                if (is_phone_type && login.length() > 5) {
                    try {
                        PhoneNumberUtil numberUtil = PhoneNumberUtil.createInstance(LoginWithEmail.this);
                        Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(login, "CM");
                        login = "+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber();
                        phone_is_valid = numberUtil.isValidNumber(phoneNumber);
                    } catch (NumberParseException e) {
                        Log.e(TAG, "Error when parsing the phone number : " + login, e);
                    }
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
        if (is_phone_type && login_ok || login_ok && pass_ok) {
            submit_btn.setEnabled(true);
            submit_btn.setClickable(true);
        }
        else{
            submit_btn.setEnabled(false);
            submit_btn.setClickable(false);
        }
    }
    private void initViews() {
        edit_email = findViewById(R.id.edit_login);
        edit_pass = findViewById(R.id.edit_pass);
        submit_btn = findViewById(R.id.submit_btn);
        btn_login_mode = findViewById(R.id.btn_login_mode);
        text_error = findViewById(R.id.text_error);
        edit_email_layout = findViewById(R.id.edit_email_layout);
        edit_pass_layout = findViewById(R.id.edit_pass_layout);
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

    /**
     * Function that send reset password mail.
     * @param login User login.
     */
    private void sendPasswordResetEmail (String login) {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        auth.sendPasswordResetEmail(login)
                .addOnCompleteListener(complete -> {
                    if (complete.isSuccessful()) {
                        Utils.setToastMessage(this, getString(R.string.mail_is_sent));
                        Utils.dismissDialog();
                    } else {
                        if (complete.getException() instanceof FirebaseAuthInvalidUserException)
                            Utils.setDialogMessage(this, R.string.this_email_is_not_recognized);
                        else if (complete.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        Log.e(TAG, "Email is never send : ", complete.getException());
                    }
                });
    }

    public void passForgot(View view) {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_Dialog)
                .setView(R.layout.layout_pass_forgot_email)
                .setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    AlertDialog alert = (AlertDialog) dialog;
                    EditText edit_mail = alert.findViewById(R.id.edit_login);
                    assert edit_mail != null;
                    String mail_str = Objects.requireNonNull(edit_mail.getText()).toString();

                    if (!mail_str.trim().equals("")) {
                        sendPasswordResetEmail(mail_str);
                    } else {
                        edit_mail.setError(getString(R.string.invalid_mail));
                    }

                })
                .show();
    }
}
