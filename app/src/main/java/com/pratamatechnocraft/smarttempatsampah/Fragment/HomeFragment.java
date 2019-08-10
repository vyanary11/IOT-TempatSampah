package com.pratamatechnocraft.smarttempatsampah.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pratamatechnocraft.smarttempatsampah.Model.TempatSampah;
import com.pratamatechnocraft.smarttempatsampah.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    View view;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button buttonCariRute;
    private ArrayList<TempatSampah> tempatSampahArrayList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_home, container, false);
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear();
        databaseReference.child("lokasi_utama").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MarkerOptions markerAwal = new MarkerOptions().position(new LatLng(-8.13967, 113.719998));
                markerAwal.title("Dinas Kebersihan Kab. Jember");
                markerAwal.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_garbage_truck));
                mMap.addMarker(markerAwal);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(-8.13967, 113.719998))
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
                    googleMap.addMarker(markerOptions);
                    googleMap.setInfoWindowAdapter(new GoogleMapsInfoWindow(statusTerisi,statusBaterai));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    class GoogleMapsInfoWindow implements GoogleMap.InfoWindowAdapter {

        private final View viewGoogleMapsInfoWindow;
        private String statusTerisi;
        private String statusBaterai;
        public GoogleMapsInfoWindow(String statusTerisi, String statusBaterai) {
            viewGoogleMapsInfoWindow = getLayoutInflater().inflate(R.layout.fragment_info_window_google_maps, null);
            this.statusBaterai= statusBaterai;
            this.statusTerisi = statusTerisi;

        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView txtTitle, txtStatusTerisi, txtStatusBaterai;
            txtTitle = viewGoogleMapsInfoWindow.findViewById(R.id.txtTitle);
            txtStatusTerisi = viewGoogleMapsInfoWindow.findViewById(R.id.txtStatusTerisi);
            txtStatusBaterai = viewGoogleMapsInfoWindow.findViewById(R.id.txtStatusBaterai);

            txtTitle.setText(marker.getTitle());
            txtStatusTerisi.setText(statusTerisi);
            txtStatusBaterai.setText(statusBaterai);
            return viewGoogleMapsInfoWindow;
        }
    }
}
