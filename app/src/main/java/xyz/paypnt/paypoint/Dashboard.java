package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
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
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("Dashboard");
        setContentView(R.layout.dashboard);
//        String id = (new SimpleDateFormat("dd").format(Calendar.getInstance().get)) +"/"+ (new SimpleDateFormat("MM").format(Calendar.getInstance().DAY_OF_MONTH)) + "/"+(new SimpleDateFormat("yyyy").format(Calendar.getInstance().YEAR)) + " "+(Calendar.getInstance().getTime().getTime());
        String id = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa")).format(Calendar.getInstance().getTime());
        Log.d(String.valueOf(Dashboard.this), id);
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        dbRef.addListenerForSingleValueEvent(vel);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button dashboard_book=(Button)findViewById(R.id.dashboard_book);
        dashboard_book.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return;
            } else startActivity(new Intent(Dashboard.this,map_activity.class));
        });

        Button reg = (Button) findViewById(R.id.dashboard_register);
        reg.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, DriverRegister.class)));

    }

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
                    lnrAdmin = (LinearLayout) findViewById(R.id.dashboard_admin),
                    lnrUser = (LinearLayout) findViewById(R.id.dashboard_apply);

            lnrDriver.setVisibility(View.GONE);
            lnrProcess.setVisibility(View.GONE);
            lnrAdmin.setVisibility(View.GONE);
            lnrUser.setVisibility(View.GONE);

            if(snapshot.child("Type").getValue().toString().equals("Driver"))
                lnrDriver.setVisibility(View.VISIBLE);
            else if(snapshot.child("Type").getValue().toString().equals("Driver"))
                lnrAdmin.setVisibility(View.VISIBLE);
            else if(snapshot.child("Driver Info").child("Status").exists()) {
                String status = snapshot.child("Driver Info").child("Status").getValue().toString();
                ((TextView) findViewById(R.id.dashboard_status)).setText(status);
                lnrProcess.setVisibility(View.VISIBLE);
                if(status.equals("Approved"))
                    ((Button) findViewById(R.id.dashboard_qr)).setVisibility(View.VISIBLE);
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
        dbRef.addListenerForSingleValueEvent(vel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbRef.removeEventListener(vel);
    }
}