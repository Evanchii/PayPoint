package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TogglePrice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Toggle Price");
        setContentView(R.layout.toggleprice);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Driver");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> spinnerArray = new ArrayList<String>();
                for (DataSnapshot child : snapshot.child("Areas").getChildren())
                    spinnerArray.add(child.getKey());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        TogglePrice.this, android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner sItems = (Spinner) findViewById(R.id.toggleprice_spinner);
                sItems.setAdapter(adapter);

                sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        EditText jeep = (EditText) findViewById(R.id.toggle_jeep),
                                bus = (EditText) findViewById(R.id.toggle_bus),
                                taxi = (EditText) findViewById(R.id.toggle_taxi);

                        jeep.setText(snapshot.child("Areas").child(sItems.getSelectedItem().toString()).child("Jeep").getValue().toString());
                        bus.setText(snapshot.child("Areas").child(sItems.getSelectedItem().toString()).child("Bus").getValue().toString());
                        taxi.setText(snapshot.child("Areas").child(sItems.getSelectedItem().toString()).child("Taxi").getValue().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        sItems.setSelection(0);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Spinner sItems = (Spinner) findViewById(R.id.toggleprice_spinner);

        EditText jeep = (EditText) findViewById(R.id.toggle_jeep),
                bus = (EditText) findViewById(R.id.toggle_bus),
                taxi = (EditText) findViewById(R.id.toggle_taxi),
                password = (EditText) findViewById(R.id.toggle_password);

        TextView error = (TextView) findViewById(R.id.toggle_error);

        CheckBox confirm = (CheckBox) findViewById(R.id.toggle_confirm);

        Button setValue = (Button) findViewById(R.id.toggle_submit);
        setValue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                error.setText("");
                if(!jeep.getText().toString().trim().equals("") && !bus.getText().toString().trim().equals("") && !taxi.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")) {
                    if(confirm.isChecked()) {
                        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), password.getText().toString().trim()).addOnSuccessListener(authResult -> {
                            dbRef.child("Areas").child(sItems.getSelectedItem().toString()).child("Jeep").setValue(Double.parseDouble(jeep.getText().toString().trim()));
                            dbRef.child("Areas").child(sItems.getSelectedItem().toString()).child("Taxi").setValue(Double.parseDouble(taxi.getText().toString().trim()));
                            dbRef.child("Areas").child(sItems.getSelectedItem().toString()).child("Bus").setValue(Double.parseDouble(bus.getText().toString().trim()));
                            finish();
                        }).addOnFailureListener(e -> error.setText("Wrong Password!"));
                    }
                    else {
                        error.setText("Please Confirm Changes!");
                    }
                }
                else {
                    error.setText("Please Fill in all required data!");
                }
            }
        });
    }
}