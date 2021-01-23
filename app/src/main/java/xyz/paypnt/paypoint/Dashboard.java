package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*

PAYPOINT 2020-2021
Accounts:

User: tneckuser@gmail.com | 123456
Driver: tneckdriver@gmail.com | 123456
Admin: tneck2020@gmail.com | 123456

*/

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Button getStarted;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("Dashboard");
        setContentView(R.layout.dashboard);

        startService(new Intent(this, driverNotif.class));

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        dbRef.addListenerForSingleValueEvent(vel);
        if(mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Session has expired. Please log-in again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Dashboard.this, MainActivity.class));
        }

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button dashboard_book=(Button)findViewById(R.id.dashboard_book);
        Button reg = (Button) findViewById(R.id.dashboard_register);
        Button topup = (Button) findViewById(R.id.dashboard_topup);

        dashboard_book.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return;
            } else startActivity(new Intent(Dashboard.this, MapActivity.class));
        });
        reg.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, DriverRegister.class).putExtra("Username", username)));
        topup.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, TopUp.class)));

        Button price = (Button) findViewById(R.id.dashboard_toggle);
        Button drivers = (Button) findViewById(R.id.dashboard_drivers);
        Button applicants = (Button) findViewById(R.id.dashboard_applicants);
        Button rejOK = (Button) findViewById(R.id.dashboard_confirmStatus);
        Button driverGetStarted = (Button) findViewById(R.id.dashboard_driverGetStarted);
        getStarted = (Button) findViewById(R.id.dashboard_getStarted);
        price.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, TogglePrice.class)));
        drivers.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, Drivers.class)));
        applicants.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, Applicants.class)));
        rejOK.setOnClickListener(v -> {
            dbRef.child("Driver Info").removeValue();
            ((LinearLayout) findViewById(R.id.dashboard_process)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.dashboard_apply)).setVisibility(View.VISIBLE);
        });
        driverGetStarted.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            else {
                Intent intent = new Intent(Dashboard.this, GetStarted.class);
                startActivity(intent);
            }
        });
        getStarted.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            else {
                dbRef.child("Driver Info").child("Status").removeValue();
                Intent intent = new Intent(Dashboard.this, GetStarted.class);
                startActivity(intent);
            }
        });
    }

    private ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            TextView bal = (TextView) findViewById(R.id.dashboard_bal), user = (TextView) findViewById(R.id.dashboard_user);
            String uName = String.valueOf(snapshot.child("Username").getValue());
            String balance = String.format("%.2f",Double.parseDouble(snapshot.child("Balance").getValue().toString()));
            bal.setText(balance);
            user.setText("Welcome, "+ uName);
            System.out.println(uName);
            username = uName;

            LinearLayout lnrDriver =(LinearLayout) findViewById(R.id.dashboard_driver),
                    lnrProcess = (LinearLayout) findViewById(R.id.dashboard_process),
                    lnrUser = (LinearLayout) findViewById(R.id.dashboard_apply);
            ConstraintLayout lnrAdmin = (ConstraintLayout) findViewById(R.id.dashboard_admin);

            lnrDriver.setVisibility(View.GONE);
            lnrProcess.setVisibility(View.GONE);
            lnrAdmin.setVisibility(View.GONE);
            lnrUser.setVisibility(View.GONE);
            getStarted.setVisibility(View.GONE);
            ((Button) findViewById(R.id.dashboard_confirmStatus)).setVisibility(View.GONE);

            if(snapshot.child("Driver Info").child("Status").exists()) {
                String status = snapshot.child("Driver Info").child("Status").getValue().toString();
                ((TextView) findViewById(R.id.dashboard_status)).setText(status);
                lnrProcess.setVisibility(View.VISIBLE);
                TextView titleProc = (TextView) findViewById(R.id.dashboard_procTxt);

                if(status.equals("Approved")) {
                    titleProc.setText("Your Application has been Processed!");
                    getStarted.setVisibility(View.VISIBLE);
                }
                else if(status.equals("Rejected")) {
                    titleProc.setText("Your Application has been Processed!");
                    ((Button) findViewById(R.id.dashboard_confirmStatus)).setVisibility(View.VISIBLE);
                }
            }
            else if(snapshot.child("Type").getValue().toString().equals("Driver"))
                lnrDriver.setVisibility(View.VISIBLE);
            else if(snapshot.child("Type").getValue().toString().equals("Admin"))
                lnrAdmin.setVisibility(View.VISIBLE);
            else
                lnrUser.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(CommonFunctions.menu(this, item, "Dashboard"))
            finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        dbRef.addListenerForSingleValueEvent(vel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbRef.removeEventListener(vel);
    }
}