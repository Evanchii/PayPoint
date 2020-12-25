package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.example.paypoint.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

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