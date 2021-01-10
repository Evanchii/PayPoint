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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("Dashboard");
        setContentView(R.layout.dashboard);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String uName = String.valueOf(snapshot.child("Username").getValue());
                String balance = String.format("%.2f",snapshot.child("Balance").getValue());
                TextView bal = (TextView) findViewById(R.id.dashboard_bal), user = (TextView) findViewById(R.id.dashboard_user);
                bal.setText(balance);
                user.setText("Welcome, "+ uName);
                System.out.println(uName);
                if(snapshot.child("Driver Info").child("Status").exists()) {
                    String status = snapshot.child("Driver Info").child("Status").getValue().toString();
                    ((TextView) findViewById(R.id.dashboard_status)).setText(status);
                    ((LinearLayout) findViewById(R.id.dashboard_process)).setVisibility(View.VISIBLE);
                    if(status.equals("Approved"))
                        ((Button) findViewById(R.id.dashboard_qr)).setVisibility(View.VISIBLE);
                    else if(status.equals("Denied"))
                        ((Button) findViewById(R.id.dashboard_confirmStatus)).setVisibility(View.VISIBLE);
                }
                else {
                    ((LinearLayout) findViewById(R.id.dashboard_apply)).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}