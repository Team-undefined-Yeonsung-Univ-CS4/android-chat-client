package kr.undefined.chatclient.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.undefined.chatclient.activity.ChatRoomActivity;
import kr.undefined.chatclient.activity.LobbyActivity;

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
        // 메시지 처리
        String[] parts = message.split(":", 4);

        // FIXME: 각기 다른 소켓 메시지 처리에 대한 구분을 상태코드로 식별하도록 수정
        if (message.startsWith("USER_COUNT")) {
            String userCnt = message.split(":")[1];
            lobbyActivity.updateUserCount(userCnt);
        } else if (parts.length == 4) {
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
     * 메세지 전송
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
}
