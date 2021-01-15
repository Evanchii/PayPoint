package xyz.paypnt.paypoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("History");
        setContentView(R.layout.history);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button book = (Button) findViewById(R.id.his_book);
        book.setOnClickListener(v -> startActivity(new Intent(History.this, MapActivity.class)));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid= mAuth.getCurrentUser().getUid();
        System.out.println(uid);
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("History");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TableLayout table = (TableLayout) findViewById(R.id.history_tblHistory);
                if (snapshot.hasChildren())
                    ((ConstraintLayout) findViewById(R.id.history_nothing)).setVisibility(View.GONE);
                for (DataSnapshot a : snapshot.getChildren()) {
                    TableRow row = new TableRow(History.this);
                    row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT));

                    TextView txtDateTime = new TextView(History.this);
                    TextView txtDetails = new TextView(History.this);
                    TextView txtAmount = new TextView(History.this);

                    String dateTime = a.child("TimeDate").getValue().toString();
                    String source = a.child("Source").getValue().toString();
                    String destination = a.child("Destination").getValue().toString();
                    String driver = a.child("Driver").getValue().toString();
                    String type = a.child("Type").getValue().toString();
                    String price = a.child("Price").getValue().toString();
                    System.out.println(dateTime);

                    txtDateTime.setText(dateTime);
                    txtDetails.setText("Source: "+source+"\nDestination: "+destination+"\nDriver: "+driver+"\nType: "+type);
                    txtAmount.setText(price);

//                    txtDateTime.setBackgroundColor(Color.parseColor("#A3515151"));
//                    txtDetails.setBackgroundColor(Color.parseColor("#A3515151"));
//                    txtAmount.setBackgroundColor(Color.parseColor("#A3515151"));

                    txtDateTime.setTextColor(Color.WHITE);
                    txtDetails.setTextColor(Color.WHITE);
                    txtAmount.setTextColor(Color.WHITE);

                    float scale = getResources().getDisplayMetrics().density;

                    txtDateTime.setPadding(0,0, (int) ( 8*scale + 0.5f),0);
                    txtDetails.setPadding(0,0, (int) ( 8*scale + 0.5f),0);
                    txtAmount .setPadding(0,0, (int) ( 2*scale + 0.5f),0);

                    txtDateTime.setTypeface(ResourcesCompat.getFont(History.this, R.font.main_font));
                    txtDetails.setTypeface(ResourcesCompat.getFont(History.this, R.font.main_font));
                    txtAmount.setTypeface(ResourcesCompat.getFont(History.this, R.font.main_font));

                    row.addView(txtDateTime);
                    row.addView(txtDetails);
                    row.addView(txtAmount);

                    row.setBackgroundColor(Color.parseColor("#A3757575"));

                    table.addView(row);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        startActivity(new Intent(History.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(CommonFunctions.menu(this, item, "History"))
            finish();
        return true;
    }
}