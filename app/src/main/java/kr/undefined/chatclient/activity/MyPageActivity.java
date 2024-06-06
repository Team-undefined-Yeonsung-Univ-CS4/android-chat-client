package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import kr.undefined.chatclient.manager.SocketManager;
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
    private FrameLayout btnEditProfile, btnEditBanner;
    private ImageButton btnAliasWizard;
    private AppCompatButton btnSave, btnSignOut;

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
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditBanner = findViewById(R.id.btn_edit_banner);
        btnAliasWizard = findViewById(R.id.btn_alias_wizard);
        btnSave = findViewById(R.id.btn_save);
        btnSignOut = findViewById(R.id.btn_sign_out);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        // TODO: 저장하기 버튼은 기본 비활성화 상태로 유지, 변경사항이 생기면 활성화
//        btnSave

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        rootLayout.setOnClickListener(view -> UtilManager.clearFocus(this));
        btnEditBanner.setOnClickListener(view -> editBannerImg());
        btnEditProfile.setOnClickListener(view -> editProfileImg());
        btnAliasWizard.setOnClickListener(view -> createRandomAlias());
        btnSave.setOnClickListener(view -> requestUpdateUserData());
        btnSignOut.setOnClickListener(view -> signOut());
    }

    /**
     * 배너 이미지 변경
     */
    private void editBannerImg() {
        // TODO: 기기 갤러리 참조하는 Activity가 있어야함
//        ivBannerImg
    }

    /**
     * 프로필 이미지 변경
     */
    private void editProfileImg() {
        // TODO: 기기 갤러리 참조하는 Activity가 있어야함
//        ivProfileImg
    }

    /**
     * 랜덤 닉네임 생성기
     */
    private void createRandomAlias() {
        // TODO: 형용사 + 명사 (10 x 10) 랜덤 닉네임 생성
    }

    /**
     * Firebase Realtime DB에 사용자 프로필 정보에 대한 모든 변경사항을 적용
     */
    private void requestUpdateUserData() {
        // TODO: 사용자 데이터 업데이트 요청
    }

    /**
     * 로그인 상태를 해제시키고 로그인 화면으로 이동
     */
    private void signOut() {

        // 네트워크 작업은 별도의 스레드에서 수행해야 함
        // 메인 스레드에서 수행 시 NetworkOnMainThreadException 발생함
        new Thread(() -> {
            SocketManager.getInstance().disconnectFromServer(); // 소켓 통신 종료

            runOnUiThread(() -> {
                auth.signOut(); // Firebase Auth 로그아웃

                it = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            });
        }).start();
    }
}
