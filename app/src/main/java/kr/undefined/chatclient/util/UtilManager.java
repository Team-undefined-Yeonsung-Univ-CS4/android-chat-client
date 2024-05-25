package kr.undefined.chatclient.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UtilManager {

    public static void clearFocus(Activity activity) {
        // 키보드 비활성화
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View rootView = activity.getWindow().getDecorView().getRootView();
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

        // 모든 포커스 상태 해제
        rootView.clearFocus();
    }

    /**
     * 이메일 형식 유효성 검사
     * @param email user email
     * @return userId@email.com 또는 co.국가코드
     */
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
