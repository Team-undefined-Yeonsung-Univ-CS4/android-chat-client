package kr.undefined.chatclient.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import kr.undefined.chatclient.activity.ChatRoomActivity;

public class ChatManager {
    private static ChatManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_IP = "192.168.219.109"; // 서버의 IP 주소
    private static final int SERVER_PORT = 9998; // 서버의 포트 번호

    private ChatManager() {
        connectToServer();
    }

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                listenForMessages();
            } catch (IOException e) {
                e.printStackTrace();
                // 서버 연결 실패시 처리할 로직 추가
            }
        }).start();
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // 수신한 메시지를 UID와 메시지로 분리
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        String uid = parts[0];
                        String msg = parts[1];
                        ChatRoomActivity.handleMessage(uid, msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 메시지 수신 실패시 처리할 로직 추가
            }
        }).start();
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            if (out != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    // UID와 메시지를 콜론으로 연결
                    String formattedMessage = uid + ":" + message;
                    out.println(formattedMessage);
                }
            } else {
                // 메시지를 보낼 수 없는 경우 처리 로직
            }
        }).start();
    }
}
