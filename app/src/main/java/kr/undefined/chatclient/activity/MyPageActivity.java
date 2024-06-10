package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

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

    private Uri profileImgUri;
    private Uri bannerImgUri;

    private ConstraintLayout rootLayout;
    private Toolbar toolbar;
    private ImageView ivBannerImg, ivProfileImg;
    private EditText etNickName, etStatusMsg;
    private FrameLayout btnEditProfile, btnEditBanner;
    private ImageButton btnAliasWizard;
    private AppCompatButton btnSave, btnSignOut;

    private String currentImageType;

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

        btnEditBanner.setOnClickListener(view -> {
            currentImageType = "banner";
            selectImage();
        });

        btnEditProfile.setOnClickListener(view -> {
            currentImageType = "profile";
            selectImage();
        });

        btnAliasWizard.setOnClickListener(view -> etNickName.setText(createRandomAlias()));

        btnSave.setOnClickListener(view -> {
            uploadImages();
            updateUserData();
        });
        btnSignOut.setOnClickListener(view -> signOut());

        loadImages();
        loadUserData();
    }

    /**
     * 프로필 이미지 변경
     */
    private void editProfileImg() {
        // TODO: 기기 갤러리 참조하는 Activity가 있어야함
//        ivProfileImg
    }

    private void updateUserData() {
        String newNickName = etNickName.getText().toString();
        String newStatusMsg = etStatusMsg.getText().toString();

        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("UID");
            if (userId != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean needsUpdate = false;

                        String currentName = snapshot.child("name").getValue(String.class);
                        if (!newNickName.isEmpty() && (currentName == null || !currentName.equals(newNickName))) {
                            needsUpdate = true;
                            userRef.child("name").setValue(newNickName).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MyPageActivity.this, "이름 업데이트 성공", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyPageActivity.this, "이름 업데이트 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        String currentStatusMsg = snapshot.child("statusMsg").getValue(String.class);
                        if (!newStatusMsg.isEmpty() && (currentStatusMsg == null || !currentStatusMsg.equals(newStatusMsg))) {
                            userRef.child("statusMsg").setValue(newStatusMsg).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MyPageActivity.this, "상태 메시지 업데이트 성공", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyPageActivity.this, "상태 메시지 업데이트 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyPageActivity.this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void loadUserData() {
        user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null) {
                            etNickName.setText(name);
                        }
                        String statusMsg = snapshot.child("statusMsg").getValue(String.class);
                        if (statusMsg != null) {
                            etStatusMsg.setText(statusMsg);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (currentImageType.equals("profile")) {
                            profileImgUri = selectedImageUri;
                            ivProfileImg.setImageURI(profileImgUri);
                        } else if (currentImageType.equals("banner")) {
                            bannerImgUri = selectedImageUri;
                            ivBannerImg.setImageURI(bannerImgUri);
                        }
                    }
                }
            }
    );

    private void uploadImages() {
        user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            if (profileImgUri != null) {
                uploadImage(profileImgUri, "profile", userId);
            }
            if (bannerImgUri != null) {
                uploadImage(bannerImgUri, "banner", userId);
            }
        }
    }

    private void loadImages() {
        user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            loadImageFromFirebase("profile", userId, ivProfileImg);
            loadImageFromFirebase("banner", userId, ivBannerImg);
        }
    }

    private void loadImageFromFirebase(String imageType, String userId, ImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("Mypage/images/" + imageType + "/" + userId);

        int placeholderImage = (imageType.equals("profile")) ? R.drawable.ic_user : R.drawable.bn_gitjump;

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(MyPageActivity.this)
                    .load(uri)
                    .into(imageView);
            ivBannerImg.setBackground(null);
        }).addOnFailureListener(exception -> {
            Glide.with(MyPageActivity.this)
                    .load(placeholderImage)
                    .into(imageView);
        });
    }

    private void uploadImage(Uri imageUri, String imageType, String Uid) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Mypage/images/" + imageType + "/" + Uid);
        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // 이미지 업로드 성공 시 다운로드 URL 가져오기
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // 다운로드 URL을 리얼타임 데이터베이스에 저장
                        saveImageUrlToDatabase(uri.toString(), imageType, Uid);
                        Toast.makeText(MyPageActivity.this, imageType + " 이미지 업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(MyPageActivity.this, imageType + " 이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveImageUrlToDatabase(String imageUrl, String imageType, String Uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(Uid);
        if (imageType.equals("profile")) {
            userRef.child("profileImageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
            });
        } else if (imageType.equals("banner")) {
            userRef.child("bannerImageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
            });
        }
    }

    /**
     * 랜덤 닉네임 생성기
     */
    private String createRandomAlias() {
        String[] adjectives = { "불타는", "차가운", "뜨거운", "냉소적인", "흥분되는", "편안한", "끔찍한", "화끈한", "그만하고싶은", "어두컴컴한" };
        String[] languages = { "자바", "파이썬", "자바스크립트", "C++", "노드", "스위프트", "코틀린", "리액트", "스프링", "타입스크립트" };

        Random random = new Random();

        String randomAdjective = adjectives[random.nextInt(adjectives.length)];
        String randomLanguage = languages[random.nextInt(languages.length)];

        String randomAlias = randomAdjective + randomLanguage;

        return randomAlias;
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
