package kr.undefined.chatclient.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.ChatRoomAdapter;
import kr.undefined.chatclient.manager.ChatManager;

public class ChatRoomActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvUserNickname, tvNumOfPeople;
    RecyclerView rvChatList;
    EditText etMessageInput;
    ImageButton btnFunction, btnSend, btnUserProfile;
    FrameLayout btnParticipant;
    ChatRoomAdapter chatAdapter;

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

        ArrayList<String> chatMessages = new ArrayList<>();
        chatAdapter = new ChatRoomAdapter(chatMessages);
        rvChatList.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

        ChatManager.getInstance(); // 인스턴스 생성 및 서버 연결
    }

    private void sendMessage() {
        String message = etMessageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatAdapter.addMessage(message);
            etMessageInput.setText("");
            rvChatList.scrollToPosition(chatAdapter.getItemCount() - 1);

            ChatManager.getInstance().sendMessage(message);
        }
    }

    public static void handleMessage(String message) {
        uiHandler.post(() -> {
            if (instance != null) {
                instance.chatAdapter.addMessage(message);
                instance.rvChatList.scrollToPosition(instance.chatAdapter.getItemCount() - 1);
            }
        });
    }
}
