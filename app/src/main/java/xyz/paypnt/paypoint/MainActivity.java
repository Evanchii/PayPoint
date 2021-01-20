package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.main);


        FirebaseApp.initializeApp(this);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }


        Button Login, Signup;
        Login = (Button) findViewById(R.id.main_login);
        Signup = (Button) findViewById(R.id.main_signup);

        Login.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,Login.class));
            finish();
        });
        Signup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,Signup.class));
            finish();
        });
    }
}