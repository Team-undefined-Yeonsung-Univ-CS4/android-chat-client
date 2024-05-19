package kr.undefined.chatclient.manager;

public class ChatMessage {
    private String uid;
    private String message;

    public ChatMessage(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

