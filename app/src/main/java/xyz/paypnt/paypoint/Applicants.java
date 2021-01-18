package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Applicants extends AppCompatActivity {

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
    }
}