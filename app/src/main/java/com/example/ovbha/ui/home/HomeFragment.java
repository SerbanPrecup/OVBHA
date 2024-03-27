package com.example.ovbha.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ovbha.MainActivity;
import com.example.ovbha.R;
import com.example.ovbha.SosActivity;
import com.example.ovbha.WeatherActivity;
import com.example.ovbha.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Button btnShareLocation;
    private Button btnCheckList;
    private Button btnWeather;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Găsirea elementelor din XML
        btnShareLocation = root.findViewById(R.id.btnShareLocation);
        btnCheckList = root.findViewById(R.id.btnCheckList);
        btnWeather = root.findViewById(R.id.btnWeather);

        // Setarea acțiunilor pentru butoane
        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(requireContext(), SosActivity.class);
                startActivity(intent);
            }
        });

        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });

        btnCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_navigation_home_to_check_fragment);
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