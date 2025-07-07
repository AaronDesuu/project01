package com.blemeterkai.meterkai.ui; // Adjust your package

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.blemeterkai.meterkai.R; // Ensure R is imported correctly
import com.blemeterkai.meterkai.databinding.FragmentMainContainerBinding; // Import ViewBinding

public class MainContainerFragment extends Fragment {

    private FragmentMainContainerBinding binding;

    public MainContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainContainerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // It's crucial to use getChildFragmentManager() for nested NavHostFragments.
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.main_content_nav_host_fragment); // ID from fragment_main_container.xml

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        } else {
            // Handle error: NavHostFragment not found
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}