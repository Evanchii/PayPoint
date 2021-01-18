package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Applicants extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Applicants");
        setContentView(R.layout.applicants);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Applicants");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
                int position = 0;
                for(DataSnapshot a : snapshot.getChildren()) {
                    hashMap.put(String.valueOf(position), new ArrayList<String>());
                    hashMap.get(String.valueOf(position)).add(a.getKey());
                    hashMap.get(String.valueOf(position)).add(a.getValue().toString());
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.applicants_recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Applicants.this);

                recyclerView.setLayoutManager(layoutManager);
                Applicants_Adapter adapter =new Applicants_Adapter(hashMap,getApplicationContext());
                recyclerView.setAdapter(adapter);
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
}