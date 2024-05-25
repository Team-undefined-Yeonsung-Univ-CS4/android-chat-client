package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.util.UtilManager;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthCheck";

    private FirebaseAuth auth;
    private FirebaseUser user;

    ConstraintLayout rootLayout;
    private Toolbar toolbar;
    private EditText etName, etEmail, etPassword;
    private Spinner spBirthYear, spBirthMonth, spBirthDay;
    private AppCompatButton btnSignUp;

    Intent it;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        rootLayout = findViewById(R.id.root_layout);
        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        spBirthYear = findViewById(R.id.sp_birth_year);
        spBirthMonth = findViewById(R.id.sp_birth_month);
        spBirthDay = findViewById(R.id.sp_birth_day);
        btnSignUp = findViewById(R.id.btn_sign_up);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        rootLayout.setOnClickListener(view -> UtilManager.clearFocus(this));
        btnSignUp.setOnClickListener(view -> requestCreateAccount());
    }

    /**
     * Firebase Auth 계정 생성 요청
     */
    private void requestCreateAccount() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // TODO: 생년월일 데이터 DB insert
        String birthYear = spBirthYear.getSelectedItem().toString();
        String birthMonth = spBirthMonth.getSelectedItem().toString();
        String birthDay = spBirthDay.getSelectedItem().toString();

        if (!(name.isEmpty() && email.isEmpty() && password.isEmpty())) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail: success");
                    Toast.makeText(getApplicationContext(), R.string.success_create_account, Toast.LENGTH_SHORT).show();
                    // FirebaseUser user = auth.getCurrentUser();

                    // TODO: 인증 메일 전송 및 승인 여부 체크

                    it = new Intent(SignUpActivity.this, LobbyActivity.class);
                    startActivity(it);
                    finish();
                } else {
                    Log.w(TAG, "createUserWithEmail: failure", task.getException());
                    Toast.makeText(getApplicationContext(), R.string.unable_to_create_account, Toast.LENGTH_SHORT).show();
                    // TODO: 계정 생성 실패 사유를 더 상세하게 설명
                    //  (이메일 중복? 유효하지 않은 입력값? 등등)
                }
            });
        } else {
            Toast.makeText(this, R.string.enter_all_info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 인증 메일 전송
     */
    private void sendAuthEmail() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(SignUpActivity.this, R.string.auth_mail_has_been_sent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
