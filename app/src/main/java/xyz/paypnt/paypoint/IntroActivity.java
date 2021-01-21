package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    int position = 0 ;
    Animation btnAnim ;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);// ini views
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        // fill list screen for the Lay
        final List<ScreenItem> mList = new ArrayList<>();
        switch(getIntent().getIntExtra("position",-1)) {
            case 0:
                //Forgot Password
                mList.add(new ScreenItem("Forgot Password", "Go to the Log In screen", R.drawable.asset_htu00_00));
                mList.add(new ScreenItem("Forgot Password", "Press \"Forgot Password\"", R.drawable.asset_htu00_00));
                mList.add(new ScreenItem("Forgot Password", "Enter your email\nYou will receive an email from us\nNOTE: Please check both your Inbox and Spam!", R.drawable.asset_htu00_01));
                mList.add(new ScreenItem("Forgot Password", "Follow the instructions indicated in the email", R.drawable.asset_htu00_02));
                mList.add(new ScreenItem("Forgot Password", "Return to the application and enter your new password!", R.drawable.asset_htu00_03));
                break;
            case 1:
                //Booking and Payment
                mList.add(new ScreenItem("Booking and Payment", "Go to dashboard and select \"Book Now!\"", R.drawable.asset_htu01_00));
                mList.add(new ScreenItem("Booking and Payment", "Enter your origin and desired location", R.drawable.asset_htu01_02));
                mList.add(new ScreenItem("Booking and Payment", "Select your preferred transportation and select next", R.drawable.asset_htu01_03));
                mList.add(new ScreenItem("Booking and Payment", "Review your bill and select \"Confirm\" to proceed with the booking process\nIf you wish to decline simply select \"Decline\"", R.drawable.asset_htu01_04));
                mList.add(new ScreenItem("Booking and Payment", "After confirming the payment, you will be asked to scan the driver's QR Code then press \"Pay\"", R.drawable.asset_htu01_05));
                mList.add(new ScreenItem("Booking and Payment", "You will be brought back to the dashboard and the transaction will recorded in your History", R.drawable.asset_htu01_06));
                break;
            case 2:
                //TopUp
                mList.add(new ScreenItem("Top Up Balance", "Go to dashboard and press the (+) button next to your balance", R.drawable.asset_htu02_00));
                mList.add(new ScreenItem("Top Up Balance", "Enter the amount you will top up and select pay", R.drawable.asset_htu02_01));
                mList.add(new ScreenItem("Top Up Balance", "Enter your email address and select confirm. DragonPay will send you an email with the instructions", R.drawable.asset_htu02_02));
                mList.add(new ScreenItem("Top Up Balance", "If the payment was successful, your balance will be credited and the transaction will be recorded in your Top Up History located in the Top Up menu", R.drawable.asset_htu02_04));
                break;
            case 3:
                //ChangePW
                mList.add(new ScreenItem("Changing Password", "Open the hamburger menu and select \"Password\"", R.drawable.asset_htu03_00));
                mList.add(new ScreenItem("Changing Password", "Enter your old password, new password, and confirm the new password", R.drawable.asset_htu03_01));
                mList.add(new ScreenItem("Changing Password", "Press \"Change Password\" and wait for the process to finalize", R.drawable.asset_htu03_02));
                break;
            case 4:
                //RegisterDriver
                mList.add(new ScreenItem("Register as Driver", "Go to dashboard and select register", R.drawable.asset_htu04_00));
                mList.add(new ScreenItem("Register as Driver", "Fill out the form with your information", R.drawable.asset_htu04_01));
                mList.add(new ScreenItem("Register as Driver", "Upload Two(2) IDs - Driver's License and PUV ID", R.drawable.asset_htu04_02));
                mList.add(new ScreenItem("Register as Driver", "Press \"Submit\" and wait for your data to be uploaded", R.drawable.asset_htu04_03));
                mList.add(new ScreenItem("Register as Driver", "You will be redirected back to the dashboard, please wait until an administrator reviews your application", R.drawable.asset_htu04_04));
                break;
            case 5:
                //ContactUS
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","tneck2020@gmail.com", null));
                email.putExtra(Intent.EXTRA_SUBJECT, "Support - "+mAuth.getUid());
                email.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                finish();
                break;
        }
        mList.add(new ScreenItem("Got more questions?", "END-CONTACT", R.drawable.ic_questionmark));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tab-layout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // tab-layout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {
        tabIndicator.setVisibility(View.VISIBLE);

    }
}