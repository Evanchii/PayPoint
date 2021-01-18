package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DriverRegister extends AppCompatActivity {

    private Calendar calendar;
    private EditText dateView;
    private int year, month, day;

    private FirebaseAuth mAuth;
    private DatabaseReference fdb;
    private StorageReference mStorage;

    private EditText reg_txtFName,reg_txtLName,reg_txtPNumber;
    private Spinner reg_route, reg_type;

    private static final int GALLERY_INTENTlicence=1;
    private static final int GALLERY_INTENTid=2;

    private Uri UriLicense, UriID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Register Vehicle");
        setContentView(R.layout.driverregister);


        mAuth=FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        fdb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Driver");
        mStorage= FirebaseStorage.getInstance().getReference();



        reg_txtFName=(EditText)findViewById(R.id.reg_txtFName);
        reg_txtLName=(EditText)findViewById(R.id.reg_txtLName);
        reg_txtPNumber=(EditText)findViewById(R.id.reg_txtPNumber);
        reg_route = (Spinner) findViewById(R.id.reg_spnRoute);
        reg_type = (Spinner) findViewById(R.id.reg_spnType);

        dateView = (EditText) findViewById(R.id.reg_txtBday);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Button upLicenses =(Button)findViewById(R.id.reg_uploadLicense);
        upLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent,GALLERY_INTENTlicence);

            }
        });
        Button upID = (Button)findViewById(R.id.reg_uploadID);
        upID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent,GALLERY_INTENTid);
            }
        });

        Button reg_Submit =(Button)findViewById(R.id.reg_accept);
        reg_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverRegisterSignUp();
            }
        });

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> spinnerArray = new ArrayList<String>();
                for (DataSnapshot child : snapshot.child("Routes").getChildren())
                    spinnerArray.add(child.getValue().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        DriverRegister.this, android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner sItems = (Spinner) findViewById(R.id.reg_spnRoute);
                sItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENTlicence && resultCode==RESULT_OK){
            UriLicense = data.getData();

            ((ImageView) findViewById(R.id.reg_dLicenseView)).getLayoutParams().height = ((ImageView) findViewById(R.id.reg_dLicenseView)).getLayoutParams().width;
            ((ImageView) findViewById(R.id.reg_dLicenseView)).setImageURI(UriLicense);
        }
        if(requestCode==GALLERY_INTENTid && resultCode==RESULT_OK){
            UriID= data.getData();
            ((ImageView) findViewById(R.id.reg_dIDView)).getLayoutParams().height = ((ImageView) findViewById(R.id.reg_dIDView)).getLayoutParams().width;
            ((ImageView) findViewById(R.id.reg_dIDView)).setImageURI(UriID);
        }
    }


    public void driverRegisterSignUp(){

//        Evan Edit this...
//        -Ryan

//        Sure daddy :*
//        -Evan
        ((TextView) findViewById(R.id.reg_error)).setText("");
        if(!reg_txtFName.getText().toString().trim().equals("") && !reg_txtLName.getText().toString().trim().equals("") && !reg_txtPNumber.getText().toString().trim().equals("") && !dateView.getText().toString().trim().equals("") && reg_route.getSelectedItemPosition() != 0 && reg_type.getSelectedItemPosition() != 0) {

            DriverRegisterSetterAndGetter driverRegister = new DriverRegisterSetterAndGetter();
            HashMap<String, Object> dRegister = new HashMap<>();
            driverRegister.setDriverRegister(dRegister);
            dRegister.put("FirstName", reg_txtFName.getText());
            dRegister.put("LastName", reg_txtLName.getText());
            dRegister.put("PlateNumber", reg_txtPNumber.getText());
            dRegister.put("Birthday", dateView.getText());
            dRegister.put("Route", reg_route.getSelectedItem().toString());
            dRegister.put("Type", reg_type.getSelectedItem().toString());
            dRegister.put("Status", "Pending");
            dRegister.put("UrlLicense",UriLicense);
            dRegister.put("UrlID",UriID);



            ProgressDialog progUp = ProgressDialog.show(this, "Uploading","Please wait as we upload your data to the database.", true);
            progUp.setCancelable(false);

            System.out.println("Clicked");
            StorageReference filepathLicense = mStorage.child("License").child(UriLicense.getLastPathSegment());

            filepathLicense.putFile(UriLicense).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    dRegister.put("UrlLicense",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    progUp.dismiss();
                    finish();
                }
            });

            StorageReference filepathID=mStorage.child("PUB ID").child(UriID.getLastPathSegment());
            filepathID.putFile(UriID).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    dRegister.put("UrlID",taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    progUp.dismiss();
                    finish();
                }

            });



            DatabaseReference driverDBref= fdb.child("Driver Info");
            driverDBref.child("FirstName").setValue(String.valueOf(driverRegister.getDriverRegister().get("FirstName")));
            driverDBref.child("LastName").setValue(String.valueOf(driverRegister.getDriverRegister().get("LastName")));
            driverDBref.child("PlateNumber").setValue(String.valueOf(driverRegister.getDriverRegister().get("PlateNumber")));
            driverDBref.child("Birthday").setValue(String.valueOf(driverRegister.getDriverRegister().get("Birthday")));
            driverDBref.child("Route").setValue(String.valueOf(driverRegister.getDriverRegister().get("Route")));
            driverDBref.child("Type").setValue(String.valueOf(driverRegister.getDriverRegister().get("Type")));
            driverDBref.child("Status").setValue(String.valueOf(driverRegister.getDriverRegister().get("Status")));
            driverDBref.child("UrlLicense").setValue(String.valueOf(driverRegister.getDriverRegister().get("UrlLicense")));
            driverDBref.child("UrlID").setValue(String.valueOf(driverRegister.getDriverRegister().get("UrlID")));



        }else{
            ((TextView) findViewById(R.id.reg_error)).setText("Please provide all required data!");
            System.out.println("Error");
        }

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int intMonth, int intDate) {
        String day, month;

        if(intDate<10) day = "0" + intDate;
        else day = String.valueOf(intDate);

        if(intMonth<10) month = "0" + intMonth;
        else month = String.valueOf(intMonth);

        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}