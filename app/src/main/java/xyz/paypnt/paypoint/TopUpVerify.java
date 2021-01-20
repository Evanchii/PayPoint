package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class  TopUpVerify extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Email Verification");
        setContentView(R.layout.topupverify);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("TopUp History");
        DatabaseReference dbRef_balance = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Balance");

        double amount = getIntent().getDoubleExtra("Amount",0);
        if(amount == 0) {
            Toast.makeText(this, "An error has occurred!", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView txtAmount = (TextView) findViewById(R.id.topupver_amount);
        txtAmount.setText(String.valueOf("Php "+String.format("%.2f",amount)));

        EditText email = (EditText) findViewById(R.id.topupver_email);
        email.setText(mAuth.getCurrentUser().getEmail());

        Button confirm = (Button) findViewById(R.id.topupver_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ID = "000";
                        if(snapshot.exists()) {
                            for(DataSnapshot child : snapshot.getChildren()) {
                                ID = String.format(Locale.ENGLISH, "%03d", Integer.parseInt(child.getKey())+1);
                            }
                        } else {
                            ID = "000";
                        }

                        dbRef.child(ID).child("Type").setValue("TopUp");
                        dbRef.child(ID).child("Amount").setValue(amount);
                        dbRef.child(ID).child("Status").setValue("Successful");
                        dbRef.child(ID).child("Date").setValue((new SimpleDateFormat("MMM dd, yyyy")).format(Calendar.getInstance().getTime()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                //Add balance
                dbRef_balance.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dbRef_balance.setValue(Double.parseDouble(snapshot.getValue().toString()) + amount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                finish();
            }
        });
    }
}