package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import xyz.paypnt.paypoint.R;

public class PriceBreakDown extends AppCompatActivity {

    private Button decline, confirm;
    private float price;
    private String type, origin,  destination;
    private TextView txtOrigin, txtDestination, txtType, txtDistance, txtDiscount, txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Price Breakdown");
        setContentView(R.layout.pricebreakdown);

        decline = (Button) findViewById(R.id.pd_decline);
        confirm = (Button) findViewById(R.id.pd_confirm);

        txtOrigin = (TextView) findViewById(R.id.pb_origin);
        txtDestination = (TextView) findViewById(R.id.pb_destination);
        txtType = (TextView) findViewById(R.id.pb_type);
        txtDiscount = (TextView) findViewById(R.id.pb_discount);
        txtDistance = (TextView) findViewById(R.id.pb_distance);
        txtTotal = (TextView) findViewById(R.id.pb_total);

        Toast.makeText(this, String.valueOf(getIntent().getFloatExtra("distance",0)) + "km - pdb", Toast.LENGTH_LONG).show();
        txtOrigin.setText(getIntent().getStringExtra("origin"));
        txtDestination.setText(getIntent().getStringExtra("destination"));
        txtType.setText(getIntent().getStringExtra("type"));
        txtTotal.setText(String.valueOf(getIntent().getFloatExtra("price", 0)));
        txtDistance.setText(String.valueOf(getIntent().getFloatExtra("distance",0))+" km");
//        txtDistance.setText("Null");
//        txtDiscount.setText("Null");

        decline.setOnClickListener(v -> {
            finish();
        });

        confirm.setOnClickListener(v -> {
            startActivity(new Intent(PriceBreakDown.this, QRScan.class));
        });
    }
}