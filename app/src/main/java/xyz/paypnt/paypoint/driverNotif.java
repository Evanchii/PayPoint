package xyz.paypnt.paypoint;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class driverNotif extends IntentService {
    //https://developer.android.com/training/run-background-service/create-service

    public driverNotif() {
        super("driverNotif");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("History");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*
                * check for the most recent payment and pass the details to the Notification Generator. If help/code needed, call evan.
                * 5 details needed. Username of the payer, UID of the payer, amount paid, payer's source, and payer's destination
                *
                * Notif Short Desc:
                * Username has paid amount.
                *
                * Notif Long Desc:
                * Username(UID) has paid amount.
                * Source: src
                * Destination: dest
                *
                * Notification Logo: Please use @drawable/logo_black_inside
                * Notification Title: Payment Received!
                */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        * In this class or Background Service, this handles the notification while the application is hidden (e.g The app is running on background)
        * We will utilize this to show the "Driver" incoming payments from their passengers.
        * This way the driver will ensure that all of his passengers are paying their fares
        *
        * This also acts as a confirmation for the payment of the user.
        * */
    }
}
