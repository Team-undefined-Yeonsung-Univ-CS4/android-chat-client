package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.util.UtilManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthCheck";

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ConstraintLayout rootLayout;
    private TextInputLayout etEmailLayout, etPasswordLayout;
    private TextInputEditText etEmail, etPassword;
    private AppCompatButton btnLogin;
    private TextView tvSignUp;
    private ProgressBar progressBar;

    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        rootLayout = findViewById(R.id.root_layout);
        etEmailLayout = findViewById(R.id.et_email_address_layout);
        etPasswordLayout = findViewById(R.id.et_password_layout);
        etEmail = findViewById(R.id.et_email_address);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
        progressBar = findViewById(R.id.progressBar);

        etEmail.addTextChangedListener(emailVerification);

        rootLayout.setOnClickListener(view -> UtilManager.clearFocus(this));
        btnLogin.setOnClickListener(view -> requestLogin());
        tvSignUp.setOnClickListener(view -> {
            it = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(it);
        });
    }

    /**
     * 이메일 입력 값 실시간 검사
     */
    private TextWatcher emailVerification = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = s.toString();
            if (email.isEmpty()) {
                etEmailLayout.setErrorEnabled(false);
            } else if (!UtilManager.isValidEmail(email)) {
                etEmailLayout.setErrorEnabled(true);
                etEmailLayout.setError("이메일 형식이어야 합니다");
            } else {
                etEmailLayout.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Firebase Auth 로그인 요청
     */
    private void requestLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!(email.isEmpty() || password.isEmpty())) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                progressBar.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail: success");
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    it = new Intent(LoginActivity.this, LobbyActivity.class);

                    // FIXME: uid는 어디에서든 추출할 수 있으므로 intent로 보내주지 않아도 됌
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String Uid = user.getUid();
                        it.putExtra("UID",Uid);
                    }
                    // FIXME END

                    startActivity(it);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "계정 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}