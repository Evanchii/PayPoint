package xyz.paypnt.paypoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.paypoint.R;
import com.google.android.material.navigation.NavigationView;

public class Password extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.password);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        startActivity(new Intent(Password.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                startActivity(new Intent(Password.this, Dashboard.class));
                finish();
                break;
            case R.id.action_history:
                Toast.makeText(Password.this, "History is under construction",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_password:
                break;
            case R.id.action_use:
                Toast.makeText(Password.this, "How to Use is under construction",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_aboutUs:
                Toast.makeText(Password.this, "About us is under construction",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_logOut:
                startActivity(new Intent(Password.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}