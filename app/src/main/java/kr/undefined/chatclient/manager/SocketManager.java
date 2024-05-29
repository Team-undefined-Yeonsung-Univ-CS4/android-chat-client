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
import kr.undefined.chatclient.activity.ChatRoomActivity;
import kr.undefined.chatclient.activity.LobbyActivity;

public class SocketManager {private static final String SERVER_IP = "172.30.1.47"; // 서버의 IP 주소

    private static final int SERVER_PORT = 9998;

    private FirebaseUser user;

    private static SocketManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String roomId;

    private List<ChatMessage> allMessages = new ArrayList<>();
    private boolean isConnected = false;

    private LobbyActivity lobbyActivity;

    private Thread connectionThread;

//    private SocketManager() {
//        connectToServer();
//    }
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

//    private void connectToServer() {
//        new Thread(() -> {
//            try {
//                socket = new Socket(SERVER_IP, SERVER_PORT);
//                out = new PrintWriter(socket.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                sendUserId();
//                listenForMessages();
//            } catch (IOException e) {
//                e.printStackTrace();
//                // 서버 연결 실패시 처리할 로직 추가
//            }
//        }).start();
//    }
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

    private void handleMessage(String message) {
        // 메시지 처리
        String[] parts = message.split(":", 3);
        if (parts.length == 3) {
            String roomId = parts[0];
            String uid = parts[1];
            String msg = parts[2];
            ChatMessage chatMessage = new ChatMessage(roomId, uid, msg);
            allMessages.add(chatMessage); // 전체 메시지 리스트에 추가
            ChatRoomActivity.handleMessage(roomId, uid, msg);
        }
    }

    // FIXME: 변경사항에 맞게 적용
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

//    private void listenForMessages() {
//        new Thread(() -> {
//            try {
//                String message;
//                while ((message = in.readLine()) != null) {
//                    if (message.startsWith("USER_COUNT:")) {
//                        String userCnt = message.split(":")[1];
//
//                        if (lobbyActivity != null) {
//                            // 현재 접속자 수 업데이트
//                            lobbyActivity.updateUserCount(userCnt);
//                        }
//                    }
//                    // 수신한 메시지를 UID와 메시지로 분리
//                    String[] parts = message.split(":", 2);
//                    if (parts.length == 2) {
//                        String uid = parts[0];
//                        String msg = parts[1];
//                        ChatRoomActivity.handleMessage(uid, msg);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                // 메시지 수신 실패시 처리할 로직 추가
//            }
//        }).start();
//    }

//    public void sendMessage(String message) {
//        new Thread(() -> {
//            if (out != null) {
//                user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    String uid = user.getUid();
//                    // UID와 메시지를 콜론으로 연결
//                    String formattedMessage = uid + ":" + message;
//                    out.println(formattedMessage);
//                }
//            } else {
//                // 메시지를 보낼 수 없는 경우 처리 로직
//            }
//        }).start();
//    }
    public void sendMessage(String message) {
        if (out != null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                // 방 ID와 UID를 포함한 메시지 전송
                String formattedMessage = roomId + ":" + uid + ":" + message;
                new Thread(() -> out.println(formattedMessage)).start();
            }
        } else {
            // 메시지를 보낼 수 없는 경우 처리 로직
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
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



