package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.util.UtilManager;

public class MyPageActivity extends AppCompatActivity {
    private static final String AUTH = "FirebaseAuthCheck";
    private static final String DB = "FirebaseRealtimeDatabaseCheck";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private Uri profileImgUri;
    private Uri bannerImgUri;
    private ImageView ivProfileImg;
    private ImageView ivBannerImg;
    private String currentImageType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        ivBannerImg = findViewById(R.id.iv_banner_img);
        ivProfileImg = findViewById(R.id.iv_profile_img);
        EditText etNickName = findViewById(R.id.et_nickname);
        EditText etStatusMsg = findViewById(R.id.et_status_msg);
        FrameLayout btnEditProfile = findViewById(R.id.btn_edit_profile);
        FrameLayout btnEditBanner = findViewById(R.id.btn_edit_banner);
        ImageButton btnAliasWizard = findViewById(R.id.btn_alias_wizard);
        AppCompatButton btnSave = findViewById(R.id.btn_save);
        AppCompatButton btnSignOut = findViewById(R.id.btn_sign_out);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

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
        btnSave.setOnClickListener(view -> uploadImages());
        btnSignOut.setOnClickListener(view -> signOut());
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
        Intent intent = getIntent();

        if (intent != null) {
            String userId = getIntent().getStringExtra("UID");
            if (userId != null) {
                if (profileImgUri != null) {
                    uploadImage(profileImgUri, "profile", userId);
                }
                if (bannerImgUri != null) {
                    uploadImage(bannerImgUri, "banner", userId);
                }

            }
        }

    }

    private void uploadImage(Uri imageUri, String imageType, String Uid) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Mypage/images/" + imageType + "/" + Uid + "_" + System.currentTimeMillis());
        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MyPageActivity.this, imageType + " 이미지 업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyPageActivity.this, imageType + " 이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestUpdateUserData() {
        // TODO: 사용자 데이터 업데이트 요청
    }

    private void signOut() {
        auth.signOut();
        Intent it = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(it);
        finish();
    }
}
