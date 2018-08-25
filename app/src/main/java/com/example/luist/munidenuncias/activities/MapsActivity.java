package com.example.luist.munidenuncias.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luist.munidenuncias.R;
import com.example.luist.munidenuncias.adapters.PostRVAdapter;
import com.example.luist.munidenuncias.models.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST = 100;

    private GoogleMap mMap;
    private  List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initMap();
            }
        });
    }

    final PostRVAdapter postRVAdapter = new PostRVAdapter();

    private void initMap(){

        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return;
        }

        // Habilita la posicion Actual a tiempo real
        mMap.setMyLocationEnabled(true);

        // Custom UiSettings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);    // Controles de zoom
        uiSettings.setCompassEnabled(true); // Brújula
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // Obteniendo nuevo post de Firebase
                String postKey = dataSnapshot.getKey();
                final Post addedPost = dataSnapshot.getValue(Post.class);

                // Actualizando adapter datasource
                posts = postRVAdapter.getPosts();
                posts.add(0, addedPost);

                LatLng latLng = new LatLng(addedPost.getLatitude(), addedPost.getLongitude());

                Marker marker= mMap.addMarker(new MarkerOptions().position(latLng).title(addedPost.getTitle()).snippet(addedPost.getBody()));

                marker.showInfoWindow();

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        // Custom InfoWindow: https://developers.google.com/maps/documentation/android-api/infowindows?hl=es-419#ventanas_de_informacion_personalizadas
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Not implemented yet
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.info_contents, null);

                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.mipmap.ic_launcher);

                TextView titleText = view.findViewById(R.id.title);
                titleText.setText(marker.getTitle());

                TextView snippetText = view.findViewById(R.id.snippet);
                snippetText.setText(marker.getSnippet());

                return view;
            }
        });

        postsRef.addChildEventListener(childEventListener);

    }

}
