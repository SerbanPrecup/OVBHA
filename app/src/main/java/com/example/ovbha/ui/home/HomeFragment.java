package com.example.ovbha.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ovbha.MainActivity;
import com.example.ovbha.R;
import com.example.ovbha.SosActivity;
import com.example.ovbha.WeatherActivity;
import com.example.ovbha.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ImageView imgMaps, imgCheckList, imgWheater, imgHotel, imgService,imgRestaurant;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgMaps = root.findViewById(R.id.imgMaps);
        imgCheckList = root.findViewById(R.id.imgCheckList);
        imgWheater = root.findViewById(R.id.imgWeather);
        imgService = root.findViewById(R.id.imgService);
        imgHotel = root.findViewById(R.id.imgHotel);
        imgRestaurant = root.findViewById(R.id.imgRestaurant);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        imgMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.map_fragment);
            }
        });

        imgWheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });

        imgCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.check_fragment);
            }

        });

        imgHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=hotels");

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                Toast.makeText(requireContext(), "No application found to handle the request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        imgService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=auto+service");

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {

                                startActivity(mapIntent);
                            } else {
                                Toast.makeText(requireContext().getApplicationContext(), "No application found to handle the request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });


        imgRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=restaurants");

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                Toast.makeText(requireContext(), "No application found to handle the request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}