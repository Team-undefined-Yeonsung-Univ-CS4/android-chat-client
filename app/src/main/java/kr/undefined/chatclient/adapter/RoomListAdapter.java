package kr.undefined.chatclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.item.RoomListItem;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {
    private ArrayList<RoomListItem> itemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RoomListItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RoomListAdapter(ArrayList<RoomListItem> itemList) {
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
        RoomListItem item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMembers.setText(item.getMembers());
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
}
