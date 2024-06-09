package kr.undefined.chatclient.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;

import java.util.Random;

import kr.undefined.chatclient.R;
import kr.undefined.chatclient.manager.SocketManager;

public class DialogManager  {
    private static final String TAG = "SocketNetworkCheck";

    public static void showMiniProfileDialog(Context context) {
        // 다이얼로그 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_profile, null);
        builder.setView(dialogView);

        // 다이얼로그의 버튼 클릭 리스너 설정
        AppCompatButton btnBack = dialogView.findViewById(R.id.btn_back);

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();
        dialog.show();

        // `뒤로가기` 버튼 클릭 시 다이얼로그 닫기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void showCreateRoomDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_room, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etPassword = dialogView.findViewById(R.id.et_password);
        Spinner spMembers = dialogView.findViewById(R.id.sp_members);
        AppCompatButton btnBack = dialogView.findViewById(R.id.btn_back);
        AppCompatButton btnCreate = dialogView.findViewById(R.id.btn_create);

        Integer[] members = new Integer[]{2, 3, 4, 5, 6, 7, 8};
        ArrayAdapter<Integer> spMembersAdpt = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, members);
        spMembersAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMembers.setAdapter(spMembersAdpt);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnBack.setOnClickListener(view -> dialog.dismiss());

        btnCreate.setOnClickListener(view -> {
            Log.d(TAG, "showCreateRoomDialog: has called");
            String title = etTitle.getText().toString().trim();

            // 방 제목이 비어있거나 공백만 포함된 경우 지정된 제목을 랜덤 할당
            if (title.isEmpty()) {
                String[] randomTitles = {
                        "제 귀여운 재귀함수좀 보실래요",
                        "제가 싱글이라 싱글톤 패턴을 자주 써요",
                        "전 html로 프로그래밍 해요",
                        "챗지피티 주도 개발",
                        "TDD는 죽었다",
                        "비전공 엄랭 4개월차 쿠팡 취업 후기",
                        "CSS 따위 안쓰는 사나이클럽",
                        "님들 졸업하면 컴퓨터 파시는거죠",
                };
                Random random = new Random();
                int i = random.nextInt(randomTitles.length);
                title = randomTitles[i];
                etTitle.setText(title);
            }

            int membersCnt = (int) spMembers.getSelectedItem();

            // TODO: password 처리 추가

            // "statusCode:title:members"를 소켓 서버로 전송
            String message = "109:" + title + ":" + membersCnt;
            Log.d(TAG, "showCreateRoomDialog's message: "+ message);
            SocketManager.getInstance().sendCreateRoomMessage(message);
        });
    }
}
