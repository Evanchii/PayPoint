package xyz.paypnt.paypoint;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommonFunctions extends AppCompatActivity {


    public static boolean menu(Context a, MenuItem item, String src) {
        FirebaseAuth mAuth;
        DatabaseReference dbRef;
        Intent i = null;
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(a, String.valueOf(src.equals(item.getTitle())),Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if(!src.equals(item.getTitle()))
                    i = new Intent(a, Dashboard.class);
                break;
            case R.id.action_history:
                if(!src.equals(item.getTitle()))
                    i = new Intent(a, History.class);
                break;
            case R.id.action_password:
                if(!src.equals(item.getTitle()))
                    i = new Intent(a, Password.class);
                break;
            case R.id.action_use:
                if(!src.equals(item.getTitle()))
                    i = new Intent(a, HowToUse.class);
                break;
            case R.id.action_aboutUs:
                if(!src.equals(item.getTitle()))
                    i = new Intent(a, AboutUs.class);
                break;
            case R.id.action_logOut:
                mAuth.signOut();
                i = new Intent(a, MainActivity.class);
                break;
        }
        if(i!=null) {
            a.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String uName = String.valueOf(snapshot.child("Username").getValue());

                //Gave up

//                TextView a = (TextView) findViewById(R.id.header_username),
//                        b = (TextView) findViewById(R.id.header_email);
//                a.setText(uName);
//                b.setText(mAuth.getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
