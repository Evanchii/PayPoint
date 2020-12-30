package xyz.paypnt.paypoint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class CommonFunctions extends AppCompatActivity{

    private static FirebaseAuth mAuth;
    private static DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.header);
    }

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
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails(NavigationView nv) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

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
}
