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

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.adapter.RoomListAdapter;
import kr.undefined.chatclient.item.RoomListItem;

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

        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
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
        /******************************** 테스트용 더미 코드 ********************************/
        roomList.add(new RoomListItem("제 귀여운 재귀함수좀 보실래요", "1/3"));
        roomList.add(new RoomListItem("싱글이라 싱글톤 패턴만 씁니다", "2/2"));
        roomList.add(new RoomListItem("전 html로 프로그래밍 해요", "7/8"));
        roomList.add(new RoomListItem("챗지피티 주도 개발", "1/8"));
        roomList.add(new RoomListItem("TDD는 죽었다", "2/8"));
        roomList.add(new RoomListItem("님들 여기 뭐하는데임", "2/4"));
        roomList.add(new RoomListItem("졸업하면 컴퓨터 파시는거죠?", "2/4"));
        /**********************************************************************************/
        roomListAdapter = new RoomListAdapter(roomList);
        rvRoomList.setAdapter(roomListAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // TODO: 동시 접속자 수 표시 (실시간 업데이트 반영)
        tvConcurrentConnectors.setText("10"); // 테스트용 더미코드

        roomListAdapter.setOnItemClickListener(item -> {
            // TODO: 방 입장 프로세스 다이얼로그 생성
        });

        // TODO: 사용자 닉네임 할당 (기본 값은 이메일 부분이 제외된 ID)
        tvUserNickname.setText(currentUser.getEmail());

        btnUserProfileImg.setOnClickListener(view -> {
            // TODO: 마이 페이지로 이동
        });

        btnSearchingRoom.setOnClickListener(view -> {
            // TODO: 방 찾기 다이얼로그 생성
        });

        btnCreatingRoom.setOnClickListener(view -> {
            // TODO: 방 만들기 다이얼로그 생성
        });
    }
}

