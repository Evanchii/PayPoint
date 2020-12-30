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

public class QRScan extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    private Button cancel, pay;
    private TextView dName, pNumber, initStatus;
    private ScrollView details;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

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

                        pay = (Button) findViewById(R.id.scan_confirm);
                        pay.setOnClickListener(v -> {
                            /**Insert Processing
                              *Check if balance is adequate
                              *Check if type matches

                              *Finalize Balance Deduction
                              *Add to history User & Driver*/

                            //If succeeded
                            if(true /*This is temporary*/) {
                                Toast.makeText(QRScan.this, "Paid " + dName.getText(), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(QRScan.this, Dashboard.class));
                                finish();
                            } else {
                                Toast.makeText(QRScan.this, "An Error Occurred! "/*Insert Reason*/, Toast.LENGTH_LONG).show();
                            }

                        });
                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(result.getText()).exists()) {
                                    initStatus.setVisibility(View.GONE);
                                    details.setVisibility(View.VISIBLE);
                                    pay.setVisibility(View.VISIBLE);
                                    String temp = String.valueOf(snapshot.child(result.getText()).child("Username").getValue());
                                    dName.setText(temp);
                                    pNumber.setText("To be implemented!");
                                } else {
                                    initStatus.setText("Invalid QR code\n"+result.getText());
                                    initStatus.setVisibility(View.VISIBLE);
                                    details.setVisibility(View.GONE);
                                    pay.setVisibility(View.GONE);
                                }
//                                String uName = String.valueOf(snapshot.child("Username").getValue());
//                                TextView bal = (TextView) findViewById(R.id.dashboard_bal), user = (TextView) findViewById(R.id.dashboard_user);
//                                bal.setText("0.00 - Str");
//                                user.setText("Welcome, "+ uName);
//                                System.out.println(uName);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //For Debugging Purposes
//                        dName.setText(result.getText());
//                        pNumber.setText(result.getText());
                    }
                });
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
//                Toast.makeText(QRScan.this, "An Error Occured! See logs", Toast.LENGTH_LONG).show();
                Log.d("QR", "An Error Occurred\n"+error);
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });

        cancel = (Button) findViewById(R.id.scan_cancel);
        cancel.setOnClickListener(v -> {
            finish();
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