package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import xyz.paypnt.paypoint.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

//        new AlertDialog.Builder(this).setCancelable(false).setMessage(String.valueOf(Calendar.getInstance().getTime())).setTitle("Date and Time").show();

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
        Signup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, QRScan.class));
                finish();
                return false;
            }
        });
    }
}