package edu.cuhk.csci3310.flowerycampus;

// TODO: Include your personal particular here
// Name: Lo Tsz Yuk
// SID: 1155133625

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "FloweryCampus";
    private GoogleMap mMap;
    String mParam1="";
    static LinkedList<String[]> mInfoList = new LinkedList<>();
    Stack<Marker> mStack = new Stack<Marker>();
    List<Marker> mMarkers = new ArrayList<Marker>();
    ArrayList<LatLng> mLoc = new ArrayList<LatLng>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();


    // TODO: Define other attributes as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // By default map fragment is added statically via the SupportMapFragment obtained
        // and get notified when the map is ready to be used.
        //
        // TODO: Modify the following code to include mapFragment and other fragments dynamically
       // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // By default a marker in SHB of CUHK is added and move the camera
        //
        // TODO: Modify the following code to
        // 1. read flowery locations from a CSV,

        //Read csv
        String line;
        InputStream file = this.getResources().openRawResource(R.raw.cu_flowers);
        Scanner scanner = new Scanner(file);

        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] data = line.split(",");
                mInfoList.addLast(data);
            }
        }catch(Exception ex){
        }

        // 2. set up markers, view bounds and zoom
        LatLng northEast = new LatLng(22.4213136,114.2089727);
        LatLng southEast = new LatLng(22.4195015,114.2033456);
        builder.include(northEast);
        builder.include(southEast);
        for(int i=1; i<10;i++) {
            LatLng latLng = new LatLng(Float.parseFloat(mInfoList.get(i)[2]),Float.parseFloat(mInfoList.get(i)[3]));

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .position(latLng)
                    .title(mInfoList.get(i)[1]));
            marker.setTag(i);

            //googleMap.setOnMarkerClickListener(this);
            mMarkers.add(marker);
            //builder.include(marker.getPosition());
        }
/*
        LatLng mid_pt = new LatLng(22.4198032,114.2064046);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLoc.get(1)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
*/
        // 3. add listeners to handle different map clicking events
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,150));
            }
        });

        // 4. include extra data structure to handle non-system "Back-pressing" states

    }

    /**
     * Include customized handling on pressing Back button
     * This callback is triggered when the Back is pressed.
     * This is where we can include extra BackStack handling not done by system by default
     * e.g. markers status etc.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
        // TODO: include addition BackStack handling, e.g. for markers, here
        //
        Marker pMark;

        if (mStack.size()>0){
            if (mStack.size()==1){
                pMark = mStack.pop();
                closeFragment();
                LatLng mid_pt = new LatLng(22.4198032,114.2064046);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mid_pt));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                for(Marker otherMarker : mMarkers) {
                    otherMarker.hideInfoWindow();
                    otherMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }
                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    getSupportFragmentManager().popBackStack();
                }


            }else{
                pMark = mStack.pop();

                if (mParam1.equals(String.valueOf(pMark.getTag())) && mStack.size()>0){
                    pMark = mStack.pop();
                }

                closeFragment();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pMark.getPosition(), 18.0F));
                for(Marker otherMarker : mMarkers) {
                    otherMarker.hideInfoWindow();
                    otherMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }
                pMark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mParam1 = String.valueOf(pMark.getTag());
                displayFragment();
            }
        }
        else{
            //return to default start point
            LatLng mid_pt = new LatLng(22.4198032,114.2064046);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mid_pt));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getSupportFragmentManager().popBackStack();
            }
            for(Marker otherMarker : mMarkers) {
                otherMarker.hideInfoWindow();
                otherMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        closeFragment();
        mStack.push(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0F));
        for(Marker otherMarker : mMarkers) {
            otherMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mParam1 = String.valueOf(marker.getTag());
        displayFragment();
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        LatLng mid_pt = new LatLng(22.4198032,114.2064046);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mid_pt));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        closeFragment();
        mStack.clear();
        for(Marker otherMarker : mMarkers) {
            otherMarker.hideInfoWindow();
            otherMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        }

    }

    // TODO: Add more utility methods, e.g. readCSV as needed
    public void displayFragment() {
        PhotoFragment simpleFragment = PhotoFragment.newInstance(mParam1);

        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the SimpleFragment.
        fragmentTransaction.add(R.id.fragment_container, simpleFragment).addToBackStack(null).commit();
    }

    public void closeFragment() { // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager(); // Check to see if the fragment is already showing.
        PhotoFragment simpleFragment = (PhotoFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(simpleFragment).commit(); }
        }
}