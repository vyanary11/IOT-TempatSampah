package com.pratamatechnocraft.smarttempatsampah.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pratamatechnocraft.smarttempatsampah.R;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    View view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button buttonCariRute;
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

            }
        });
        return view;
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

        final CollectionReference collectionReference = db.collection("tempat_sampah");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear();
                int i=0;
                for (QueryDocumentSnapshot document : value) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    GeoPoint geoPoint = document.getGeoPoint("lokasi");
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                    markerOptions.title(document.getData().get("nama").toString().trim());

                    String statusTerisi;
                    String statusBaterai;
                    if (document.getData().get("status_terisi").equals("0")){
                        statusTerisi="Kosong";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_kosong));
                    }else if(document.getData().get("status_terisi").equals("1")){
                        statusTerisi="Setengah";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_setengah));
                    }else{
                        statusTerisi="Penuh";
                        markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_penuh));
                    }
                    if (document.getData().get("status_baterai").equals("0")){
                        statusBaterai="Kosong";
                    }else if(document.getData().get("status_baterai").equals("1")){
                        statusBaterai="Setengah";
                    }else{
                        statusBaterai="Penuh";
                    }
                    markerOptions.snippet(statusTerisi);
                    mMap.addMarker(markerOptions);
                    if (i==0){
                        CameraPosition googlePlex = CameraPosition.builder()
                                .target(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()))
                                .zoom(15)
                                .bearing(0)
                                .tilt(45)
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                    }
                    i++;
                }
            }
        });
        /*db.collection("tempat_sampah")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int i=0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            GeoPoint geoPoint = document.getGeoPoint("lokasi");
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                            markerOptions.title(document.getData().get("nama").toString().trim());

                            String statusTerisi;
                            String statusBaterai;
                            if (document.getData().get("status_terisi").equals("0")){
                                statusTerisi="Kosong";
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_kosong));
                            }else if(document.getData().get("status_terisi").equals("1")){
                                statusTerisi="Setengah";
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_setengah));
                            }else{
                                statusTerisi="Penuh";
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_trash_penuh));
                            }
                            if (document.getData().get("status_baterai").equals("0")){
                                statusBaterai="Kosong";
                            }else if(document.getData().get("status_baterai").equals("1")){
                                statusBaterai="Setengah";
                            }else{
                                statusBaterai="Penuh";
                            }
                            markerOptions.snippet(statusTerisi);
                            mMap.addMarker(markerOptions);
                            if (i==0){
                                CameraPosition googlePlex = CameraPosition.builder()
                                        .target(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()))
                                        .zoom(15)
                                        .bearing(0)
                                        .tilt(45)
                                        .build();

                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                            }
                            i++;
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });*/
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
}
