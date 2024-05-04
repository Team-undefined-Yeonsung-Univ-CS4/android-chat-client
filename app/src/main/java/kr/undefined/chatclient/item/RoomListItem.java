package kr.undefined.chatclient.item;

public class RoomListItem {
    private String title;
    private String members;

    public RoomListItem(String title, String members) {
        this.title = title;
        this.members = members;
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
