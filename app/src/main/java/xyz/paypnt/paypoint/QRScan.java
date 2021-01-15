package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import xyz.paypnt.paypoint.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

public class QRScan extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    private Button cancel, pay;
    private TextView dName, pNumber, initStatus;
    private ScrollView details;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String driverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Scan");
        setContentView(R.layout.qrscan);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScan.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        cancel = (Button) findViewById(R.id.scan_cancel);
        cancel.setOnClickListener(v -> finish());

        pay = (Button) findViewById(R.id.scan_confirm);
        pay.setOnClickListener(v -> {
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double userBal = (double) snapshot.child(mAuth.getCurrentUser().getUid()).child("Balance").getValue(),
                        total = (double) getIntent().getFloatExtra("finalTotal", 0),
                        driverBal = (double) snapshot.child(driverUid).child("Balance").getValue();

//                    Toast.makeText(QRScan.this, data[0]+" "+data[1]+" "+data[2], Toast.LENGTH_LONG).show();
                    if(userBal >= total && total != 0) {
                        codeScanner.releaseResources();
                        dbRef.child(mAuth.getCurrentUser().getUid()).child("Balance").setValue((float) userBal - total);
                        dbRef.child(driverUid).child("Balance").setValue((float) userBal + total);

                        //add to history
                        String id = (new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa")).format(Calendar.getInstance().getTime());
                        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("History").child(id);

                        dbRef.child("TimeDate").setValue((new SimpleDateFormat("MMM dd, yyyy\nhh:mm:ss aa")).format(Calendar.getInstance().getTime()));
                        dbRef.child("Driver").setValue(dName.getText().toString().trim() + " ("+pNumber.getText().toString().trim()+")");
                        dbRef.child("Source").setValue(getIntent().getStringExtra("src"));
                        dbRef.child("Destination").setValue(getIntent().getStringExtra("dest"));
                        dbRef.child("Price").setValue((double) total);
                        dbRef.child("Type").setValue(getIntent().getStringExtra("type"));

                        Toast.makeText(QRScan.this, "Paid " + dName.getText() + " Php "+String.format("%.2f", total), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(QRScan.this, Dashboard.class));
//                        finish();
                    } else {
                        Toast.makeText(QRScan.this, "An Error Occurred! "/*Insert Reason*/, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });


        });

        codeScanner();
    }

    private void codeScanner() {
        CodeScannerView scannerView = findViewById(R.id.qrscan_cam);
        codeScanner = new CodeScanner(this, scannerView);
        Toast.makeText(this, "Camera Created", Toast.LENGTH_LONG).show();


        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Insert Detection System
                        initStatus = (TextView) findViewById(R.id.scan_initStatus);
                        dName = (TextView) findViewById(R.id.scan_drivername);
                        pNumber = (TextView) findViewById(R.id.scan_platenumber);

                        details = (ScrollView) findViewById(R.id.scr_scan);

                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Toast.makeText(QRScan.this, String.valueOf(snapshot.child(result.getText()).exists())+"\n"+
                                        snapshot.child(result.getText()).child("Type").getValue().equals("Driver")+"\n"+
                                        getIntent().getStringExtra("type").equals(snapshot.child(result.getText()).child("Driver Info").child("Type").getValue()), Toast.LENGTH_LONG).show();
                                if(snapshot.child(result.getText()).exists()
                                        && snapshot.child(result.getText()).child("Type").getValue().equals("Driver")
                                        && getIntent().getStringExtra("type").equals(snapshot.child(result.getText()).child("Driver Info").child("Type").getValue())) {
                                    initStatus.setVisibility(View.GONE);
                                    details.setVisibility(View.VISIBLE);
                                    pay.setVisibility(View.VISIBLE);
                                    driverUid = result.getText();
                                    String driverName = String.valueOf(snapshot.child(result.getText()).child("Username").getValue());
                                    dName.setText(driverName);
                                    pNumber.setText(snapshot.child(result.getText()).child("Driver Info").child("PlateNumber").getValue().toString());
                                } else {
                                    initStatus.setText(String.format("Invalid QR code\n%s", result.getText()));
                                    initStatus.setVisibility(View.VISIBLE);
                                    details.setVisibility(View.GONE);
                                    pay.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                });
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Log.d("QR", "An Error Occurred\n"+error);
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}