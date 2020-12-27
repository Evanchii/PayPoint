package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.paypoint.R;
import com.google.zxing.Result;

import org.w3c.dom.Text;

public class QRScan extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Scan");
        setContentView(R.layout.qrscan);

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

                        TextView res = findViewById(R.id.result);
                        res.setText(result.getText());

                    }
                });
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Toast.makeText(QRScan.this, "An Error Occured! See logs", Toast.LENGTH_LONG).show();
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