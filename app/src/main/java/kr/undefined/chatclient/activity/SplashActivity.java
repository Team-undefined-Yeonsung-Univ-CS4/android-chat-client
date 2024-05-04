package kr.undefined.chatclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.undefined.chatclient.R;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rl_splash_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 애니메이션 로드
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);

        // 검은색 동그라미 뷰 참조
        View blackCircle = findViewById(R.id.v_black_circle);

        // 애니메이션 적용
        blackCircle.startAnimation(scaleAnimation);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 2000);
    }

    private class SplashHandler implements Runnable{
        @Override
        public void run() {

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            if (user == null) {
                startActivity(new Intent(getApplication(), LoginActivity.class));
                finish();
            }else{
                startActivity(new Intent(getApplication(), LobbyActivity.class));
                SplashActivity.this.finish();
            }


        }
    }
}