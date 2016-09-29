package com.android.letsnurture.firebaseanonymousauthentication.activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.letsnurture.firebaseanonymousauthentication.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import fragments.LinkAccountDialogFragment;
import utils.DialogUtils;
import utils.FormValidationUtils;
import utils.NetworkUtils;

public class HomeActivity extends BaseAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.tIETLoginEmail)
    EditText mEditEmail;
    @BindView(R.id.tIETLoginPassword)
    EditText mEditPassword;
    @BindView(R.id.buttonLinkWithEmail)
    Button buttonLinkWithEmail;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private ArrayList<String> permissions = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1;

    @OnClick(R.id.buttonLinkWithEmail)
    public void linkWithEmail() {
        FormValidationUtils.clearErrors(mEditEmail, mEditPassword);

        if (FormValidationUtils.isBlank(mEditEmail)) {
            FormValidationUtils.setError(null, mEditEmail, getString(R.string.str_enter_email));
            return;
        }

        if (!FormValidationUtils.isEmailValid(mEditEmail)) {
            FormValidationUtils.setError(null, mEditEmail, getString(R.string.str_enter_valid_email));
            return;
        }

        if (TextUtils.isEmpty(mEditPassword.getText())) {
            FormValidationUtils.setError(null, mEditPassword, getString(R.string.str_enter_password));
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(mEditEmail.getText().toString(), mEditPassword.getText().toString());
        convertToPermanent(credential);
    }

    public void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (NetworkUtils.isInternetAvailable(this)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
        } else {
            showToast(getString(R.string.str_check_internet));
        }
    }

    @OnClick(R.id.btnLink)
    void linkAccount() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LinkAccountDialogFragment linkAccountDialogFragment = new LinkAccountDialogFragment();

        linkAccountDialogFragment.setCloseDialogInterface(new LinkAccountDialogFragment.CloseDialogInterface() {
            @Override
            public void onCloseDialogListener(View view) {
                switch (view.getId()) {

                    case R.id.buttonEmail:
                        Log.d("account", "email/password");
                        mEditEmail.setVisibility(View.VISIBLE);
                        mEditPassword.setVisibility(View.VISIBLE);
                        buttonLinkWithEmail.setVisibility(View.VISIBLE);
                        break;

                    case R.id.buttonGoogle:
                        Log.d("account", "google");
                        googleSignIn();
                        break;

                    case R.id.buttonFacebook:
                        Log.d("account", "facebook");
                        linkWithFacebook();
                        break;
                }
            }
        });
        linkAccountDialogFragment.show(fragmentManager, "LinkAccountDialogFragment");
    }

    public void linkWithFacebook() {

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        permissions.add("user_friends");
        permissions.add("public_profile");
        permissions.add("email");


        if (NetworkUtils.isInternetAvailable(this)) {
            loginManager.logInWithReadPermissions(this, permissions);
        } else {
            showToast(getString(R.string.str_check_internet));
        }

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("facebook login", "success");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(HomeActivity.this, "Facebook Sign in failed. Please try again" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("facebook login error", error.toString());
            }
        });
    }

    private void convertToPermanent(AuthCredential credential) {

        if (NetworkUtils.isInternetAvailable(this)) {

            DialogUtils.showProgressDialog(HomeActivity.this, "", getString(R.string.sign_in), false);
            mFirebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        DialogUtils.dismissProgressDialog();
                        Log.d("Account", "linked account successfully");
                        Toast.makeText(HomeActivity.this, R.string.str_link_success, Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        DialogUtils.dismissProgressDialog();
                        Log.e("Account", "failed to link account");
                    }
                }
            });
        }
    }

    @OnClick(R.id.btn_home_logout)
    public void logout() {
        mFirebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("google sign in", "successful");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("google sign in", "failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        convertToPermanent(credential);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        convertToPermanent(credential);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleSignIn", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, R.string.str_google_play_services_error, Toast.LENGTH_SHORT).show();
    }
}
