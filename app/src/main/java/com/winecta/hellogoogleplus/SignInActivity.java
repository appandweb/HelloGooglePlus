package com.winecta.hellogoogleplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "TAG";
    private static final String PREF_NAME = "PrefUserLogged";
    private static final String IS_LOGGED = "UserLogged";
    private static final String EMAIL = "UserEmail";
    private static final String IMAGE = "UserImage";
    private static final String NAME = "UserName";
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 9001;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private GoogleSignInAccount acct;

    @Bind(R.id.signin_iv_user_image)
    ImageView userImage;
    @Bind(R.id.signin_tv_user_email)
    TextView userEmail;
    @Bind(R.id.signin_tv_user_name)
    TextView userName;
    @Bind(R.id.signin_btn_login)
    SignInButton bLogin;
    @Bind(R.id.signin_btn_logout)
    Button bLogout;

    @OnClick(R.id.signin_btn_login)
    public void onLogin(View view) {
        signIn();
    }

    @OnClick(R.id.signin_btn_logout)
    public void onLogout(View view) {
        signOut();
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                saveUserLogout();
                showButtonLogin(false);
                bLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ButterKnife.bind(this);

        initializeGoogleApiClient();

        bLogin.setSize(SignInButton.SIZE_STANDARD);

        initializeSharedPreferences();
        checkUserLogged();


    }

    private void initializeSharedPreferences() {
        sharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void checkUserLogged() {
        String logged = sharedPreferences.getString(IS_LOGGED, "null");
        if (logged.equals("true")) {
            hideButtonLogin();
        } else {
            showButtonLogin(true);
        }
    }

    private void initializeGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SignInActivity.this.getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void showButtonLogin(boolean showAnimation) {
        if (showAnimation) {
            animateShowButtonLogin();
        } else {
            bLogin.setVisibility(View.VISIBLE);
        }
        userImage.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        bLogout.setVisibility(View.GONE);
    }

    private void animateShowButtonLogin() {
        int screenHeight = getScreenSize().y;
        TranslateAnimation animation = new TranslateAnimation(0, 0, screenHeight, bLogin.getY());
        animation.setDuration(450);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setInterpolator(new LinearInterpolator());

        bLogin.setVisibility(View.VISIBLE);
        bLogin.startAnimation(animation);
    }


    private Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void hideButtonLogin() {
        bLogin.setVisibility(View.GONE);
        bLogin.clearAnimation();
        userImage.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);
        bLogout.setVisibility(View.VISIBLE);

        userEmail.setText(sharedPreferences.getString(EMAIL, "no_email"));
        userName.setText(sharedPreferences.getString(NAME, "no_name"));
        Picasso.with(this)
                .load(sharedPreferences.getString(IMAGE, ""))
                .into(userImage);

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "HandleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            acct = result.getSignInAccount();
            saveUserLogged();
        } else {
            Log.d(TAG, "Auth failed");
        }
    }

    private void saveUserLogged() {
        editor.putString(IS_LOGGED, "true");
        editor.putString(EMAIL, acct.getEmail());
        editor.putString(IMAGE, String.valueOf(acct.getPhotoUrl()));
        editor.putString(NAME, acct.getGivenName());
        editor.commit();
        hideButtonLogin();
    }

    private void saveUserLogout() {
        editor.clear();
        editor.commit();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Disconnected");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bLogin.clearAnimation();
    }
}
