package com.example.ovbha;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

public class MapsFragment extends Fragment {
    private GoogleMap googleMap;
    private ArrayList<Marker> hotelMarkers = new ArrayList<>();
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;

    FusedLocationProviderClient fusedLocationProviderClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
//            MapsFragment.this.googleMap = googleMap;
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
//            searchHotels();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBQTY0sOKHIZ8yYjy2eTij64uSwq8SaVhs");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLastLocation();
        if (getActivity() != null) {
            androidx.appcompat.app.ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLastLocation();
                } else {
                    Toast.makeText(requireContext(), "Location permission is denied.", Toast.LENGTH_SHORT).show();
                }
            }
    );

//    private void searchHotels() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        LatLng searchLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//        PlacesClient placesClient = Places.createClient(requireContext());
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
//        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
//
//        placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {
//            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                if (placeLikelihood.getPlace().getTypes().contains(Place.Type.LODGING)) {
//                    LatLng hotelLatLng = placeLikelihood.getPlace().getLatLng();
//                    if (hotelLatLng != null) {
//                        Marker marker = googleMap.addMarker(new MarkerOptions()
//                                .position(hotelLatLng)
//                                .title(placeLikelihood.getPlace().getName()));
//                        hotelMarkers.add(marker);
//                    }
//                }
//            }
//        }).addOnFailureListener((exception) -> {
//            Toast.makeText(requireContext(), "Failed to search for hotels.", Toast.LENGTH_SHORT).show();
//        });
//    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment =
                            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(callback);
                    }
                }
            }
        });
    }

}
