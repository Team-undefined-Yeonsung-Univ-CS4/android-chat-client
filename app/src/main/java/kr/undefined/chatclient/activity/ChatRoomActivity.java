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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.ChatRoomAdapter;
import kr.undefined.chatclient.manager.SocketManager;
import kr.undefined.chatclient.manager.ChatMessage;
import kr.undefined.chatclient.util.DialogManager;

public class ChatRoomActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle, tvUserNickname, tvNumOfPeople;
    private RecyclerView rvChatList;
    private EditText etMessageInput;
    private ImageButton btnFunction, btnSend, btnUserProfile;
    private FrameLayout btnParticipant;
    private ChatRoomAdapter chatAdapter;

    private static ChatRoomActivity instance;
    private static Handler uiHandler;

    static {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        instance = this;

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

        SocketManager.getInstance(); // 인스턴스 생성 및 서버 연결

        //프로필 클릭시 이벤트
        Context context = this; // Dialog 호출 시 필요
        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogManager.showParticipantsDialog(context);
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                etMessageInput.setText("");

                SocketManager.getInstance().sendMessage(uid + ":" + messageText);
            }
        }
    }

    public static void handleMessage(String uid, String messageText) {
        if (uiHandler != null) {
            uiHandler.post(() -> {
                if (instance != null) {
                    ChatMessage message = new ChatMessage(uid, messageText);
                    instance.chatAdapter.addMessage(message);
                    instance.rvChatList.scrollToPosition(instance.chatAdapter.getItemCount() - 1);
                }
            });
        }
    }
}

