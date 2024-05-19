package kr.undefined.chatclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import kr.undefined.chatclient.R;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private ArrayList<String> chatList;

    // ChatRoomAdapter 생성자
    public ChatRoomAdapter(ArrayList<String> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    // ViewHolder 생성
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 오른쪽 텍스트 뷰 레이아웃을 inflate하여 새로운 뷰 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_text_view, parent, false);
        // 새로운 ViewHolder 객체 생성
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    // ViewHolder에 데이터 바인딩
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 해당 위치(position)의 채팅 메시지를 가져와 텍스트 뷰에 설정
        holder.messageTextView.setText(chatList.get(position));
    }

    @Override
    // 아이템 개수 반환
    public int getItemCount() {
        return chatList.size();
    }

    // 메시지 추가
    public void addMessage(String message) {
        // 채팅 메시지를 리스트에 추가
        chatList.add(message);
        // 새로운 아이템이 추가되었음을 알림
        notifyItemInserted(chatList.size() - 1);
    }

    // ViewHolder 클래스 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 뷰 홀더 내의 텍스트 뷰
        TextView messageTextView;

        // ViewHolder 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 아이템 뷰로부터 텍스트 뷰 참조
            messageTextView = itemView.findViewById(R.id.tvChat);
        }
    }
}

