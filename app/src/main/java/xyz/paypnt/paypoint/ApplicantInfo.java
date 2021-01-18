package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicantInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Applicant Info");
        setContentView(R.layout.applicantinfo);

        String AppUID = getIntent().getStringExtra("Applicant UID");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Applicants").child("");

        /*
        * Panay fetching of data lang naman to so yeah uwu
        * */
    }
}