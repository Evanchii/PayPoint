package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,DirectionFinderListener {

    private GoogleMap mMap;
    private EditText map_Origin, map_Destination;
    private Button find_Path, next;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>(), destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private RelativeLayout info;
    private RadioButton jeep, taxi, bus;
    private RadioGroup type;
    private String typeSel = "";
    private float priceSel = 0, distance = 0;
    private FusedLocationProviderClient userLocation;
    private Double priceBus = 0.00, priceJeep = 0.00, priceTaxi = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        String Area = "Dagupan City";
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Driver").child("Areas").child(Area);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                priceBus = Double.parseDouble(snapshot.child("Bus").getValue().toString());
                priceJeep = Double.parseDouble(snapshot.child("Jeep").getValue().toString());
                priceTaxi = Double.parseDouble(snapshot.child("Taxi").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        userLocation = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

        map_Origin=(EditText)findViewById(R.id.map_Origin);
        map_Destination=(EditText)findViewById(R.id.map_Distination);
        find_Path=(Button)findViewById(R.id.findPath);

        find_Path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        type = (RadioGroup) findViewById(R.id.map_type);
        jeep = (RadioButton) type.findViewById(R.id.map_jeep);
        taxi = (RadioButton) type.findViewById(R.id.map_taxi);
        bus = (RadioButton) type.findViewById(R.id.map_bus);
        next = (Button) findViewById(R.id.map_next);

        next.setOnClickListener((View.OnClickListener) v -> startActivity(new Intent(MapActivity.this, PriceBreakDown.class)
                .putExtra("type", typeSel)
                .putExtra("origin", String.valueOf(map_Origin.getText()))
                .putExtra("destination", String.valueOf(map_Destination.getText()))
                .putExtra("price", (float)priceSel)
                .putExtra("distance", (float)distance)));

        type.setOnCheckedChangeListener((group, checkedId) -> {
            next.setVisibility(View.VISIBLE);
            if(checkedId == jeep.getId()) {
                typeSel = "Jeep";
                priceSel = (distance>5) ? (float) (((distance - 5) * priceJeep) + 10) : (float) 10.00;
            } else if (checkedId == taxi.getId()) {
                typeSel = "Taxi";
                priceSel = (float) (distance*priceTaxi);
            } else if (checkedId == bus.getId()) {
                typeSel = "Bus";
                priceSel = (float) (distance*priceBus);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        // Add a marker in position and move the camera
        userLocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18));
                    map_Origin.setText(String.format(location.getLatitude() + ", "+location.getLongitude()));
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mMap.setMyLocationEnabled(true);
    }

    private void sendRequest() {
        String Origin=map_Origin.getText().toString();
        String Destination=map_Destination.getText().toString();

        if (Origin.isEmpty()){
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Destination.isEmpty()){
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder( this, Origin, Destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(MapActivity.this, "An error occurred while fetching API. See logs", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait",
                "Finding direction...", true);
        progressDialog.setCancelable(true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            distance = route.distance.value/1000;

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        info = (RelativeLayout) findViewById(R.id.map_details);
        info.setVisibility(View.VISIBLE);

        jeep.setText("Jeepney\nPhp "+ String.valueOf(String.valueOf((distance>5) ? ((distance-5)*priceJeep)+10 : 10.00)));
        jeep.setContentDescription(String.valueOf((distance>5) ? ((distance-5)*priceJeep)+10 : 10.00));
        taxi.setText("Taxi\nPhp "+String.valueOf(distance*priceTaxi));
        taxi.setContentDescription(String.valueOf(distance*priceTaxi));
        bus.setText("Bus\nPhp "+String.valueOf(distance*priceBus));
        bus.setContentDescription(String.valueOf(distance*priceBus));
    }
}