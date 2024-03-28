package com.example.ovbha;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SosActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 456;
    TextView txtCo;
    Button btn112, btnCall, btnSms;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText editText;
    double latitude;
    double longitude;
    String textLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        txtCo = findViewById(R.id.txtCoord);
        btn112 = findViewById(R.id.btn112);
        btnCall = findViewById(R.id.btnCall);
        btnSms = findViewById(R.id.btnSms);
        editText = findViewById(R.id.inNumber);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        btn112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "112";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty())
                    Toast.makeText(SosActivity.this, "COMPLETE PHONE NUMBER TEXT!", Toast.LENGTH_SHORT).show();
                else {
                    String phoneNumber = editText.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SosActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    int MY_PERMISSIONS_REQUEST_SEND_SMS = 456;
                    ActivityCompat.requestPermissions(SosActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    sendSms();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                Toast.makeText(this, "Permisiunea pentru trimiterea SMS-urilor a fost refuzată", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms() {
        String phoneNumber = editText.getText().toString();
        String message = "SOS! Emergency! Need immediate assistance!\nMy Location:\n" + textLoc + "\nLink: " + "https://www.google.com/maps/search/?api=1&query=" + String.valueOf(latitude) + "," + String.valueOf(longitude);

        Uri uri = Uri.parse("smsto:" + phoneNumber);

        Intent smsIntent = new Intent(Intent.ACTION_VIEW, uri);

        smsIntent.putExtra("sms_body", message);

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(SosActivity.this, "Nu există aplicație de mesagerie disponibilă", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLocationAddress(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = String.format(
                        "%s, %s, %s, %s, %s",
                        address.getFeatureName(),
                        address.getThoroughfare(),
                        address.getLocality(),
                        address.getAdminArea(),
                        address.getCountryName());

                return addressText;
            } else {
                Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return "Adr.";
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    textLoc = "Latitudine: " + latitude + "\nLongitudine: " + longitude + "\nAdresa: " + getLocationAddress(currentLocation);
                    txtCo.setText(textLoc);
                }
            }
        });
    }
}