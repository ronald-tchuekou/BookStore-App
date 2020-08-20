package com.roncoder.bookstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.utils.Utils;

import java.util.Collections;

public class LoginWithSocial extends AppCompatActivity implements View.OnClickListener {
    private TextView text_desc, text_change_state, text_passed;
    private LoginButton facebook_btn;
    private Button google_btn, instangram_btn, sign_btn;
    private boolean isSignInMode = true;

    // FACEBOOK :
    private CallbackManager callbackManager;


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
        instangram_btn.setOnClickListener(this);
        sign_btn.setOnClickListener(this);
    }
    private void initView() {
        text_desc = findViewById(R.id.textView);
        text_change_state = findViewById(R.id.text_change_state);
        text_passed = findViewById(R.id.text_passed);
        facebook_btn = (LoginButton) findViewById(R.id.facebook_btn);
        google_btn = findViewById(R.id.google_btn);
        instangram_btn = findViewById(R.id.instangram_btn);
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
                case R.id.instangram_btn:
                    signInWithInstagram();
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
                case R.id.instangram_btn:
                    signUpWithInstagram();
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
    private void signUpWithFacebook() {
        Toast.makeText(this, "Sign up with facebook", Toast.LENGTH_SHORT).show();
    }
    private void signUpWithGoogle() {
        Toast.makeText(this, "Sign up with google", Toast.LENGTH_SHORT).show();
    }
    private void signUpWithInstagram() {
        Toast.makeText(this, "Sign up with instagram", Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to sign in with facebook.
     */
    private void signInWithFacebook() {
        facebook_btn.setPermissions(Collections.singletonList("email, public_profile"));
        facebook_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //TODO
                String id = loginResult.getAccessToken().getUserId();
                Utils.setToast(LoginWithSocial.this, "Log successful, id = " + id);
            }

            @Override
            public void onCancel() {
                // TODO
                Utils.setToast(LoginWithSocial.this, "Log cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                // TODO
                Utils.setToast(LoginWithSocial.this, "Log Error.");
            }
        });
//        Toast.makeText(this, "Sign in with facebook", Toast.LENGTH_SHORT).show();
    }
    private void signInWithGoogle() {
        Toast.makeText(this, "Sign in with google", Toast.LENGTH_SHORT).show();
    }
    private void signInWithInstagram() {
        Toast.makeText(this, "Sign in with instrangram", Toast.LENGTH_SHORT).show();
    }
    private void changeMode() {
        if (isSignInMode){
            isSignInMode = false;
            text_desc.setText(R.string.text_sign_up);
            text_change_state.setText(R.string.already_registered);
            facebook_btn.setText(R.string.sign_up_with_facebook);
            google_btn.setText(R.string.sign_up_with_google);
            instangram_btn.setText(R.string.sign_up_with_instangram);
            sign_btn.setText(R.string.sign_up_with_email);
        }else {
            isSignInMode = true;
            text_desc.setText(R.string.text_sign_in);
            text_change_state.setText(R.string.not_sign_up);
            facebook_btn.setText(R.string.sign_in_with_facebook);
            google_btn.setText(R.string.sign_in_with_google);
            instangram_btn.setText(R.string.sign_in_with_instangram);
            sign_btn.setText(R.string.sign_in_with_email);
        }
    }
}
