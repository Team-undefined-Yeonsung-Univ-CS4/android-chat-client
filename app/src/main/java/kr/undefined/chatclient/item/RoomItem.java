package kr.undefined.chatclient.item;

public class RoomItem {
    private String roomId;
    private String title;
    private String members;

    public RoomItem(String roomId, String title, String members) {
        this.roomId = roomId;
        this.title = title;
        this.members = members;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
}
