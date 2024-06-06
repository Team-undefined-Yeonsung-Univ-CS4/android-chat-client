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

public class ChatManager {
    private static ChatManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String roomId;
    private static final String SERVER_IP = "192.168.219.163"; // 서버의 IP 주소
    private static final int SERVER_PORT = 9998; // 서버의 포트 번호
    private List<ChatMessage> allMessages = new ArrayList<>();
    private boolean isConnected = false;

    private Thread connectionThread;

    private ChatManager() {}

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void connectToServer() { // 서버 연결
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

    private void handleMessage(String message) { //메시지 수신 기능
        // 메시지 처리
        String[] parts = message.split(":", 4);
        if (parts.length == 4) {
            String roomId = parts[0];
            String uid = parts[1];
            String userName = parts[2];
            String msg = parts[3];
            ChatMessage chatMessage = new ChatMessage(roomId, uid, userName, msg);
            allMessages.add(chatMessage); // 전체 메시지 리스트에 추가
            ChatRoomActivity.handleMessage(roomId, uid, userName, msg);
        }
    }

    public void sendMessage(String message) { // 메시지 송신 기능
        if (out != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String uid = user.getUid();
                String userName = "unknown"; //TODO : DB에서 사용자 닉네임 가져오기
                // 방 ID와 UID, 이름을 포함한 메시지 전송
                String formattedMessage = roomId + ":" + uid + ":" + userName + ":" + message;
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


    public void disconnectFromServer() { //서버 연결 종료
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



