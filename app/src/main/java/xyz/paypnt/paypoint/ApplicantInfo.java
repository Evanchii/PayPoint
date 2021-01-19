package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ApplicantInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Applicant Info");
        setContentView(R.layout.applicantinfo);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        String AppUID = getIntent().getStringExtra("Applicant UID");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(AppUID).child("Driver Info"),
                dbRefAdmin = FirebaseDatabase.getInstance().getReference().child("Admin").child("Applicants");

        TextView appinfo_Fname = (TextView)findViewById(R.id.appinfo_txtFName);
        TextView appinfo_Lname = (TextView)findViewById(R.id.appinfo_txtLName);
        TextView appinfo_PNumber = (TextView)findViewById(R.id.appinfo_txtPNumber);
        TextView appinfo_Bday = (TextView)findViewById(R.id.appinfo_txtBday);
        TextView appinfo_spnType = (TextView)findViewById(R.id.appinfo_spnType);
        TextView appinfo_spnRoute = (TextView)findViewById(R.id.appinfo_spnRoute);

        ImageView appinfo_dLicenseView=(ImageView)findViewById(R.id.appinfo_dLicenseView);
        ImageView appinfo_dIDView=(ImageView)findViewById(R.id.appinfo_dIDView);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appinfo_Fname.setText(snapshot.child("FirstName").getValue().toString());
                appinfo_Lname.setText(snapshot.child("LastName").getValue().toString());
                appinfo_PNumber.setText(snapshot.child("PlateNumber").getValue().toString());
                appinfo_Bday.setText(snapshot.child("Birthday").getValue().toString());
                appinfo_spnType.setText(snapshot.child("Type").getValue().toString());
                appinfo_spnRoute.setText(snapshot.child("Route").getValue().toString());

                String license = snapshot.child("UrlLicense").getValue().toString();
                String ID = snapshot.child("UrlID").getValue().toString();

                Log.d("uri:",license);
                StorageReference photoRef = mStorageRef.child(license);
                final long ONE_MB = 1024*1024;
                photoRef.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                    Bitmap bmpLicense = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    appinfo_dLicenseView.setImageBitmap(bmpLicense);
                    appinfo_dLicenseView.getLayoutParams().height = appinfo_dLicenseView.getLayoutParams().width;
                }).addOnFailureListener(e -> {
                    Toast.makeText(ApplicantInfo.this, "Failed to fetch License photo. See longs", Toast.LENGTH_SHORT).show();
                    Log.e("photoRef Error", e.toString());
                });

                Log.d("uri:", ID);
                photoRef = mStorageRef.child(ID);
                photoRef.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                    Bitmap bmpID = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    appinfo_dIDView.setImageBitmap(bmpID);
                    appinfo_dIDView.getLayoutParams().height = appinfo_dIDView.getLayoutParams().width;
                }).addOnFailureListener(e -> {
                    Toast.makeText(ApplicantInfo.this, "Failed to fetch License photo. See longs", Toast.LENGTH_SHORT).show();
                    Log.e("photoRef Error", e.toString());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dbRefUID = FirebaseDatabase.getInstance().getReference().child("Users").child(AppUID);
        DatabaseReference dbRefDriverList = FirebaseDatabase.getInstance().getReference().child("Admin").child("Drivers");

        Button appinfo_accept =(Button)findViewById(R.id.appinfo_accept);
        appinfo_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRefUID.child("Driver Info").child("Status").setValue("Approved");
                dbRefUID.child("Type").setValue("Driver");
                dbRefAdmin.child(AppUID).removeValue();
                dbRefDriverList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int position = 0;
                        for (DataSnapshot a : snapshot.getChildren())
                            position = Integer.parseInt(a.getKey())+1;
                        dbRefDriverList.child(String.format("%03d", position)).setValue(AppUID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                startActivity(new Intent(ApplicantInfo.this, Applicants.class));
                finish();
            }
        });
        Button appinfo_reject =(Button)findViewById(R.id.appinfo_reject);
        appinfo_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRefUID.child("Status").setValue("Rejected");
                dbRefAdmin.child(AppUID).removeValue();
                startActivity(new Intent(ApplicantInfo.this, Applicants.class));
                finish();
            }
        });



    }
}