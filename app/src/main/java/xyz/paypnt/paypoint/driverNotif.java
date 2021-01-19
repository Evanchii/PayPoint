package xyz.paypnt.paypoint;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class driverNotif extends IntentService {
    //https://developer.android.com/training/run-background-service/create-service

    private static final String channelPayment = "Payment Channel";
    private NotificationManagerCompat mNotificationManager;

    public driverNotif() {
        super("driverNotif");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("History");

        createNotificationChannel();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String passenger = snapshot.child("Passenger").getValue().toString();
                String passenger_UID = snapshot.child("PassengerUID").getValue().toString();
                String price = snapshot.child("Price").getValue().toString();
                String source = snapshot.child("Source").getValue().toString();
                String distination = snapshot.child("Destination").getValue().toString();



                /*
                * check for the most recent payment and pass the details to the Notification Generator. If help/code needed, call evan.
                * 5 details needed. Username of the payer, UID of the payer, amount paid, payer's source, and payer's destination
                *
                * Notif Short Desc:
                * Username has paid amount.
                *
                * Notif Long Desc:
                * Username(UID) has paid amount.
                * Source:
                * Destination: dest
                *
                * Notification Logo: Please use @drawable/logo_black_inside
                * Notification Title: Payment Received!
                */

                Notification builder = new NotificationCompat.Builder(driverNotif.this, channelPayment)
                        .setSmallIcon(R.drawable.logo_black_inside)
                        .setContentTitle("Received Payment")
                        .setContentText(passenger+" has paid Php "+price+"php")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(passenger_UID+" has paid Php100.00\nSource: "+ source+ "\nDestination:"+ distination))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build();

                mNotificationManager = NotificationManagerCompat.from(driverNotif.this);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, builder);

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

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel paymentchannel = new NotificationChannel(
                    channelPayment,
                    "Payment Received",
                    NotificationManager.IMPORTANCE_HIGH
            );
            paymentchannel.setDescription("Channel for Notifying driver if payment has been received");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(paymentchannel);
        }
    }
}
