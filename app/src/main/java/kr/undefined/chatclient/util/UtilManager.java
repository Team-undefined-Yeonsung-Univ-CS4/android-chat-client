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
}
