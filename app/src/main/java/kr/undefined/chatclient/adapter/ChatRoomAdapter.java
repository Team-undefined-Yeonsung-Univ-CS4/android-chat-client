package kr.undefined.chatclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import kr.undefined.chatclient.R;
import kr.undefined.chatclient.manager.ChatMessage;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;
    private ArrayList<ChatMessage> chatList;

    public ChatRoomAdapter(ArrayList<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && chatList.get(position).getUid().equals(user.getUid())) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message,
                    parent, false);
            return new MyMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_message,
                    parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatList.get(position);
        String messageText = chatMessage.getMessage(); // 채팅 메시지를 가져옴
        String userName = chatMessage.getUserName(); // 사용자 닉네임을 가져옴
        if (holder.getItemViewType() == VIEW_TYPE_MY_MESSAGE) {
            ((MyMessageViewHolder) holder).bind(messageText);
        } else {
            ((OtherMessageViewHolder) holder).bind(messageText, userName);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void addMessage(ChatMessage message) {
        chatList.add(message);
        notifyItemInserted(chatList.size() - 1);
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_chat);
        }

        public void bind(String message) {
            messageTextView.setText(message);
        }
    }

    public static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView userNameTextView;

        public OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_chat);
            userNameTextView = itemView.findViewById((R.id.tv_user_nickname));
        }

        public void bind(String message, String username) {
            messageTextView.setText(message);
            userNameTextView.setText(username);
        }
    }
}



