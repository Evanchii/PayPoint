package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GetStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getstarted);

        Button download = (Button) findViewById(R.id.getstarted_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions cf = new CommonFunctions();
                String path = cf.generateQR();
                if(!path.equals(null))
                    Toast.makeText(GetStarted.this, "Saved! "+path, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(GetStarted.this, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}