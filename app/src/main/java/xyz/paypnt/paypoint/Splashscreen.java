package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import xyz.paypnt.paypoint.R;

public class Splashscreen extends AppCompatActivity {

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.splashscreen);

        ImageView pin = (ImageView) findViewById(R.id.ss_logoPin),
                text = (ImageView) findViewById(R.id.ss_logoText),
                car = (ImageView) findViewById(R.id.ss_car);
        View line = (View) findViewById(R.id.ss_line);
        LinearLayout allLogo = (LinearLayout) findViewById(R.id.ss_logo);


        //Delay for splashscreen
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splashscreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2500);

        //Animation
        pin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        allLogo.startAnimation(AnimationUtils.loadAnimation(Splashscreen.this, R.anim.fade_in));
        line.startAnimation(AnimationUtils.loadAnimation(Splashscreen.this, R.anim.fade_in));
        car.startAnimation(AnimationUtils.loadAnimation(Splashscreen.this, R.anim.car));
    }
}