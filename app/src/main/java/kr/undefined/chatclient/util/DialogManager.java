package kr.undefined.chatclient.util;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;



import kr.undefined.chatclient.R;

public class DialogManager  {
    // TODO: 모든 다이얼로그 생성 함수는 여기에 구현해주세요.
    //  public static 함수로 지정 후, 다른 클래스에서는
    //  `DialogManager.functionName(params)` 과 같은 형태로 해당 함수를 호출하여 사용하시면 됩니다.



    public static void showParticipantsDialog(Context context) {
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
}
