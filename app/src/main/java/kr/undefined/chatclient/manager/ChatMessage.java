package kr.undefined.chatclient.manager;

public class ChatMessage {
    private String roomId;
    private String uid;
    private String message;
    private String userName;

    public ChatMessage(String roomId, String uid, String userName, String message) {
        this.roomId = roomId;
        this.uid = uid;
        this.message = message;
        this.userName = userName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }
    public String getUserName() { return userName; }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public  void setUserName(String userName) { this.userName = userName; }
}
