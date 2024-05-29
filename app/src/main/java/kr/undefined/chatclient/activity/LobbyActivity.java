package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.RoomListAdapter;
import kr.undefined.chatclient.item.RoomListItem;
import kr.undefined.chatclient.manager.ChatManager;

public class LobbyActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    Toolbar toolbar;
    RecyclerView rvRoomList;
    TextView tvConcurrentConnectors, tvUserNickname;
    ImageButton btnUserProfileImg;
    FrameLayout btnSearchingRoom, btnCreatingRoom;

    Intent it;

    private RoomListAdapter roomListAdapter;
    private ArrayList<RoomListItem> roomList = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            it = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        rvRoomList = findViewById(R.id.rv_room_list);
        tvConcurrentConnectors = findViewById(R.id.tv_concurrent_connectors_value);
        tvUserNickname = findViewById(R.id.tv_user_nickname);
        btnUserProfileImg = findViewById(R.id.ib_user_profile);
        btnSearchingRoom = findViewById(R.id.btn_searching_room);
        btnCreatingRoom = findViewById(R.id.btn_creating_room);

        rvRoomList.setLayoutManager(new LinearLayoutManager(this));

        // 더미 데이터 추가
        roomList.add(new RoomListItem("1", "제 귀여운 재귀함수좀 보실래요", "1/3"));
        roomList.add(new RoomListItem("2", "제가 싱글이라 싱글톤 패턴을 자주 써요", "2/2"));
        roomList.add(new RoomListItem("3", "전 html로 프로그래밍 해요", "7/8"));
        roomList.add(new RoomListItem("4", "챗지피티 주도 개발", "1/8"));
        roomList.add(new RoomListItem("5", "TDD는 죽었다", "2/8"));
        roomList.add(new RoomListItem("6", "비전공 엄랭 4개월차 쿠팡 취업 후기", "3/4"));
        roomList.add(new RoomListItem("7", "CSS 따위 안쓰는 사나이클럽", "2/4"));
        roomList.add(new RoomListItem("8", "님들 졸업하면 컴퓨터 파시는거죠", "2/4"));

        roomListAdapter = new RoomListAdapter(roomList);
        rvRoomList.setAdapter(roomListAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // 동시 접속자 수 표시
        tvConcurrentConnectors.setText("10");

        roomListAdapter.setOnItemClickListener(item -> {
            it = new Intent(getApplicationContext(), ChatRoomActivity.class);
            it.putExtra("roomId", item.getRoomId());
            startActivity(it);
        });

        btnUserProfileImg.setOnClickListener(view -> {
            // 마이 페이지로 이동
        });

        btnSearchingRoom.setOnClickListener(view -> {
            // 방 찾기 다이얼로그 생성
        });

        btnCreatingRoom.setOnClickListener(view -> {
            // 방 만들기 다이얼로그 생성
        });

        // 서버 연결
        ChatManager.getInstance().connectToServer();
    }
}


