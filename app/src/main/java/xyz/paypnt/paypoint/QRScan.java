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
import com.google.zxing.Result;

import org.w3c.dom.Text;

public class QRScan extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    private Button cancel, pay;
    private TextView dName, pNumber, initStatus;
    private ScrollView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Scan");
        setContentView(R.layout.qrscan);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScan.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

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
                        initStatus.setVisibility(View.GONE);
                        dName = (TextView) findViewById(R.id.scan_drivername);
                        pNumber = (TextView) findViewById(R.id.scan_platenumber);

                        details = (ScrollView) findViewById(R.id.scr_scan);
                        details.setVisibility(View.VISIBLE);

                        pay = (Button) findViewById(R.id.scan_confirm);
                        pay.setVisibility(View.VISIBLE);
                        pay.setOnClickListener(v -> {
                            //Insert Processing
                            Toast.makeText(QRScan.this, "Paid %driver name%", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(QRScan.this, Dashboard.class));
                            finish();
                        });

                        //For Debugging Purposes
                        dName.setText(result.getText());
                        pNumber.setText(result.getText());
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