package xyz.paypnt.paypoint;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CommonFunctions extends AppCompatActivity {

    private static FirebaseAuth mAuth;

    public static boolean menu(Context a, MenuItem item, String src) {
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
}
