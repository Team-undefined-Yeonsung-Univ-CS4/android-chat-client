package kr.undefined.chatclient.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.undefined.chatclient.R;

public class ChatRoomActivity extends AppCompatActivity {

    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        tvTitle = findViewById(R.id.tv_title);

    }
}
