package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xyz.paypnt.paypoint.R;

public class PriceBreakDown extends AppCompatActivity {

    private Button decline, confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Price Breakdown");
        setContentView(R.layout.pricebreakdown);

        decline = (Button) findViewById(R.id.pd_decline);
        confirm = (Button) findViewById(R.id.pd_confirm);

        decline.setOnClickListener(v -> {
            finish();
        });

        confirm.setOnClickListener(v -> {
            startActivity(new Intent(PriceBreakDown.this, QRScan.class));
        });
    }
}