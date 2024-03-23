package com.example.ovbha;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
        // Inițializare client de localizare
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obține ultima locație cunoscută
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
                // Verificăm dacă există permisiune pentru a trimite SMS
                if (ActivityCompat.checkSelfPermission(SosActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Dacă nu avem permisiune, o solicităm
                    int MY_PERMISSIONS_REQUEST_SEND_SMS = 456;
                    ActivityCompat.requestPermissions(SosActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    // Dacă avem permisiune, trimitem SMS-ul
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
                // Permisiunea pentru trimiterea SMS-urilor a fost acordată, trimite SMS-ul
                sendSms();
            } else {
                // Permisiunea pentru trimiterea SMS-urilor a fost refuzată
                Toast.makeText(this, "Permisiunea pentru trimiterea SMS-urilor a fost refuzată", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms() {
        String phoneNumber = editText.getText().toString();
        String message = "SOS! Emergency! Need immediate assistance!\nMy Location:\n" + textLoc + "\nLink: " + "https://www.google.com/maps/search/?api=1&query=" + String.valueOf(latitude) + "," + String.valueOf(longitude);

        // Creăm un URI pentru compunerea unui mesaj SMS
        Uri uri = Uri.parse("smsto:" + phoneNumber);

        // Creăm un Intent de tip ACTION_VIEW pentru deschiderea aplicației de mesagerie cu numărul și mesajul completat
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, uri);

        // Adăugăm mesajul SMS în corpul Intent-ului
        smsIntent.putExtra("sms_body", message);

        // Verificăm dacă există o aplicație de mesagerie disponibilă pentru a trimite SMS
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(SosActivity.this, "Nu există aplicație de mesagerie disponibilă", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Dacă nu avem permisiunea, solicită-o
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // Obține locația curentă
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    textLoc = "Latitudine: " + latitude + "\nLongitudine: " + longitude;
                    txtCo.setText(textLoc);
                }
            }
        });
    }
}