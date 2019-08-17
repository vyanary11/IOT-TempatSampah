package com.pratamatechnocraft.smarttempatsampah.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pratamatechnocraft.smarttempatsampah.MainActivity;
import com.pratamatechnocraft.smarttempatsampah.Model.InfoWindowMap;
import com.pratamatechnocraft.smarttempatsampah.Model.TempatSampah;
import com.pratamatechnocraft.smarttempatsampah.R;
import com.pratamatechnocraft.smarttempatsampah.Utils.directionhelpers.FetchURL;
import com.pratamatechnocraft.smarttempatsampah.Utils.directionhelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowCloseListener, TaskLoadedCallback {
    private GoogleMap mMap;
    View view;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button buttonCariRute;
    private ArrayList<TempatSampah> tempatSampahArrayList;
    NavigationView navigationView;
    private int jenisFragment;
    private Long idHistori;
    private Polyline currentPolyline;
    private MarkerOptions markerAwal;
    private HashMap<String, HashMap<String, String>> hashMapHashMapStatus = new HashMap<>();
    private boolean isInfoWindowShown = false;
    String lastMarkerKlik;

    public HomeFragment(int jenisFragment, Long idHistori) {
        this.jenisFragment = jenisFragment;
        this.idHistori = idHistori;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_home, container, false);
        navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonCariRute = view.findViewById(R.id.buttonCariRute);
        buttonCariRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitungRuteTerpendek();
            }
        });
        return view;
    }

    private void hitungRuteTerpendek() {
        new FetchURL(getContext()).execute(getUrl(markerAwal.getPosition(), new LatLng(-8.141689, 113.721291), "driving"), "driving");
        /*String goolgeMap = "com.google.android.apps.maps"; // identitas package aplikasi google masps android
        Uri gmmIntentUri;
        Intent mapIntent;
        Double longitudeOrigin=markerAwal.getPosition().longitude;
        Double latitudeOrigin=markerAwal.getPosition().latitude;
        String origin = latitudeOrigin.toString()+","+longitudeOrigin.toString();
        String wayPoints = "-8.158879,113.721337%7C-8.164735,113.717475%7C-8.145681,113.724939";
        // Buat Uri dari intent string. Gunakan hasilnya untuk membuat Intent.
        gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin="+origin+"&destination="+origin+"&travelmode=driving&waypoints="+wayPoints);

        // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
        mapIntent.setPackage(goolgeMap);

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }*/
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(R.string.app_name);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.clear();
        databaseReference.child("lokasi_utama").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markerAwal = new MarkerOptions().position(new LatLng(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longtitude").getValue(Double.class)));
                markerAwal.title(dataSnapshot.child("nama").getValue(String.class));
                markerAwal.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_garbage_truck));
                mMap.addMarker(markerAwal);
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longtitude").getValue(Double.class)))
                        .zoom(15)
                        .bearing(0)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("tempat_sampah").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataSnapshot1.child("latitude").getValue(Double.class), dataSnapshot1.child("longtitude").getValue(Double.class)));
                    markerOptions.title(dataSnapshot1.child("nama").getValue().toString().trim());
                    String statusTerisi;
                    String statusBaterai;
                    if (dataSnapshot1.child("status_terisi").getValue().toString().equals("0")){
                        statusTerisi="Kosong";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_kosong));
                    }else if(dataSnapshot1.child("status_terisi").getValue().toString().equals("1")){
                        statusTerisi="Setengah";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_setengah));
                    }else{
                        statusTerisi="Penuh";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_penuh));
                    }
                    if (dataSnapshot1.child("status_baterai").getValue().toString().equals("0")){
                        statusBaterai="Kosong";
                    }else if(dataSnapshot1.child("status_baterai").getValue().toString().equals("1")){
                        statusBaterai="Setengah";
                    }else{
                        statusBaterai="Penuh";
                    }
                    Marker marker = mMap.addMarker(markerOptions);
                    mMap.addMarker(markerOptions);
                    HashMap<String, String> hashMapStatusValue = new HashMap<>();
                    hashMapStatusValue.put("statusBaterai",statusBaterai);
                    hashMapStatusValue.put("statusTerisi",statusTerisi);
                    hashMapHashMapStatus.put(markerOptions.getTitle(),hashMapStatusValue);
                    mMap.setInfoWindowAdapter(new GoogleMapsInfoWindow(hashMapHashMapStatus));
                    if (isInfoWindowShown && marker.getTitle().equals(lastMarkerKlik)){
                        marker.showInfoWindow();
                        isInfoWindowShown=true;
                        lastMarkerKlik=marker.getTitle();
                    }
                }
                Log.d("TAG", "onMapReady: "+hashMapHashMapStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Double getDistanceBetween(LatLng latLon1, LatLng latLon2) {
        if (latLon1 == null || latLon2 == null)
            return null;
        float[] result = new float[1];
        Location.distanceBetween(latLon1.latitude, latLon1.longitude,
                latLon2.latitude, latLon2.longitude, result);
        return (double) result[0];
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!isInfoWindowShown) {
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            isInfoWindowShown = true;
            lastMarkerKlik=marker.getTitle();
        }
        return true;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        isInfoWindowShown = false;
        lastMarkerKlik="";
    }

    class GoogleMapsInfoWindow implements GoogleMap.InfoWindowAdapter {
        private final View viewGoogleMapsInfoWindow;
        private HashMap<String, HashMap<String, String>> stringArrayListStatus;
        public GoogleMapsInfoWindow(HashMap<String, HashMap<String, String>> stringArrayListStatus) {
            viewGoogleMapsInfoWindow = getLayoutInflater().inflate(R.layout.fragment_info_window_google_maps, null);
            this.stringArrayListStatus= stringArrayListStatus;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            final ImageView imageViewMarkerInfoWindow;
            final TextView txtTitle, txtStatusTerisi, txtStatusBaterai;
            TableLayout tableStatusInfoWindow;
            imageViewMarkerInfoWindow = viewGoogleMapsInfoWindow.findViewById(R.id.imageViewMarkerInfoWindow);
            txtTitle = viewGoogleMapsInfoWindow.findViewById(R.id.txtTitle);
            txtStatusTerisi = viewGoogleMapsInfoWindow.findViewById(R.id.txtStatusTerisi);
            txtStatusBaterai = viewGoogleMapsInfoWindow.findViewById(R.id.txtStatusBaterai);
            tableStatusInfoWindow = viewGoogleMapsInfoWindow.findViewById(R.id.tableStatusInfoWindow);
            txtTitle.setText(marker.getTitle());
            if (stringArrayListStatus.get(marker.getTitle()) == null){
                tableStatusInfoWindow.setVisibility(View.GONE);
                marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_garbage_truck));
                imageViewMarkerInfoWindow.setBackgroundResource(R.drawable.truck);
            }else{
                tableStatusInfoWindow.setVisibility(View.VISIBLE);
                txtStatusTerisi.setText(stringArrayListStatus.get(marker.getTitle()).get("statusTerisi"));
                txtStatusBaterai.setText(stringArrayListStatus.get(marker.getTitle()).get("statusBaterai"));

                if (txtStatusTerisi.getText().equals("Kosong")){
                    marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_kosong));
                    imageViewMarkerInfoWindow.setBackgroundResource(R.drawable.kosong);
                }else if(txtStatusTerisi.getText().equals("Setengah")){
                    marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_setengah));
                    imageViewMarkerInfoWindow.setBackgroundResource(R.drawable.setengah);
                }else{
                    marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_penuh));
                    imageViewMarkerInfoWindow.setBackgroundResource(R.drawable.penuh);
                }
            }

            return viewGoogleMapsInfoWindow;
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
