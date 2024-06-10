package kr.undefined.chatclient.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.ChatRoomAdapter;
import kr.undefined.chatclient.manager.ChatManager;
import kr.undefined.chatclient.manager.ChatMessage;
import kr.undefined.chatclient.util.DialogManager;

public class ChatRoomActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvUserNickname, tvNumOfPeople;
    RecyclerView rvChatList;
    EditText etMessageInput;
    ImageButton btnFunction, btnSend, btnUserProfile;
    FrameLayout btnParticipant;
    ChatRoomAdapter chatAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    private static ChatRoomActivity instance;
    private static Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        instance = this;
        uiHandler = new Handler(Looper.getMainLooper());

        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tv_title);
        tvUserNickname = findViewById(R.id.tv_user_nickname);
        tvNumOfPeople = findViewById(R.id.tv_numOfPeople);
        rvChatList = findViewById(R.id.rv_chat_list);
        etMessageInput = findViewById(R.id.et_message_input);
        btnFunction = findViewById(R.id.btn_function);
        btnSend = findViewById(R.id.btn_send);
        btnUserProfile = findViewById(R.id.ib_user_profile);
        btnParticipant = findViewById(R.id.btn_participant);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        tvTitle.setText("고독한 방");
        tvNumOfPeople.setText("1 / 8");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(layoutManager);

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        chatAdapter = new ChatRoomAdapter(chatMessages);
        rvChatList.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

        ChatManager.getInstance(); // 인스턴스 생성 및 서버 연결

        //프로필 클릭시 이벤트
        Context context = this; // Dialog 호출 시 필요
        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogManager.showParticipantsDialog(context);
            }
        });



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        loadProfileImage();
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                etMessageInput.setText("");

                ChatManager.getInstance().sendMessage(uid + ":" + messageText);
            }
        }
    }

    public static void handleMessage(String uid, String messageText) {
        uiHandler.post(() -> {
            if (instance != null) {
                ChatMessage message = new ChatMessage(uid, messageText);
                instance.chatAdapter.addMessage(message);
                instance.rvChatList.scrollToPosition(instance.chatAdapter.getItemCount() - 1);
            }
        });
    }

    private void loadProfileImage() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null) {
                        Glide.with(ChatRoomActivity.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_user)
                                .into(btnUserProfile);
                        btnUserProfile.setBackground(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

