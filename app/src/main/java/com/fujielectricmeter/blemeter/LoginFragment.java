package com.fujielectricmeter.blemeter;

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
import com.fujielectricmeter.blemeter.databinding.FragmentLoginBinding;

import java.io.File;

public class LoginFragment extends ItemFragment {

    private final static String TAG = FourthFragment.class.getSimpleName();
    private FragmentLoginBinding binding;
    private messageManager mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach.");
        Activity a = getActivity();
        if (a instanceof messageManager == false) {
            throw new ClassCastException("Activity have to implement FourthFragment.messageManager");
        }
        mCallback = (messageManager) a;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mCallback.fragment(this);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.textView3.setText("");
        binding.editAccount.setText("");
        MainActivity.login = new CSVParser(MainActivity.folderFiles);
        if(!MainActivity.login.readFile("login.csv")){
            MainActivity.writeFile(
                    getString(R.string.login)+","+getString(R.string.password)+","+getString(R.string.authenticate),
                    "login.csv",
                    MainActivity.folderFiles);
            MainActivity.login.readFile("login.csv");
            MainActivity.login.Add("Super,BleMeter,0");
            MainActivity.login.Add("Admin,Admin,1");
            MainActivity.login.Add("Reader,Reader,3");
            MainActivity.login.writeFile();
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.Login = null;
        MainActivity.Level = null;
        MainActivity.mFragmentid = 0;
        mCallback.fragmentOrder(MainActivity.ODR_UPDATE);

        binding.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean find = false;
                while (true) {
                    String account = MainActivity.login.Row( getString(R.string.login));
                    if (account == null) {
                        break;
                    }
                    if (!find) {
                        if (account.equals(binding.editAccount.getText().toString())) {
                            String password = MainActivity.login.Column(getString(R.string.password));
                            if (password != null) {
                                if (password.equals(binding.editPassword.getText().toString())) {
                                    binding.editPassword.setText("");
                                    find = true;
                                    MainActivity.Login = account;
                                    MainActivity.Level = MainActivity.login.Column(getString(R.string.authenticate));
                                    break;
                                }
                            } else {
                                MainActivity.Login = account;
                                MainActivity.Level = "3";
                            }
                        }
                    }
                }
                if (find) {
                    setAnime(binding.button);
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_LoginFragment_to_SecondFragment);
                } else {
                    binding.textView3.setText("Please input correct user or password");
                }
            }
        });
    }
    @Override
    public void invalidate() {
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}