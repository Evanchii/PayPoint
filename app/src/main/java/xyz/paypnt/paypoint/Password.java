package xyz.paypnt.paypoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import xyz.paypnt.paypoint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Password extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference loginDbRef;

    private EditText oldPass, newPass, confPass;
    private Button change;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("Change Password");
        setContentView(R.layout.password);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth=FirebaseAuth.getInstance();
        loginDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        change = (Button) findViewById(R.id.pass_change);
        change.setOnClickListener(v -> {
            changePass();
        });
    }

    private void changePass() {
        error.setVisibility(View.GONE);
        oldPass = (EditText) findViewById(R.id.pass_old);
        newPass = (EditText) findViewById(R.id.pass_new);
        confPass = (EditText) findViewById(R.id.pass_confirm);
        error = (TextView) findViewById(R.id.pass_error);

        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), oldPass.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(!oldPass.getText().toString().trim().equals(newPass.getText().toString().trim())) {
                    if(newPass.getText().toString().trim().equals(confPass.getText().toString().trim())) {
                        mAuth.getCurrentUser().updatePassword(newPass.getText().toString().trim());
                        Toast.makeText(Password.this, "Changed Password!",Toast.LENGTH_LONG).show();

                        startActivity(new Intent(Password.this, Dashboard.class));
                        finish();
                    } else {
                        error.setText("Passwords do not match");
                        error.setVisibility(View.VISIBLE);
                    }
                } else {
                    error.setText("Password must differ from old password");
                    error.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error.setText("Incorrect Old Password");
                error.setVisibility(View.VISIBLE);
            }
        });
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
        if(CommonFunctions.menu(this, item, "Password"))
            finish();
        return true;
    }
}