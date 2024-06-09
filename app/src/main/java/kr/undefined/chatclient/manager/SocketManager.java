package kr.undefined.chatclient.manager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import kr.undefined.chatclient.activity.ChatRoomActivity;
import kr.undefined.chatclient.activity.LobbyActivity;
import kr.undefined.chatclient.item.RoomItem;

public class SocketManager {
    private static final String SERVER_IP = "172.30.1.47";
    private static final int SERVER_PORT = 9998;

    private FirebaseUser user;
    private DatabaseReference userRef;

    private static SocketManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String roomId;

    private List<ChatMessage> allMessages = new ArrayList<>();
    private boolean isConnected = false;

    private LobbyActivity lobbyActivity;

    private Thread connectionThread;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void setLobbyActivity(LobbyActivity lobbyActivity) {
        this.lobbyActivity = lobbyActivity;
    }

    /**
     * 소켓 서버 연결 및 전체 메시지에 대한 핸들링
     */
    public void connectToServer() {
        if (isConnected) {
            return; // 이미 연결되어 있는 경우 새로운 연결을 생성하지 않음
        }

        connectionThread = new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                sendUserId();

                // 메시지 수신 루프
                String message;
                while ((message = in.readLine()) != null) {
                    handleMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 서버 연결 실패시 처리할 로직 추가
            }
        });
        connectionThread.start();
    }

    /**
     * 서버 소켓으로부터 수신되는 모든 메시지에 대한 처리 함수
     * @param message 서버 소켓으로부터 수신되는 메시지
     */
    private void handleMessage(String message) {
        // 동시 접속자 수
        if (message.startsWith("USER_COUNT")) {
            String userCnt = message.split(":")[1];
            lobbyActivity.updateUserCount(userCnt);
        }
        // 방 목록
        else if (message.startsWith("ROOM_LIST")) {
            handleRoomListMessage(message);
        }
        // 채팅방 메시지
        // FIXME: 상태 코드 "CHAT_MESSAGE"를 식별하는 로직 추가
        else {
            String[] parts = message.split(":", 4);
            String roomId = parts[0];
            String uid = parts[1];
            String userName = parts[2];
            String msg = parts[3];

            ChatMessage chatMessage = new ChatMessage(roomId, uid, userName, msg);
            allMessages.add(chatMessage); // 전체 메시지 리스트에 추가
            ChatRoomActivity.handleMessage(roomId, uid, userName, msg);
        }
    }

    /**
     * 서버로 사용자 ID 전송
     */
    private void sendUserId() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && out != null) {
            String uid = user.getUid();
            out.println(uid);
        }
    }

    /**
     * 방 생성 요청에 대한 소켓 메시지 전송 함수
     * @param message 채팅방 정보가 담긴 메시지
     */
    public void sendCreateRoomMessage(String message) {
        new Thread(() -> {
            if (out != null) {
                out.println(message);
            } else {
                // TODO: 메시지를 전송할 수 없는 경우에 대한 예외 처리
            }
        }).start();
    }

    /**
     * 채팅 메세지 전송 요청에 대한 소켓 메시지 전송 함수
     * @param message 사용자 입력 메시지
     */
    public void sendMessage(String message) {
        if (out != null) {
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String uid = user.getUid();

                fetchUserName(username -> {
                    if (username == null) {
                        // 계정 오류 등의 이유로 닉네임이 존재하지 않는 경우에 해당
                        username = "(알 수 없음)";
                    }
                    // 방 ID와 UID, 이름을 포함한 메시지 전송
                    String formattedMessage = roomId + ":" + uid + ":" + username + ":" + message;
                    new Thread(() -> out.println(formattedMessage)).start();
                });
            }
        } else {
            // TODO: 메시지를 전송할 수 없는 경우 예외 처리
        }
    }

    public void setCurrentRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<ChatMessage> getAllMessages() {
        return allMessages;
    }

    public void disconnectFromServer() {
        try {
            if (out != null) {
                out.println("DISCONNECT:" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Firebase Realtime Database에서 사용자 이름을 가져오는 함수
     * @param callback 사용자 이름을 가져온 후 실행할 콜백
     */
    private void fetchUserName(UsernameCallback callback) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // users > uid > name 노드 참조
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("name");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);
                    callback.onCallback(userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO: 읽기 실패 시 예외 처리
                }
            });
        }
    }

    public interface UsernameCallback {
        void onCallback(String username);
    }

    private void handleRoomListMessage(String message) {
        // 방마다 구분하기 위해 수신코드(상태코드) 부분은 제거
        String roomListData = message.substring("ROOM_LIST:".length());

        // 각 방 정보를 ';'로 구분하여 배열로 분리
        String[] roomEntries = roomListData.split(";");

        List<RoomItem> roomList = new ArrayList<>();
        for (String roomEntry : roomEntries) {
            String[] roomDetails = roomEntry.split(",");

            String roomId = roomDetails[0];
            String title = roomDetails[1];
            String members = roomDetails[2];
            roomList.add(new RoomItem(roomId, title, members));
        }

        if (lobbyActivity != null) {
            lobbyActivity.setRoomList(roomList);
        }
    }
}
