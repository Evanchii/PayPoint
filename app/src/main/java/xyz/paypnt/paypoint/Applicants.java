package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Applicants extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    HashMap<String, ArrayList<String>> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Applicants");
        setContentView(R.layout.applicants);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Applicants");

        /*
        * This will use RecyclerView
        * When an admin taps on an item
        * it will transfer to ApplicantInfo.java
        * Use the following code:
        * startActivity(new Intent(Applicants.this, ApplicantInfo.class).putExtra("Applicant UID", UID);
        * UID is the User Unique ID of the Applicant
        * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.applicants_recyclerView);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        Applicants_Adapter adapter =new Applicants_Adapter(hashMap,getApplicationContext());
        recyclerView.setAdapter(adapter);


    }
}