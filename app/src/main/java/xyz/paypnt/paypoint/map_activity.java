package xyz.paypnt.paypoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xyz.paypnt.paypoint.R;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class map_activity extends AppCompatActivity implements OnMapReadyCallback,DirectionFinderListener {

    private GoogleMap mMap;
    private EditText map_Origin;
    private EditText map_Destination;
    private Button find_Path;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Button next;
    private RelativeLayout info;
    private RadioButton jeep, taxi, bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(map_activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }

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

        next = (Button) findViewById(R.id.map_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(map_activity.this, PriceBreakDown.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        // Add a marker in position and move the camera
        LatLng position = new LatLng(16.049484398586554, 120.33254164793823);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18));
        mMap.addMarker(new MarkerOptions().position(position).title("Your position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        originMarkers.add(mMap.addMarker(new MarkerOptions().title("OPPS").position(position)));


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
            Toast.makeText(map_activity.this, "An error occurred while fetching API. See logs", Toast.LENGTH_LONG).show();
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
        double distance = 0;

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            Toast.makeText(this, route.distance.text+" -- "+route.distance.value, Toast.LENGTH_LONG).show();
            distance = route.distance.value/1000;

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
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

        jeep = (RadioButton) findViewById(R.id.map_jeep);
        taxi = (RadioButton) findViewById(R.id.map_taxi);
        bus = (RadioButton) findViewById(R.id.map_bus);

        Toast.makeText(this, String.valueOf(distance), Toast.LENGTH_LONG).show();
        jeep.setText("Jeepney\nPhp "+ String.valueOf((distance>5) ? ((distance-5)*3)+10 : 10.00));
        taxi.setText("Taxi\nPhp "+String.valueOf(distance*20));
        bus.setText("Bus\nPhp "+String.valueOf(distance*2));
    }
}