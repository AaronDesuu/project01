package com.blemeterkai.meterkai.ui.login;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.blemeterkai.meterkai.MainActivity;
import com.blemeterkai.meterkai.utils.LevelUtils;
import com.blemeterkai.meterkai.R;
// Import TestLoginData and its inner class
import com.blemeterkai.meterkai.auth.SessionManager; // Import SessionManager
import com.blemeterkai.meterkai.auth.TestLoginData;
import com.blemeterkai.meterkai.auth.TestLoginData.LoginCredentials; // Assuming TestLoginData is in the same package
import com.blemeterkai.meterkai.databinding.FragmentLoginBinding;
import com.blemeterkai.meterkai.ui.main.meter.SecondFragment; // Keep if TAG is used, otherwise remove
import com.blemeterkai.meterkai.ui.main.item.ItemFragment;

import java.util.List; // Import List

public class LoginFragment extends ItemFragment {

    private final static String TAG = SecondFragment.class.getSimpleName(); // Or LoginFragment.class.getSimpleName()
    private FragmentLoginBinding binding;
    private messageManager mCallback;
    private List<LoginCredentials> userCredentialsList; // To store the list of users
    private SessionManager sessionManager; // Add SessionManager instance

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach.");
        Activity a = getActivity();
        if (!(a instanceof messageManager)) { // Simpler check
            throw new ClassCastException("Activity have to implement LoginFragment.messageManager");
        }
        mCallback = (messageManager) a;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mCallback.fragment(this);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.textView3.setText("");
        binding.editAccount.setText("");

        // Get user credentials from TestLoginData
        userCredentialsList = TestLoginData.getUsers();
        sessionManager = new SessionManager(requireContext()); // Initialize SessionManager

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.Login = null;
        LevelUtils.Level = null;
        MainActivity.mFragmentid = 0;
        mCallback.fragmentOrder(MainActivity.ODR_UPDATE);

        binding.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputUsername = binding.editAccount.getText().toString();
                String inputPassword = binding.editPassword.getText().toString();
                boolean foundUser = false;
                LoginCredentials loggedInUser = null;

                for (LoginCredentials user : userCredentialsList) {
                    if (user.username.equals(inputUsername)) {
                        if (user.password.equals(inputPassword)) {
                            loggedInUser = user;
                            foundUser = true;
                            break;
                        }
                    }
                }

                if (foundUser && loggedInUser != null) {
                    // Create session
                    sessionManager.createLoginSession(loggedInUser.username, loggedInUser.authenticateLevel);

                    // Update MainActivity static fields (consider if these are still needed
                    // if SessionManager is the source of truth)
                    MainActivity.Login = loggedInUser.username;
                    LevelUtils.Level = loggedInUser.authenticateLevel;

                    // Clear password field
                    binding.editPassword.setText("");

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && v.getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    // Inform MainActivity to update UI (like ActionBar title/subtitle)
                    mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
                    mCallback.onLoginSuccess(); // Notify MainActivity

                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_LoginFragment_to_MainContainerFragment);
                } else {
                    binding.textView3.setText(getString(R.string.login_error_message));
                }
            }
        });
    }

    @Override
    public void invalidate() {
        // Implement if needed
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Assuming messageManager interface is defined in MainActivity or a common place
    public interface messageManager {
        void fragment(ItemFragment fragment);
        int fragmentOrder(int order);
        void onLoginSuccess(); // New method
    }
}