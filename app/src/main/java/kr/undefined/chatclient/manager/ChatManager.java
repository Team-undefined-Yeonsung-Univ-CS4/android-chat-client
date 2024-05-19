package kr.undefined.chatclient.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import kr.undefined.chatclient.activity.ChatRoomActivity;

public class ChatManager {
    private static ChatManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_IP = "172.30.1.93"; // 편의점
//    private static final String SERVER_IP = "192.168.1.100"; // 서버의 IP 주소
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
                    ChatRoomActivity.handleMessage(message);
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
                out.println(message);
            } else {
                // 메시지를 보낼 수 없는 경우 처리 로직
            }
        }).start();
    }
}
