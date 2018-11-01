package pcvark.com.firebasedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pcvark.com.firebasedemo.BuildConfig;
import pcvark.com.firebasedemo.R;
import pcvark.com.firebasedemo.util.Constants;
import pcvark.com.firebasedemo.util.Util;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    private EditText et_email, et_password;
    private Button login_btn;
    private TextView tv_register, tv_forgotPassword;
    private FirebaseAuth firebaseAuth;
    private String TAG = LoginActivity.class.getSimpleName();
    private Context context;
//    private SignInButton google_sign_in_btn;
    private GoogleSignInClient googleSignInClient;
    private ProgressBar progress_bar;
    private LoginButton fb_sign_btn;
    private CallbackManager callbackManager;
    private ImageView iv_google, iv_facebook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setClickListeners();
        configureGoogleSignIn();
        firebaseAuth = FirebaseAuth.getInstance();
        configureFacebookSignIn();


    }

    private void configureFacebookSignIn() {

        callbackManager = CallbackManager.Factory.create();
        fb_sign_btn.setReadPermissions("email", "public_profile");
        fb_sign_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError" + error.getLocalizedMessage());
                Toast.makeText(context, "FacebookException : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.e(TAG, "handleFacebookAccessToken :" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "signInWithCredential:success");
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        updateUI(currentUser);
                    }
                } else {
                    Log.e(TAG, "signInWithCredential:failure " + task.getException());
                    Toast.makeText(context, "signInWithCredential:failure :" + task.getException(), Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }

            }
        });
    }

    private void setClickListeners() {
//        google_sign_in_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forgotPassword.setOnClickListener(this);
        iv_google.setOnClickListener(this);
        iv_facebook.setOnClickListener(this);
    }

    private void configureGoogleSignIn() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)        // for firebase web server authentication
                .requestEmail()
                .build();

        // for custom developed API i.e own server
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();

        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "on resume Called");
        if (progress_bar.getVisibility() == View.VISIBLE)
            progress_bar.setVisibility(View.GONE);

    }


    private void updateUI(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        String displayName = firebaseUser.getDisplayName();
        Intent intent = new Intent(context, MainActivity.class);    // launch MainActivity  if exist  // hide google sign in button if not
        intent.putExtra(Constants.KEY_USER_EMAIL, email);
        intent.putExtra(Constants.KEY_USER_DISPLAY_NAME, displayName);
        startActivity(intent);
        finish();
    }


    private void init() {

        context = this;
        progress_bar = findViewById(R.id.progress_bar);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        login_btn = findViewById(R.id.login_btn);
        tv_register = findViewById(R.id.tv_register);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
//        google_sign_in_btn = findViewById(R.id.google_sign_in_btn);
        fb_sign_btn = findViewById(R.id.fb_sign_btn);
        iv_google = findViewById(R.id.iv_google);
        iv_facebook = findViewById(R.id.iv_facebook);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.google_sign_in_btn:
//                googleSignIn();
//                break;
            case R.id.login_btn:
                loginWithEmailPassword();
                break;
            case R.id.tv_register:
                startActivity(new Intent(context, SignUpActivity.class));
                break;
            case R.id.tv_forgotPassword:
                startActivity(new Intent(context, ResetPasswordActivity.class));
                break;
            case R.id.iv_google:
//                google_sign_in_btn.performClick();
                googleSignIn();
                break;
            case R.id.iv_facebook:
                fb_sign_btn.performClick();
                break;
        }
    }

    private void loginWithEmailPassword() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(email))
            Util.showSnackBar(et_email, getString(R.string.enter_email));
        else if (TextUtils.isEmpty(password))
            Util.showSnackBar(et_password, getString(R.string.enter_password));
        else {
            progress_bar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress_bar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            updateUI(currentUser);
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                        Util.showSnackBar(et_email, getString(R.string.authentication_failed));
//                        updateUI(null);
                    }
                }
            });
        }

    }

    private void googleSignIn() {
        progress_bar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(signInAccountTask);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);    // for facebook integration
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount googleSignInAccount = completedTask.getResult(ApiException.class);
            if (googleSignInAccount != null) {
                fireBaseAuthWithGoogle(googleSignInAccount);   // signed in successfully, authenticate with firebase
            }
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code= " + e.getStatusCode());
//            updateUI(null);
        }

    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Log.e(TAG, "fireBaseAuthWithGoogle : " + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress_bar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.e(TAG, "signInWithCredential success");
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        updateUI(currentUser);
                    }
                } else {
                    Log.e(TAG, "signInWithCredential : failure", task.getException());
                    Util.showSnackBar(findViewById(R.id.main_layout), getString(R.string.authentication_failed));
                }
            }
        });
    }
}
