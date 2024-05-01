package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.undefined.chatclient.R;

public class LobbyActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;

    TextView tvUserId;
    Button btnSignOut;
    Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        tvUserId = findViewById(R.id.tv_user_id);
        btnSignOut = findViewById(R.id.btn_sign_out);

        if (user == null) {
            it = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(it);
            finish();
        }

        // TODO: 아래는 로그인한 사용자 ID 값에 접근하는 방법 및 로그아웃에 대한 테스트 코드이므로
        //   구현 시에 참고만 하시고 적절하게 수정해주세요.

        tvUserId.setText(user.getEmail());

        btnSignOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            it = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(it);
            finish();
        });
    }
}
