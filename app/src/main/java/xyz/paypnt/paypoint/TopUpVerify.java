package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class TopUpVerify extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Email Verification");
        setContentView(R.layout.topupverify);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        double amount = getIntent().getDoubleExtra("Amount",0);
        if(amount == 0) {
            Toast.makeText(this, "An error has occurred!", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView txtAmount = (TextView) findViewById(R.id.topupver_amount);
        txtAmount.setText(String.valueOf("Php "+String.format("%.2f",amount)));

        EditText email = (EditText) findViewById(R.id.topupver_email);
        email.setText(mAuth.getCurrentUser().getEmail());
    }
}