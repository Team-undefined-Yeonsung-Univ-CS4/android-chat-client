package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.util.UtilManager;

public class SignUpActivity extends AppCompatActivity {
    private static final String AUTH = "FirebaseAuthCheck";
    private static final String DB = "FirebaseRealtimeDatabaseCheck";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private ConstraintLayout rootLayout;
    private Toolbar toolbar;
    private EditText etName, etEmail, etPassword, etBirthday;
    private AppCompatButton btnSignUp;

    Intent it;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        rootLayout = findViewById(R.id.root_layout);
        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etBirthday = findViewById(R.id.et_birthday);
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
        String birthday = etBirthday.getText().toString();

        if (!(name.isEmpty() && email.isEmpty() && password.isEmpty() && birthday.isEmpty())) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

                // TODO: 인증 메일 전송 및 승인 여부 체크

                if (task.isSuccessful()) {
                    Log.d(AUTH, "createUserWithEmail: success");

                    user = auth.getCurrentUser();
                    userRef = db.getReference("users").child(user.getUid());

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("birthday", birthday);
                    userData.put("email", email);

                    userRef.setValue(userData).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Log.d(DB, "User data saved to database");
                            Toast.makeText(getApplicationContext(), R.string.success_create_account, Toast.LENGTH_SHORT).show();

                            it = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(it);
                            finish();
                        } else {
                            Log.w(DB, "Failed to save user data to database", dbTask.getException());
                            Toast.makeText(getApplicationContext(), R.string.error_saving_user_data, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.w(AUTH, "createUserWithEmail: failure", task.getException());
                    Toast.makeText(getApplicationContext(), R.string.email_already_exists, Toast.LENGTH_SHORT).show();
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
                    Log.d(AUTH, "Email sent.");
                    Toast.makeText(SignUpActivity.this, R.string.auth_mail_has_been_sent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
