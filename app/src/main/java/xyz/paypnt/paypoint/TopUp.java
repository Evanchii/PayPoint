package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Top Up");
        setContentView(R.layout.topup);

        TextView error = (TextView) findViewById(R.id.topup_error);
        Button pay = (Button) findViewById(R.id.topup_pay);

        EditText amount = (EditText) findViewById(R.id.topup_eTxtAmount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                error.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.VISIBLE);
                if(amount.getText().toString().trim().equals("")) {
                    pay.setVisibility(View.INVISIBLE);
                }
                else if(Double.parseDouble(amount.getText().toString()) > 10000) {
                    error.setVisibility(View.VISIBLE);
                    pay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        pay.setOnClickListener(v -> startActivity(new Intent(TopUp.this, TopUpVerify.class)
                .putExtra("Amount", Double.parseDouble(amount.getText().toString()))
        ));

    }
}