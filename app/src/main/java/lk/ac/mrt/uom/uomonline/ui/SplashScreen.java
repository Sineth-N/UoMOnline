package lk.ac.mrt.uom.uomonline.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

import lk.ac.mrt.uom.uomonline.LoginActivity;
import lk.ac.mrt.uom.uomonline.MainActivity;
import lk.ac.mrt.uom.uomonline.R;


public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        AuthStateListener authStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (null != currentUser) {
                    Snackbar.with(SplashScreen.this, null)
                            .type(Type.ERROR)
                            .message("Not Logged In")
                            .duration(Duration.INFINITE)
                            .show();
                    runAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                            startActivity(intent);
                            SplashScreen.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                } else {
                    runAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                            startActivity(intent);
                            SplashScreen.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);


    }

    private void runAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.reset();
        TextView tv = (TextView) findViewById(R.id.splashText);
        tv.clearAnimation();
        tv.startAnimation(a);
    }
}
