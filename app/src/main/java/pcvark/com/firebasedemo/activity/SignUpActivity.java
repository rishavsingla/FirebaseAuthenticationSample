package pcvark.com.firebasedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import pcvark.com.firebasedemo.R;
import pcvark.com.firebasedemo.util.Constants;
import pcvark.com.firebasedemo.util.Util;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;
    private Button register_btn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private String TAG = SignUpActivity.class.getSimpleName();
    private Context context;
    private ImageView iv_back_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        firebaseAuth = FirebaseAuth.getInstance();
        register_btn.setOnClickListener(v -> SignUpActivity.this.signUp());

        iv_back_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void signUp() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(email))
            Util.showSnackBar(et_email, getString(R.string.enter_email));
        else if (TextUtils.isEmpty(password))
            Util.showSnackBar(et_password, getString(R.string.enter_password));
        else if (!Util.checkEmail(email))
            Util.showSnackBar(et_email, getString(R.string.valid_email));
        else if (password.length() < 8)
            Util.showSnackBar(et_password, getString(R.string.password_length));
        else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            String userEmail = null;
                            if (currentUser != null) {
                                userEmail = currentUser.getEmail();
                            }
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra(Constants.KEY_USER_EMAIL, userEmail);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Authentication failed : " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Authentication Failed" + task.getException());
                        }

                    });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }


    private void init() {
        context = this;
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        register_btn = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progressBar);
        iv_back_toolbar = findViewById(R.id.iv_back_toolbar);

    }
}
