package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Applicants extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Applicants");
        setContentView(R.layout.applicants);
        layoutManager = new LinearLayoutManager(this);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Applicants");

        RecyclerView trans = (RecyclerView) findViewById(R.id.applicants_recyclerView);
        trans.setLayoutManager(layoutManager);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
                if(snapshot.exists()) {
                    int position = 0;
                    for (DataSnapshot a : snapshot.getChildren()) {
                        list.put(String.valueOf(position), new ArrayList<String>());
                        list.get(String.valueOf(position)).add(a.getKey());
                        list.get(String.valueOf(position)).add(a.getValue().toString());
                    }
                }
                Applicants_Adapter adapter =new Applicants_Adapter(list,getApplicationContext());
                trans.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        * This will use RecyclerView
        * When an admin taps on an item
        * it will transfer to ApplicantInfo.java
        * Use the following code:
        * startActivity(new Intent(Applicants.this, ApplicantInfo.class).putExtra("Applicant UID", UID);
        * UID is the User Unique ID of the Applicant
        * */


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}