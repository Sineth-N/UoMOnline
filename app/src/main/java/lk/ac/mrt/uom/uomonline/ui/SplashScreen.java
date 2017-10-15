package lk.ac.mrt.uom.uomonline.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        final ImageView splashImageView = findViewById(R.id.splashImageView);
//        Picasso.with(this).load(R.mipmap.ic_launcher_2).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                splashImageView.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });
        AuthStateListener authStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (null != currentUser) {
                    Snackbar.with(SplashScreen.this, null)
                            .type(Type.ERROR)
                            .message("Not Logged In")
                            .duration(Duration.INFINITE);
                    runAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            intent.putExtra("Name",currentUser.getDisplayName());
                            intent.putExtra("Email",currentUser.getEmail());
                            intent.putExtra("Phone",currentUser.getPhoneNumber());
                            intent.putExtra("Image",currentUser.getPhotoUrl());
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
