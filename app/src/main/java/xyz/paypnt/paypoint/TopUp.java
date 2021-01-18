package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TopUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Top Up");
        setContentView(R.layout.topup);
        layoutManager = new LinearLayoutManager(this);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("TopUp History");

        RecyclerView trans = (RecyclerView) findViewById(R.id.topup_trans);
        trans.setLayoutManager(layoutManager);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
                System.out.print(snapshot.exists());
                if(snapshot.exists()) {
                    System.out.print("Exists!");
                    Toast.makeText(TopUp.this, "Run?", Toast.LENGTH_SHORT).show();
                    int position = 0;
                    for(DataSnapshot child : snapshot.getChildren()) {
                        Log.d("ForEachTopUp","RUNNING");
                        list.put(String.valueOf(position), new ArrayList<String>());
                        list.get(String.valueOf(position)).add(child.child("Type").getValue().toString());
                        list.get(String.valueOf(position)).add(child.child("Amount").getValue().toString()+" ("+child.child("Status").getValue()+")");
                        list.get(String.valueOf(position++)).add(child.child("Date").getValue().toString());
                        TopUpAdapter adapter = new TopUpAdapter(list,getApplicationContext());
                        trans.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.print(error.toString());
            }
        });


        TextView error = (TextView) findViewById(R.id.topup_error);
        Button pay = (Button) findViewById(R.id.topup_pay);

        EditText amount = (EditText) findViewById(R.id.topup_eTxtAmount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                error.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.VISIBLE);
                if(amount.getText().toString().trim().equals(""))
                    pay.setVisibility(View.INVISIBLE);
                else if(Integer.parseInt(amount.getText().toString().trim()) == 0)
                    pay.setVisibility(View.INVISIBLE);
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

        pay.setOnClickListener(v -> {
            startActivity(new Intent(TopUp.this, TopUpVerify.class)
                .putExtra("Amount", Double.parseDouble(amount.getText().toString()))
            );
            finish();
        });

    }
}