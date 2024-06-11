package kr.undefined.chatclient.activity;

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
import java.util.List;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.ChatRoomAdapter;
import kr.undefined.chatclient.manager.ChatMessage;
import kr.undefined.chatclient.manager.SocketManager;
import kr.undefined.chatclient.util.DialogManager;

public class ChatRoomActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    private Toolbar toolbar;
    private TextView tvTitle, tvUserNickname, tvNumOfPeople;
    private RecyclerView rvChatList;
    private EditText etMessageInput;
    private ImageButton btnFunction, btnSend, btnUserProfile;
    private FrameLayout btnParticipant;
    private ChatRoomAdapter chatAdapter;

    private static ChatRoomActivity instance;
    private static Handler uiHandler;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

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

        // FIXME: 방 제목 참조 & 현재 인원 수 실시간 업데이트
        tvTitle.setText("고독한 방");
        tvNumOfPeople.setText("1 / 8");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatList.setLayoutManager(layoutManager);

        roomId = getIntent().getStringExtra("roomId");

        List<ChatMessage> chatMessages = loadChatHistory(roomId); // 방 번호에 맞는 채팅 내역 불러오기
        chatAdapter = new ChatRoomAdapter(new ArrayList<>(chatMessages));
        rvChatList.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

        SocketManager.getInstance().setCurrentRoomId(roomId);

        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUserInfoAndShowDialog();
            }
        });

        loadProfileImage();
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
//                String uid = user.getUid();
                etMessageInput.setText("");
                SocketManager.getInstance().sendMessage(messageText);
            }
        }
    }

    public static void handleMessage(String roomId, String uid, String userName, String messageText, String currentTime) {
        uiHandler.post(() -> {
            if (instance != null && instance.roomId.equals(roomId)) {
                ChatMessage message = new ChatMessage(roomId, uid, userName, messageText, currentTime);
                instance.chatAdapter.addMessage(message);
                instance.rvChatList.scrollToPosition(instance.chatAdapter.getItemCount() - 1);
            }
        });
    }

    /**
     * 전체 채팅 리스트에서 방 번호에 맞는 채팅 내역을 필터링하여 반환하는 함수
     * @param roomId 방 번호
     * @return 해당 채팅방의 모든 채팅 기록
     */
    private List<ChatMessage> loadChatHistory(String roomId) {
        List<ChatMessage> allMessages = SocketManager.getInstance().getAllMessages();
        List<ChatMessage> roomMessages = new ArrayList<>();
        for (ChatMessage message : allMessages) {
            if (message.getRoomId().equals(roomId)) {
                roomMessages.add(message);
            }
        }
        return roomMessages;
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

    private void fetchUserInfoAndShowDialog() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    String nickname = snapshot.child("name").getValue(String.class);
                    String statusMessage = snapshot.child("statusMsg").getValue(String.class);

                    // 다이얼로그 호출
                    DialogManager.showMiniProfileDialog(ChatRoomActivity.this, profileImageUrl, nickname, statusMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
