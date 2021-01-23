package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import xyz.paypnt.paypoint.R;

public class PriceBreakDown extends AppCompatActivity {

    private Button decline, confirm;
    private TextView txtOrigin, txtDestination, txtType, txtDistance, txtDiscount, txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Price Breakdown");
        setContentView(R.layout.pricebreakdown);

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        float total = getIntent().getFloatExtra("price", 0);
        String type = getIntent().getStringExtra("type");

        decline = (Button) findViewById(R.id.pd_decline);
        confirm = (Button) findViewById(R.id.pd_confirm);

        txtOrigin = (TextView) findViewById(R.id.pb_origin);
        txtDestination = (TextView) findViewById(R.id.pb_destination);
        txtType = (TextView) findViewById(R.id.pb_type);
        txtDiscount = (TextView) findViewById(R.id.pb_discount);
        txtDistance = (TextView) findViewById(R.id.pb_distance);
        txtTotal = (TextView) findViewById(R.id.pb_total);

//        Toast.makeText(this, String.valueOf(getIntent().getFloatExtra("distance", 0)) + "km - pdb", Toast.LENGTH_LONG).show();
        txtOrigin.setText(getIntent().getStringExtra("origin"));
        txtDestination.setText(getIntent().getStringExtra("destination"));
        txtType.setText(type);
        txtTotal.setText(String.valueOf(total));
        txtDistance.setText(String.valueOf(getIntent().getFloatExtra("distance", 0)) + " km");

        decline.setOnClickListener(v -> {
            finish();
        });

        confirm.setOnClickListener(v -> {
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double bal = (double) snapshot.child(mAuth.getCurrentUser().getUid()).child("Balance").getValue();
                    if(bal >= (double)total)
                        startActivity(new Intent(PriceBreakDown.this, QRScan.class)
                                .putExtra("type", type)
                                .putExtra("finalTotal", (float) total)
                                .putExtra("src", getIntent().getStringExtra("origin"))
                                .putExtra("dest", getIntent().getStringExtra("destination")));
                    else
                        new AlertDialog.Builder(PriceBreakDown.this).setTitle("Insufficient Balance!").setMessage("You only have Php "+String.format("%.2f", bal)+"! You need Php "+String.format("%.2f", total)+" to book a ride!").show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }
}