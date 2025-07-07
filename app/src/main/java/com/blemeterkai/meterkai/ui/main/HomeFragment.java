package com.blemeterkai.meterkai.ui.main; // Make sure this package is correct

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
// Make sure this import is correct based on your package and XML file name
import com.blemeterkai.meterkai.R; // For R.drawable access
import com.blemeterkai.meterkai.databinding.FragmentHomeBinding; // Generated ViewBinding class
import com.blemeterkai.meterkai.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // Declare binding variable
    private HomeViewModel mViewModel; // Your existing ViewModel

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); // Get the root view

        // Set up click listeners
        binding.cardMeterReading.setOnClickListener(v -> {
            // Handle Meter Reading click
            Toast.makeText(getContext(), "Meter Reading Clicked", Toast.LENGTH_SHORT).show();
            // Example navigation:
            // NavHostFragment.findNavController(HomeFragment.this)
            //        .navigate(R.id.action_homeFragment_to_meterReadingFragment);
        });

        binding.cardProfile.setOnClickListener(v -> {
            // Handle Profile click
            Toast.makeText(getContext(), "Profile Clicked", Toast.LENGTH_SHORT).show();
        });

        binding.cardUpload.setOnClickListener(v -> {
            // Handle Upload click
            Toast.makeText(getContext(), "Upload Clicked", Toast.LENGTH_SHORT).show();
        });

        // --- Example: Manually update status for demonstration ---
        // You would typically get this status from your Bluetooth/Printer services
        // or observe it from your ViewModel.
        updateBleStatus(false); // Initial state: offline
        updatePrinterStatus(false); // Initial state: offline

        // Example: Simulate connection changes after a delay (for testing UI)
        /*
        if (view != null) { // Ensure view is not null before posting runnables
            view.postDelayed(() -> {
                if (isAdded() && binding != null) { // Check if fragment is still added and binding is valid
                    updateBleStatus(true);
                }
            }, 3000);
            view.postDelayed(() -> {
                if (isAdded() && binding != null) {
                    updatePrinterStatus(true);
                }
            }, 5000);
        }
        */

        return view; // Return the root view
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // TODO: Use the ViewModel to observe connection states and call
        // updateBleStatus and updatePrinterStatus accordingly.
        // Example:
        /*
        if (mViewModel != null) {
            mViewModel.getBleConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
                if (binding != null) { // Always check if binding is null
                    updateBleStatus(isConnected);
                }
            });
            mViewModel.getPrinterConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
                if (binding != null) {
                    updatePrinterStatus(isConnected);
                }
            });
        }
        */
    }

    // Function to update BLE connection status UI
    public void updateBleStatus(boolean isConnected) {
        if (binding == null || !isAdded()) return; // Prevent NullPointerException if view is destroyed

        if (isConnected) {
            binding.imageViewBleStatus.setImageResource(R.drawable.ic_status_dot_green);
            binding.textViewBleStatus.setText("BLE Connection: Connected");
            // Optionally change text color:
            // binding.textViewBleStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_connected));
        } else {
            binding.imageViewBleStatus.setImageResource(R.drawable.ic_status_dot_red);
            binding.textViewBleStatus.setText("BLE Connection: Offline");
            // binding.textViewBleStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_disconnected));
        }
    }

    // Function to update Printer connection status UI
    public void updatePrinterStatus(boolean isConnected) {
        if (binding == null || !isAdded()) return; // Prevent NullPointerException if view is destroyed

        if (isConnected) {
            binding.imageViewPrinterStatus.setImageResource(R.drawable.ic_status_dot_green);
            binding.textViewPrinterStatus.setText("Woosim Printer: Connected");
        } else {
            binding.imageViewPrinterStatus.setImageResource(R.drawable.ic_status_dot_red);
            binding.textViewPrinterStatus.setText("Woosim Printer: Offline");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Important: Clear the binding when the view is destroyed to prevent memory leaks
    }

    // onActivityCreated is deprecated.
    // Use onViewCreated for view setup and ViewModel initialization.
    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ViewModel initialization is now typically done in onViewCreated or onCreateView
    }
    */
}