package com.roncoder.bookstore.activities;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.utils.Utils;

import java.util.Arrays;

public class LoginWithSocial extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginWithSocial";
    private TextView text_desc, text_change_state, text_passed;
//    private LoginButton facebook_btn;
    private Button google_btn, twitter_btn, sign_btn, facebook_btn;
    private boolean isSignInMode = true;

    // FACEBOOK :
    LoginManager loginManager = LoginManager.getInstance();
    private CallbackManager callbackManager;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_social);
        callbackManager = CallbackManager.Factory.create();
        initView();
        initListeners();
    }
    private void initListeners() {
        text_change_state.setOnClickListener(this);
        text_passed.setOnClickListener(this);
        facebook_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
        twitter_btn.setOnClickListener(this);
        sign_btn.setOnClickListener(this);
    }
    private void initView() {
        text_desc = findViewById(R.id.textView);
        text_change_state = findViewById(R.id.text_change_state);
        text_passed = findViewById(R.id.text_passed);
        facebook_btn = findViewById(R.id.facebook_btn);
        google_btn = findViewById(R.id.google_btn);
        twitter_btn = findViewById(R.id.twitter_btn);
        sign_btn = findViewById(R.id.sign_btn);
    }
    @Override
    public void onClick(View v) {
        if (isSignInMode) {
            switch (v.getId()) {
                case R.id.facebook_btn:
                    signInWithFacebook();
                    break;
                case R.id.google_btn:
                    signInWithGoogle();
                    break;
                case R.id.twitter_btn:
                    signInWithTwitter();
                    break;
                case R.id.sign_btn:
                    startActivity(new Intent(this, LoginWithEmail.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.text_change_state:
                    changeMode();
                    break;
                default:
                    finish();
                    break;
            }
        }else {
            switch (v.getId()) {
                case R.id.facebook_btn:
                    signUpWithFacebook();
                    break;
                case R.id.google_btn:
                    signUpWithGoogle();
                    break;
                case R.id.twitter_btn:
                    signUpWithTwitter();
                    break;
                case R.id.sign_btn:
                    startActivity(new Intent(this, SignUpWithEmail.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.text_change_state:
                    changeMode();
                    break;
                default:
                    finish();
                    break;
            }
        }
    }

    /**
     * Function to get the image url.
     * @param id Acccoutn id.
     */
    private String getProfileFacebookUrl (String id) {
        return "https://graph.facebook.com/" + id + "/picture?type=normal";
    }

    /**
     * Function to sign up with facebook.
     */
    private void signUpWithFacebook () {
        Toast.makeText(this, "Sign up with facebook", Toast.LENGTH_SHORT).show();
    }
    private void signUpWithGoogle () {
        Toast.makeText(this, "Sign up with google", Toast.LENGTH_SHORT).show();
    }
    private void signUpWithTwitter() {
        Toast.makeText(this, "Sign up with twitter", Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to sign in with facebook.
     */
    private void signInWithFacebook() {
        loginManager.logInWithReadPermissions(this,
                Arrays.asList("user_birthday", "user_gender", "email", "public_profile"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //TODO
                String id = loginResult.getAccessToken().getToken();
                Utils.setToastMessage(LoginWithSocial.this, "Log successful, id = " + id);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // TODO
                Utils.setToastMessage(LoginWithSocial.this, "Log cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                // TODO
                Utils.setToastMessage(LoginWithSocial.this, "Log Error.");
            }
        });
//        Toast.makeText(this, "Sign in with facebook", Toast.LENGTH_SHORT).show();
    }
    private void signInWithGoogle() {
        Toast.makeText(this, "Sign in with google", Toast.LENGTH_SHORT).show();
    }
    private void signInWithTwitter() {
        Toast.makeText(this, "Sign in with twitter", Toast.LENGTH_SHORT).show();
    }
    private void changeMode() {
        if (isSignInMode){
            isSignInMode = false;
            changeTextView(text_desc, R.string.text_sign_up);
            changeTextView(text_change_state, R.string.already_registered);
            changeTextBtn(facebook_btn, R.string.sign_up_with_facebook);
            changeTextBtn(google_btn, R.string.sign_up_with_google);
            changeTextBtn(twitter_btn, R.string.sign_up_with_twitter);
            changeTextBtn(sign_btn, R.string.sign_up_with_email);
        }else {
            isSignInMode = true;
            changeTextView(text_desc, R.string.text_sign_in);
            changeTextView(text_change_state, R.string.not_sign_up);
            changeTextBtn(facebook_btn, R.string.sign_in_with_facebook);
            changeTextBtn(google_btn, R.string.sign_in_with_google);
            changeTextBtn(twitter_btn, R.string.sign_in_with_twitter);
            changeTextBtn(sign_btn, R.string.sign_in_with_email);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null)
                            Log.i(TAG, "user: " + user.getProviderData());
//                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginWithSocial.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }

                    // ...
                });
    }

    /**
     * Function to change the on the buttons
     * @param btn Button view.
     * @param stringRes resource string.
     */
    private void changeTextBtn(Button  btn, @StringRes int stringRes) {
        btn.animate()
                .rotationXBy(90f)
                .setDuration(150)
                .withEndAction(() -> {
                    btn.setRotationX(360f);
                    btn.setText(stringRes);
                });
        btn.animate()
                .setStartDelay(150)
                .rotationXBy(90f)
                .setDuration(150);
    }

    /**
     * Function that change the text of the textView.
     * @param textView TextView.
     * @param stringRes String resource.
     */
    private void changeTextView (TextView textView, @StringRes int stringRes) {
        textView.animate()
                .alphaBy(1f)
                .setDuration(150)
                .withEndAction(() -> textView.setText(stringRes));
        textView.animate()
                .setStartDelay(150)
                .alphaBy(0f)
                .setDuration(150);
    }
}
