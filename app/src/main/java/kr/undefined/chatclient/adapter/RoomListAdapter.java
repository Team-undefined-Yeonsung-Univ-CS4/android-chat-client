package kr.undefined.chatclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.item.RoomItem;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {
    private ArrayList<RoomItem> itemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RoomItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RoomListAdapter(ArrayList<RoomItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RoomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                onItemClickListener.onItemClick(itemList.get(position));
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListAdapter.ViewHolder holder, int position) {
        RoomItem item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMembers.setText(item.getMembers());

        String roomUid = item.getUid();

        loadProfileImage(roomUid, holder.ivManagerProfile);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivManagerProfile, ivPublic;
        TextView tvTitle, tvMembers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivManagerProfile = itemView.findViewById(R.id.iv_manager_profile);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMembers = itemView.findViewById(R.id.tv_members);
            ivPublic = itemView.findViewById(R.id.iv_public);
        }
    }
    private void loadProfileImage(String uid, ImageView imageView) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null) {
                        Glide.with(imageView.getContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_user)
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
