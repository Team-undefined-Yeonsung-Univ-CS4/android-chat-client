package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.util.UtilManager;

public class MyPageActivity extends AppCompatActivity {
    private static final String AUTH = "FirebaseAuthCheck";
    private static final String DB = "FirebaseRealtimeDatabaseCheck";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private ConstraintLayout rootLayout;
    private Toolbar toolbar;
    private ImageView ivBannerImg, ivProfileImg;
    private EditText etNickName, etStatusMsg;
    private AppCompatButton btnUpload, btnRemove, btnSave, btnSignOut;

    private Intent it;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        toolbar = findViewById(R.id.toolbar);
        rootLayout = findViewById(R.id.root_layout);
        ivBannerImg = findViewById(R.id.iv_banner_img);
        ivProfileImg = findViewById(R.id.iv_profile_img);
        etNickName = findViewById(R.id.et_nickname);
        etStatusMsg = findViewById(R.id.et_status_msg);
//        btnUpload = findViewById(R.id.btn_upload);
//        btnRemove = findViewById(R.id.btn_remove);
//        btnSave = findViewById(R.id.btn_save);
        btnSignOut = findViewById(R.id.btn_sign_out);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        rootLayout.setOnClickListener(view -> UtilManager.clearFocus(this));
        btnSignOut.setOnClickListener(view -> signOut());
    }

    /**
     * 로그인 상태를 해제시키고 로그인 화면으로 이동
     */
    private void signOut() {
        auth.signOut();
        it = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(it);
        finish();
    }
}
