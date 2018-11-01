package pcvark.com.firebasedemo.activity;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import pcvark.com.firebasedemo.R;
import pcvark.com.firebasedemo.util.Util;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private Button reset_btn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private LinearLayout ll_forgot_email;
    private ImageView iv_back_toolbar;
    private String TAG = ResetPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();
        iv_back_toolbar.setOnClickListener(v -> onBackPressed());
        reset_btn.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Util.showSnackBar(et_email, getString(R.string.enter_registered_email));
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ll_forgot_email.setVisibility(View.GONE);
                        findViewById(R.id.tv_forgot_email_description).setVisibility(View.VISIBLE);

                    } else {
                        Util.showSnackBar(et_email, getString(R.string.reset_email_failed));
                        Log.e(TAG, getString(R.string.reset_email_failed) + " : " + task.getException().getLocalizedMessage());
                    }


                }
            });


        });
    }

    private void init() {
        et_email = findViewById(R.id.et_email);
        ll_forgot_email = findViewById(R.id.ll_forgot_email);
        iv_back_toolbar = findViewById(R.id.iv_back_toolbar);
        reset_btn = findViewById(R.id.reset_btn);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
