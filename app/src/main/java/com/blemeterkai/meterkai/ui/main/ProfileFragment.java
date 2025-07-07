package com.blemeterkai.meterkai.ui.main; // Adjust package as needed

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blemeterkai.meterkai.MainActivity;
import com.blemeterkai.meterkai.R;
import com.blemeterkai.meterkai.auth.SessionManager;
import com.blemeterkai.meterkai.utils.LevelUtils;

public class ProfileFragment extends Fragment {

    // Declare View variables
    private ImageView imageViewProfilePic;
    private TextView textViewProfileName;
    private TextView textViewProfileEmail;
    private TextView textViewProfileRole;
    private Button buttonEditProfile;
    private ImageView imageViewLogout;
    private SessionManager sessionManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize SessionManager
        if (getContext() != null) {
            sessionManager = new SessionManager(getContext());
        }

        // Initialize Views
        imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
        textViewProfileName = view.findViewById(R.id.textViewProfileName);
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail);
        textViewProfileRole = view.findViewById(R.id.textViewProfileRole);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        imageViewLogout = view.findViewById(R.id.imageViewLogout);

        // Load example profile data
        loadExampleProfileData();

        // Set up listeners
        buttonEditProfile.setOnClickListener(v -> {
            // Handle edit profile action
            // For now, just show a Toast message
            if (getContext() != null) {
                Toast.makeText(getContext(), "Edit Profile Clicked!", Toast.LENGTH_SHORT).show();
            }
            // Later, you might navigate to an EditProfileFragment or open a dialog
        });
        if (imageViewLogout != null) {
            imageViewLogout.setOnClickListener(v -> {
                if (getContext() != null && sessionManager != null) {
                    //sessionManager.logoutUser();
                    MainActivity.Login = null;
                    LevelUtils.Level = null;

                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                    try {
                        if (getActivity() != null) {
                            // Use the ID from your content_main.xml
                            NavController mainNavController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

                            // Navigate using the global action defined in nav_graph.xml
                            mainNavController.navigate(R.id.action_global_navigateToLoginFragment);

                        } else {
                            Toast.makeText(getContext(), "Activity not available for navigation.", Toast.LENGTH_LONG).show();
                        }

                    } catch (IllegalArgumentException e) {
                        // This error should ideally not happen now if IDs are correct and graph is sound
                        Toast.makeText(getContext(), "Error navigating to login. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) { // Catch any other unexpected navigation errors
                        Toast.makeText(getContext(), "Unexpected error during navigation.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Error: Could not perform logout.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadExampleProfileData() {
        // In a real app, you'd fetch this from a ViewModel, SharedPreferences, database, or server
        String exampleName = "Admin User";
        String exampleEmail = "admin@example.com";
        String exampleRole = "Administrator"; // You could get this from your login logic

        // Set data to TextViews
        textViewProfileName.setText(exampleName);
        textViewProfileEmail.setText(exampleEmail);
        textViewProfileRole.setText(exampleRole);

        // You could also set a real profile image here if you have one
        // For now, it will use the placeholder defined in XML
        // e.g., using Glide or Picasso:
        // if (getContext() != null) {
        //     Glide.with(this).load("url_to_profile_image.jpg").into(imageViewProfilePic);
        // }
    }
}