package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Drivers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Drivers");
        setContentView(R.layout.drivers);

        DatabaseReference dbRefAdmin = FirebaseDatabase.getInstance().getReference().child("Admin").child("Drivers"),
                dbRefUser = FirebaseDatabase.getInstance().getReference().child("Users");

        //Uses table?
        /*
        * In this activity, we will use table
        * The table will have 4 columns: Active, Name, Route/Area, Type.
        *
        * Active will contain a CheckBox that indicates if driver privileges/permissions will be activated or deactivated.
        * Active status will be kept track on the database; if(Type == "Driver") checkbox.setChecked(true); else checkbox.setChecked(false);
        *
        * Name will be "Last Name, First Name" in the same order. This will be the LN and FN in driver info
        *
        * Route will be the route in driver info
        *
        * Type will be the type in driver info
        */
    }
}