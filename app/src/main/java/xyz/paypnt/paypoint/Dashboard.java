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


public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private static final String channelPayment = "Payment Channel";
    private NotificationManagerCompat mNotificationManager;
    private Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("Dashboard");
        setContentView(R.layout.dashboard);


        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        dbRef.addListenerForSingleValueEvent(vel);

        Button dashboard_book=(Button)findViewById(R.id.dashboard_book);
        dashboard_book.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return;
            } else startActivity(new Intent(Dashboard.this, MapActivity.class));
        });

        Button reg = (Button) findViewById(R.id.dashboard_register);
        reg.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, DriverRegister.class)));

        Button topup = (Button) findViewById(R.id.dashboard_topup);
        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, TopUp.class));
            }
        });

        Button price = (Button) findViewById(R.id.dashboard_toggle);
        Button drivers = (Button) findViewById(R.id.dashboard_drivers);
        Button applicants = (Button) findViewById(R.id.dashboard_applicants);
        price.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, TogglePrice.class)));
        drivers.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, Drivers.class)));
        applicants.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, Applicants.class)));


//        createNotificationChannel();

        getStarted = (Button) findViewById(R.id.dashboard_getStarted);
        getStarted.setOnClickListener(v -> {
            /*
            Notification builder = new NotificationCompat.Builder(this, channelPayment)
                    .setSmallIcon(R.drawable.logo_black_inside)
                    .setContentTitle("Received Payment")
                    .setContentText("Sample User has paid Php100.00")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Sample User(UID) has paid Php100.00\nSource: University of Pangasinan\nDestination: Bonuan Tondaligan"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            mNotificationManager = NotificationManagerCompat.from(this);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, builder);*/

            Toast.makeText(this, "Hello?", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            else {
                Intent intent = new Intent(Dashboard.this, GetStarted.class);
                startActivity(intent);
            }

        });
    }

//    private void createNotificationChannel() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel paymentchannel = new NotificationChannel(
//                    channelPayment,
//                    "Payment Received",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            paymentchannel.setDescription("Channel for Notifying driver if payment has been received");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(paymentchannel);
//        }
//    }

    private ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String uName = String.valueOf(snapshot.child("Username").getValue());
            String balance = String.format("%.2f",Double.parseDouble(snapshot.child("Balance").getValue().toString()));
            TextView bal = (TextView) findViewById(R.id.dashboard_bal), user = (TextView) findViewById(R.id.dashboard_user);
            bal.setText(balance);
            user.setText("Welcome, "+ uName);
            System.out.println(uName);

            LinearLayout lnrDriver =(LinearLayout) findViewById(R.id.dashboard_driver),
                    lnrProcess = (LinearLayout) findViewById(R.id.dashboard_process),
                    lnrUser = (LinearLayout) findViewById(R.id.dashboard_apply);
            ConstraintLayout lnrAdmin = (ConstraintLayout) findViewById(R.id.dashboard_admin);

            lnrDriver.setVisibility(View.GONE);
            lnrProcess.setVisibility(View.GONE);
            lnrAdmin.setVisibility(View.GONE);
            lnrUser.setVisibility(View.GONE);
            getStarted.setVisibility(View.GONE);

            if(snapshot.child("Type").getValue().toString().equals("Driver"))
                lnrDriver.setVisibility(View.VISIBLE);
            else if(snapshot.child("Type").getValue().toString().equals("Admin"))
                lnrAdmin.setVisibility(View.VISIBLE);
            else if(snapshot.child("Driver Info").child("Status").exists()) {
                String status = snapshot.child("Driver Info").child("Status").getValue().toString();
                ((TextView) findViewById(R.id.dashboard_status)).setText(status);
                lnrProcess.setVisibility(View.VISIBLE);
                if(status.equals("Approved"))
                    getStarted.setVisibility(View.VISIBLE);
                else if(status.equals("Denied"))
                    ((Button) findViewById(R.id.dashboard_confirmStatus)).setVisibility(View.VISIBLE);
            }
            else {
                lnrUser.setVisibility(View.VISIBLE);
            }
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