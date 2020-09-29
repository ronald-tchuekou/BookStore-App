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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.roncoder.bookstore.R;
import com.roncoder.bookstore.dbHelpers.UserHelper;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class LoginWithSocial extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginWithSocial";
    private static final int RC_SIGN_IN = 0;
    private TextView text_desc, text_change_state, text_passed;
//    private LoginButton facebook_btn;
    private Button google_btn, twitter_btn, sign_btn, facebook_btn;
    private boolean isSignInMode = true;
    GoogleSignInClient mGoogleSignInClient;

    OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // FACEBOOK :
    LoginManager loginManager = LoginManager.getInstance();
    private CallbackManager callbackManager;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_social);
        callbackManager = CallbackManager.Factory.create();
        auth.useAppLanguage();

        if (mAuth.getCurrentUser() != null)
            Utils.setDialogMessage(this, R.string.your_all_ready_auth, (dialog, which) -> finish());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                    startActivityForResult(new Intent(this, LoginWithEmail.class), 1);
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
                    startActivityForResult(new Intent(this, SignUpWithEmail.class), 1);
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
     * Function to sign up with facebook.
     */
    private void signUpWithFacebook () {
        signInWithFacebook();
    }
    private void signUpWithGoogle () {
       signInWithGoogle();
    }
    private void signUpWithTwitter() {
        signInWithTwitter();
    }

    /**
     * Function to sign in with facebook.
     */
    private void signInWithFacebook() {
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "Log facebook successful, id = " + token);
            }

            @Override
            public void onCancel() {
                Utils.setToastMessage(LoginWithSocial.this, "Log cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook auth error : ", error);
                Utils.setToastMessage(LoginWithSocial.this, "Log Error.");
            }
        });
    }
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signInWithTwitter() {
        Utils.setProgressDialog(this, getString(R.string.wait_a_moment));
        auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(success -> {
                    Utils.dismissDialog();
                    Map<String, Object> userProfile = null;
                    if (success.getAdditionalUserInfo() != null)
                        userProfile = success.getAdditionalUserInfo().getProfile();

                    if (userProfile != null){ // Save this user to the data base.
                        User user = new User();

                        String[] splitName = Objects.requireNonNull(userProfile.get("name")).toString().split(" ");
                        user.setSurname(splitName[0]);
                        if (splitName.length >= 2) user.setName(splitName[1]);
                        user.setProfile(Objects.requireNonNull(userProfile.get("profile_image_url")).toString());
                        user.setLogin("@"+user.getSurname()+user.getName());

                        FirebaseUser user1 = auth.getCurrentUser();
                        if (user1 != null) {
                            user.setId(user1.getUid());
                            user1.getIdToken(true).addOnSuccessListener(suc -> {
                                user.setToken(suc.getToken());
                                // We added this user information to the database.
                                addUserToDataBase (user);
                            }).addOnFailureListener(fail -> {
                                if (fail instanceof FirebaseNetworkException)
                                    Utils.setDialogMessage(this, R.string.network_not_allowed);
                                else
                                    Log.e(TAG, "Error: ", fail);
                            });
                        } else {
                            Utils.setDialogMessage(this, getString(R.string.not_auth));
                        }
                    }
                }).addOnFailureListener(
                e -> {
                    Utils.dismissDialog();
                    Utils.setToastMessage(this, "userProfile fail.");
                });
    }

    /**
     * Function to add user to the data base.
     * @param user User.
     */
    private void addUserToDataBase(User user) {
        user.setId(auth.getUid());
        Utils.setProgressDialog(this, getString(R.string.saver_data));
        // Check if the user exist.
        UserHelper.getUserById(user.getId())
                .addOnCompleteListener(complete -> {
                    if (complete.isSuccessful()) {
                        // Add the user.
                        UserHelper.addUser(user)
                                .addOnCompleteListener(com -> {
                                    Utils.dismissDialog();
                                    if (com.isSuccessful()) {
                                        Utils.setToastMessage(LoginWithSocial.this, getString(R.string.connect_successful));
                                        finish();
                                    } else {
                                        if (com.getException() instanceof FirebaseNetworkException)
                                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                                        else
                                            Utils.setToastMessage(this, getString(R.string.failled));
                                        Log.e(TAG, "Error: ", com.getException());
                                    }
                                });
                    } else {
                        Utils.dismissDialog();
                        if (complete.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Utils.setToastMessage(this, getString(R.string.failled));
                            Log.e(TAG, "Error: ", complete.getException());
                    }
                });
    }

    private void changeMode() {
        if (isSignInMode) {
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

        if (requestCode == 1)
            finish();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebase Auth With Google: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(), account);
            } catch (ApiException e) {
                Log.e(TAG, "Error: ", e);
            }
        }

    }

    /**
     * Function that finalized the google login.
     * @param idToken Access token
     */
    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && account != null){
                            User user1 = new User();
                            user1.setId(user.getUid());
                            user1.setName(account.getFamilyName());
                            user1.setSurname(account.getGivenName());
                            user1.setLogin(account.getEmail());
                            user1.setProfile(Objects.requireNonNull(account.getPhotoUrl()).toString());
                            user1.setToken(idToken);
                            addUserToDataBase(user1);
                        }
                    } else {
                        if (task.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Log.e(TAG, "Error: ", task.getException());
                    }
                });
    }

    /**
     * Function that finalized the facebook login.
     * @param token Access token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User user1 = new User();
                            String[] splitName = Objects.requireNonNull(user.getDisplayName()).split(" ");
                            user1.setSurname(splitName[0]);
                            if (splitName.length >= 2) user1.setName(splitName[1]);
                            user1.setId(user.getUid());
                            user1.setProfile(Objects.requireNonNull(user.getPhotoUrl()).toString());
                            user1.setPhone(user.getPhoneNumber() == null ? "" : user.getPhoneNumber());
                            user1.setLogin(user.getEmail() == null ? "" : user.getEmail());
                            user1.setToken(token.getToken());
                            addUserToDataBase(user1);
                        }
                    } else {
                        if (task.getException() instanceof FirebaseNetworkException)
                            Utils.setDialogMessage(this, R.string.network_not_allowed);
                        else
                            Log.e(TAG, "Error: ", task.getException());
                    }
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
