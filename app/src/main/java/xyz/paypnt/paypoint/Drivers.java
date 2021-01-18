package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;

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
        dbRefAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot a:snapshot.getChildren() ){
                    CharSequence driver_status = a.getValue().toString();


                    dbRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            TableLayout tableLayout = (TableLayout)findViewById(R.id.driver_tablelayout);
                            TableRow row = new TableRow(Drivers.this);
                            row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.MATCH_PARENT));

                            CheckBox status = new CheckBox(Drivers.this);
                            TextView tb_name = new TextView(Drivers.this);
                            TextView tb_route = new TextView(Drivers.this);
                            TextView tb_type = new TextView(Drivers.this);

                            System.out.println(driver_status);

                            String fname = snapshot.child(driver_status.toString()).child("Driver Info").child("FirstName").getValue().toString();
                            String lname= snapshot.child(driver_status.toString()).child("Driver Info").child("LastName").getValue().toString();
                            String route = snapshot.child(driver_status.toString()).child("Driver Info").child("Route").getValue().toString();
                            String type = snapshot.child(driver_status.toString()).child("Driver Info").child("Type").getValue().toString();

                            status.setContentDescription(driver_status);
                            tb_name.setText(lname+", "+fname);
                            tb_route.setText(route);
                            tb_type.setText(type);

                            if(snapshot.child(driver_status.toString()).child("Type").getValue().toString().equals("Driver")){
                                status.setChecked(true);
                            }else{
                                status.setChecked(false);
                            }
                            status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if(compoundButton.isChecked()){
                                        dbRefUser.child(driver_status.toString()).child("Type").setValue("Driver");
                                    }else{
                                        dbRefUser.child(driver_status.toString()).child("Type").setValue("User");

                                    }
                                }
                            });

                            tb_name.setTextColor(Color.WHITE);
                            tb_route.setTextColor(Color.WHITE);
                            tb_type.setTextColor(Color.WHITE);

                            tb_name.setTypeface(ResourcesCompat.getFont(Drivers.this, R.font.main_font));
                            tb_route.setTypeface(ResourcesCompat.getFont(Drivers.this, R.font.main_font));
                            tb_type.setTypeface(ResourcesCompat.getFont(Drivers.this, R.font.main_font));

                            row.addView(status);
                            row.addView(tb_name);
                            row.addView(tb_route);
                            row.addView(tb_type);

                            tableLayout.addView(row);






                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}