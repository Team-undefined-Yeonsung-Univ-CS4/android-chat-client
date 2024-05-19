package kr.undefined.chatclient.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration; // DividerItemDecoration 추가
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.ChatRoomAdapter;

public class ChatRoomActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvUserNickname, tvNumOfPeople;
    RecyclerView rvChatList;
    EditText etMessageInput;
    ImageButton btnFunction, btnSend, btnUserProfile;
    FrameLayout btnParticipant;
    ChatRoomAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

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

        //TODO: 추후 데이터 가져오는 코드 추가
        tvTitle.setText("고독한 방");
        tvNumOfPeople.setText("1 / 8");

        // RecyclerView에 대한 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(layoutManager);

        // 채팅 메시지를 담을 리스트 생성 및 어댑터 설정
        ArrayList<String> chatMessages = new ArrayList<>();
        chatAdapter = new ChatRoomAdapter(chatMessages);
        rvChatList.setAdapter(chatAdapter);

        // Divider 추가 (아이템 간의 간격 설정)
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rvChatList.getContext(), layoutManager.getOrientation());
        rvChatList.addItemDecoration(itemDecoration);

        // 전송 버튼 클릭 시 동작
        btnSend.setOnClickListener(v -> sendMessage());


    }

    // 메시지 전송 처리
    private void sendMessage() {
        String message = etMessageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // 메시지가 비어 있지 않으면 어댑터를 통해 RecyclerView에 추가
            chatAdapter.addMessage(message);
            etMessageInput.setText(""); // 입력 필드 초기화
            // RecyclerView를 최하단으로 스크롤
            rvChatList.scrollToPosition(chatAdapter.getItemCount() - 1);
            // TODO: 여기에 메시지를 서버로 보내는 코드를 추가
        }
    }
}



