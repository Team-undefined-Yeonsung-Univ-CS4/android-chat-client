package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    ConstraintLayout rootLayout;
    TextInputLayout etEmailLayout, etPasswordLayout;
    TextInputEditText etEmail, etPassword;
    AppCompatButton btnLogin;
    TextView tvSignUp;

    @Override
    public void onStart() {
        super.onStart();

        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
             Intent it = new Intent(LoginActivity.this, LobbyActivity.class);
             startActivity(it);
             finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        rootLayout = findViewById(R.id.login_layout);
        etEmailLayout = findViewById(R.id.et_email_address_layout);
        etPasswordLayout = findViewById(R.id.et_password_layout);
        etEmail = findViewById(R.id.et_email_address);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);

        etEmail.addTextChangedListener(twEmail);

        rootLayout.setOnClickListener(view -> UtilManager.clearFocus(this));
        btnLogin.setOnClickListener(view -> requestLogin());
        tvSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 이메일 입력 값 실시간 검사
     */
    private TextWatcher twEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = s.toString();
            if (email.isEmpty()) {
                etEmailLayout.setErrorEnabled(false);
            } else if (!isValidEmail(email)) {
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
     * 이메일 형식 유효성 검사
     * @param email user email
     * @return userId@email.com 또는 co.국가코드
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Firebase Auth 로그인 요청
     */
    private void requestLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!(email.isEmpty() || password.isEmpty())) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail: success");
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                    Intent it = new Intent(LoginActivity.this, LobbyActivity.class);
                    startActivity(it);
                    finish();
                } else {
                    Log.w(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "계정 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}