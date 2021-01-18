package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(AppUID).child("Driver Info");

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



//                  Error
                Uri license = Uri.parse(snapshot.child("UrlLicense").getValue().toString());
//                Uri ID = Uri.parse(snapshot.child("UrlID").getValue().toString());

                mStorageRef.child(String.valueOf(license)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        appinfo_dLicenseView.setImageURI(uri);
                    }
                });

//                appinfo_dIDView.setImageURI(ID);


                System.out.println(snapshot.child("UrlLicense").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        Button appinfo_accept =(Button)findViewById(R.id.appinfo_accept);
        appinfo_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.child("Status").setValue("Approve");
            }
        });
        Button appinfo_reject =(Button)findViewById(R.id.appinfo_reject);
        appinfo_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.child("Status").setValue("Reject");
            }
        });



    }
}