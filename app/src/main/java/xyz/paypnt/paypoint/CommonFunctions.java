package xyz.paypnt.paypoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommonFunctions extends AppCompatActivity{

    private static FirebaseAuth mAuth;
    private static DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.header);
    }

    @SuppressLint("NonConstantResourceId")
    public static boolean menu(Context con, MenuItem item, String src) {
        Intent i = null;
        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Dashboard.class);
                break;
            case R.id.action_history:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, History.class);
                break;
            case R.id.action_password:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Password.class);
                break;
            case R.id.action_use:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, HowToUse.class);
                break;
            case R.id.action_aboutUs:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, AboutUs.class);
                break;
            case R.id.action_logOut:
                mAuth.signOut();
                i = new Intent(con, MainActivity.class);
                con.stopService(new Intent(con, driverNotif.class));
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails(NavigationView nv) {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        View headerView = nv.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.header_username);
        TextView email = (TextView) headerView.findViewById(R.id.header_email);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = String.valueOf(snapshot.child("Username").getValue());
                name.setText(String.valueOf(uName));
                email.setText(mAuth.getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public String generateQR() {
        mAuth = FirebaseAuth.getInstance();
        String qr_Address = mAuth.getCurrentUser().getUid();

        File storageDir = new File( Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "PayPoint");

        MultiFormatWriter writer = new MultiFormatWriter();

        if(!storageDir.exists()){

            boolean s = new File(storageDir.getPath()).mkdirs();

            if(!s){
                Log.v("not", "not created");
            }
            else{
                Log.v("cr","directory created");
            }
        }
        else{
            Log.v("directory", "directory exists");
        }

        try {
            Log.v("SaveQR", "Running#1");
            BitMatrix matrix = writer.encode(qr_Address, BarcodeFormat.QR_CODE,350,350);

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);
            Log.v("SaveQR", "Running#2");
            try (FileOutputStream out = new FileOutputStream(storageDir+"/QRCode.jpg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                Log.v("SaveQR", "Running#3");
                return storageDir+"/PayPointQRCode.jpg";
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("SaveQR", "ERROR");
                return null;
            }
        } catch (WriterException e) {
            e.printStackTrace();
            Log.v("SaveQR", "ERROR#2");
            return null;
        }
    }
}
