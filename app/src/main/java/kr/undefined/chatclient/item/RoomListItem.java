package kr.undefined.chatclient.item;

public class RoomListItem {
    private String roomId;  // 추가된 부분
    private String title;
    private String members;

    public RoomListItem(String roomId, String title, String members) {  // 수정된 부분
        this.roomId = roomId;  // 추가된 부분
        this.title = title;
        this.members = members;
    }

    public String getRoomId() {  // 추가된 부분
        return roomId;
    }

    public void setRoomId(String roomId) {  // 추가된 부분
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

