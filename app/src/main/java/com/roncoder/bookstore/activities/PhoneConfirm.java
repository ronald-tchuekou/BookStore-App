package com.roncoder.bookstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.util.Utils;

import java.util.concurrent.TimeUnit;

import static com.roncoder.bookstore.activities.LoginPhone.IS_USER;
import static com.roncoder.bookstore.activities.LoginPhone.PHONE_NUMBER;

public class PhoneConfirm extends AppCompatActivity {

    private boolean is_user;
    private String phone_number, verificationId, code;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button validate;
    private EditText code_edit;
    private TextView information, timer_view, resend_code;

    private final int[] timer = {0};
    private int time = 10;
    private final Handler handlerTime = new Handler();

    //Phone verification callback.
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            verifyCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w("PhoneConfirm", "phone verification failed : ", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException)
                Utils.setDialogMessage(PhoneConfirm.this, getString(R.string.error_code_verification));
            else if (e instanceof FirebaseNetworkException)
                Utils.setDialogMessage(PhoneConfirm.this, getString(R.string.network_not_allowed));
        }

        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken fToken) {
            super.onCodeSent(id, fToken);
            runnable.run();
            verificationId = id;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Log.e("PhoneConfirm", "onCodeAutoRetrievalTimeOut: Time is over.");

            resend_code.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_confirm);

        // Get the extra data.
        Intent extra = getIntent();
        is_user = extra.getBooleanExtra(IS_USER, false);
        phone_number = extra.getStringExtra(PHONE_NUMBER);

        // Init views.
        validate = findViewById(R.id.validate);
        code_edit = findViewById(R.id.code_edit);
        information = findViewById(R.id.information);
        timer_view = findViewById(R.id.timer);
        resend_code = findViewById(R.id.resend_code);


        // Init views value.
        timer_view.setText(Utils.secondsToTime(time));

        // Send the message.
        sendSms();

        // Listeners
        setListener();
    }

    private void setListener() {
        String text = getString(R.string.enter_code) + " :   " + phone_number;
        information.setText(text);
        validate.setOnClickListener( v ->  {
            code = code_edit.getText().toString().trim();
            if (!code.equals("") && code.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                verifyCredential(credential);
            } else {
                code_edit.setError(getString(R.string.invalid_code));
            }
        });

        resend_code.setOnClickListener(v -> {
            time += 5;
            sendSms();
        });
    }

    private void sendSms() {
        resend_code.setEnabled(false);
        PhoneAuthProvider.getInstance(FirebaseAuth.getInstance())
                .verifyPhoneNumber(phone_number, time, TimeUnit.SECONDS, this, verificationCallback);
        timer[0] = time;
    }

    /**
     * Function that verify the credential.
     * @param phoneAuthCredential credential.
     */
    private void verifyCredential(PhoneAuthCredential phoneAuthCredential) {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(command -> {

            Utils.dismissDialog();

            if (!command.isSuccessful()) {
                Exception e = command.getException();
                if (e instanceof FirebaseAuthInvalidCredentialsException)
                    Utils.setDialogMessage(this, R.string.invalid_code);
                else
                    Utils.setDialogMessage(this, R.string.error_has_provide);
                Log.e("PhoneConfirm", "Verify Credential : ", command.getException());
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                if (!is_user)
                    getMoreInfoAboutUser();
                else {
                    Utils.setToastMessage(this, getString(R.string.connect_successful));
                    finish();
                }
                return;
            }

            // User is null.
            Utils.setDialogMessage(this, getString(R.string.error_has_provide)
                    + ", " + getString(R.string.retry_again), (dialog, which) -> {
                        startActivity(new Intent(this, Login.class));
                        finish();
                    });
        });
    }

    /**
     * Function to get more information about current user.
     */
    private void getMoreInfoAboutUser() {
        startActivity(new Intent(this, UserMoreInfo.class));
        finish();
    }

    // Update the time counter.
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer[0]--;
            if (timer[0] < 0){
                handlerTime.removeCallbacks(runnable);
                resend_code.setEnabled(true);
                return;
            }
            timer_view.setText(Utils.secondsToTime(timer[0]));
            handlerTime.postDelayed(runnable, 1000);
        }
    };

}