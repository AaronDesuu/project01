package com.blemeterkai.meterkai.ui.main.meter;

import static com.blemeterkai.meterkai.MainActivity.folderExternal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.blemeterkai.meterkai.MainActivity;
import com.blemeterkai.meterkai.ui.main.item.ItemFragment;
import com.blemeterkai.meterkai.printing.PrintData;
import com.blemeterkai.meterkai.R;
import com.blemeterkai.meterkai.adapters.SampleListAdapter;
import com.blemeterkai.meterkai.ui.main.item.SampleListItem;
import com.blemeterkai.meterkai.data.parser.CSVParser;
import com.blemeterkai.meterkai.databinding.FragmentFirstBinding;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blemeterkai.meterkai.viewmodel.FileUploadViewModel; // Import your Java ViewModel

import java.util.ArrayList;

public class FirstFragment extends ItemFragment {

    public FragmentFirstBinding binding;
    private AlertDialog.Builder builder;
    private int mPosition;
    private int mState;
    private int mRecordCount;
    private boolean abort = false;
    public static ArrayList<PrintData> mPrintData = new ArrayList<PrintData>();
    PrintData printData;
    private final Handler batchHandler = new Handler(); // Make it a member
    private Runnable batchRunnable;
    private FileUploadViewModel fileUploadViewModel;

    private Toolbar toolbar;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "- onAttach.");
        Activity a;
        if (context instanceof Activity){ // Check if context is an Activity
            a = (Activity) context;
            if (!(a instanceof messageManager)) {
                throw new ClassCastException(a.toString() + " must implement FirstFragment.messageManager");
            }
            mCallback = (messageManager) a;
        } else {
            // Handle case where context is not an Activity, though for Fragments it usually is.
            // Or if your callback is always the activity:
            if (!(getActivity() instanceof messageManager)) {
                throw new ClassCastException(getActivity().toString() + " must implement FirstFragment.messageManager");
            }
            mCallback = (messageManager) getActivity();
        }
        builder = new AlertDialog.Builder(context); // If still needed
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel scoped to the Activity
        fileUploadViewModel = new ViewModelProvider(requireActivity()).get(FileUploadViewModel.class);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i(TAG, TAG + "- onCreateView.");
        mCallback.fragment(this);
//        if (MainActivity.mfirstKey != null) {
//            MainActivity.mActionBar.setTitle(MainActivity.mfirstName);
//        }
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, TAG + "- onViewCreated in FirstFragment");
        // Your existing onViewCreated setup:
        MainActivity.mFragmentid = 2; // Or appropriate ID for FirstFragment
        if (mCallback != null) {
            mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
            mCallback.fragmentOrder(MainActivity.ODR_SCAN_ON);
        }
        mPosition = -1; // Your existing initializations
        mState = -1;
        stopper = true; // Assuming stopper is a member variable

        fileUploadViewModel.getNewFileUploaded().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String fileName) {
                if (fileName != null) {
                    Log.i(TAG, "New file uploaded signal received: " + fileName);
                    if (getContext() != null) { // Check context before showing toast
                        Toast.makeText(getContext(), "New file detected: " + fileName + ", refreshing...", Toast.LENGTH_SHORT).show();
                    }
                    refreshFirstFragmentData();
                    // Optional: Clear the signal in the ViewModel so this doesn't re-trigger on config change
                    // without a new actual upload.
                    fileUploadViewModel.clearNewFileUploadedSignal();
                }
            }
        });

        // Setup Toolbar
        toolbar = binding.toolbarFirstFragment;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        NavController navController = NavHostFragment.findNavController(this);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        loadInitialData();
    }

    private void loadInitialData() {
        Log.d(TAG, "Loading initial data for FirstFragment.");
        String csvfile = ""; // Initialize to prevent potential later null issues if try block fails early
        try {
            // It's good practice to ensure MainActivity.d is not null either
            if (MainActivity.d == null) {
                Log.e(TAG, "MainActivity.d is null in loadInitialData. Cannot generate csvfile name.");
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: App data initialization failed (d is null).", Toast.LENGTH_LONG).show();
                }
                return; // Can't proceed
            }
            csvfile = MainActivity.d.CurrentYearMonth() + "_meter.csv";

            if (MainActivity.secondcsv != null) {
                String presentFile = MainActivity.secondcsv.Present();
                if (presentFile != null) { // Explicit null check for the result of Present()
                    if (!presentFile.equals(csvfile)) {
                        MainActivity.secondcsv.readFile(csvfile);
                        MainActivity.Selection = 0;
                    } else {
                        MainActivity.secondcsv.Reset();
                    }
                } else {
                    // Handle the case where Present() returns null, e.g., assume file doesn't match or needs reading
                    Log.w(TAG, "MainActivity.secondcsv.Present() returned null. Assuming file needs to be read or is new.");
                    MainActivity.secondcsv.readFile(csvfile); // Or other appropriate logic
                    MainActivity.Selection = 0;
                }
            } else {
                MainActivity.secondcsv = new CSVParser(folderExternal);
                if (MainActivity.secondcsv.exist(csvfile)) {
                    MainActivity.secondcsv.readFile(csvfile);
                } else {
                    CSVParser csv = new CSVParser("meter.csv", folderExternal);
                    if (csv.size() > 0) {
                        MainActivity.secondcsv.Copy(csv, csvfile, folderExternal);
                        MainActivity.secondcsv.writeFile();
                    }
                }
                MainActivity.Selection = 0;
            }
            updateList();

            // Load oldcsv if needed
            // Add similar null check for MainActivity.d if AnyYearMonth uses it
            if (MainActivity.d == null) {
                Log.e(TAG, "MainActivity.d is null before loading oldcsv.");
                // Optionally show a toast here too, or handle as part of the initial error.
                return;
            }
            String oldfile = MainActivity.d.AnyYearMonth(0, -1) + "_meter.csv";
            MainActivity.oldcsv = new CSVParser(folderExternal);
            if (!MainActivity.oldcsv.exist(oldfile)) {
                MainActivity.oldcsv.readFile("registration.csv");
            } else {
                MainActivity.oldcsv.readFile(oldfile);
            }

        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException during loadInitialData: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading initial data (NPE). File: " + csvfile, Toast.LENGTH_LONG).show();
            }
            // Decide how to proceed. Maybe try to load default data or show an error state.
            // For now, it will just log and toast, and might leave the list empty or in an inconsistent state.
        } catch (Exception e) { // Catch other potential exceptions during file operations
            Log.e(TAG, "Exception during loadInitialData: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading initial data. File: " + csvfile, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void refreshFirstFragmentData() {
        Log.d(TAG, "Refreshing FirstFragment data due to new upload.");
        if (getContext() == null || !isAdded()) { // Important check
            Log.w(TAG, "Fragment not attached, cannot refresh data.");
            return;
        }

        // 1. Clear existing data if necessary
        if (MainActivity.mListItems != null) {
            MainActivity.mListItems.clear();
        }
        // If your adapter is directly bound to mListItems, clearing mListItems
        // and then calling notifyDataSetChanged (or re-setting the adapter) is key.

        // 2. Re-initialize CSVParsers or tell them to re-read the files.
        //    The logic here should be similar to loadInitialData() for relevant files.
        String csvfile = MainActivity.d.CurrentYearMonth() + "_meter.csv";
        if (MainActivity.secondcsv == null) { // Defensive: ensure it's initialized
            MainActivity.secondcsv = new CSVParser(folderExternal);
        }
        // Force re-read the potentially updated file
        if (MainActivity.secondcsv.exist(csvfile)) {
            MainActivity.secondcsv.readFile(csvfile);
        } else {
            // Handle case where the expected file might not exist after upload (e.g., if naming changed)
            // Or rely on the copy logic if that's intended
            Log.w(TAG, "Expected CSV file not found after upload: " + csvfile);
            // Potentially re-run the copy logic from loadInitialData if applicable
            CSVParser csv = new CSVParser("meter.csv", folderExternal);
            if (csv.size() > 0) {
                MainActivity.secondcsv.Copy(csv, csvfile, folderExternal);
                MainActivity.secondcsv.writeFile();
            }
        }
        MainActivity.Selection = 0; // Reset selection

        // Re-load oldcsv if it could have been affected or needs to be re-parsed
        String oldfile = MainActivity.d.AnyYearMonth(0, -1) + "_meter.csv";
        if (MainActivity.oldcsv == null) { // Defensive
            MainActivity.oldcsv = new CSVParser(folderExternal);
        }
        if (!MainActivity.oldcsv.exist(oldfile)) {
            MainActivity.oldcsv.readFile("registration.csv");
        } else {
            MainActivity.oldcsv.readFile(oldfile);
        }


        // 3. Call your updateList() method (or equivalent) to repopulate the UI
        updateList(); // This method should use the updated MainActivity.secondcsv

        // 4. Ensure the adapter is notified if updateList() doesn't re-create it.
        //    If updateList() creates a new adapter and sets it, this might not be needed.
        //    Otherwise:
        if (binding != null && binding.secondlist != null && binding.secondlist.getAdapter() != null) {
            ((SampleListAdapter) binding.secondlist.getAdapter()).notifyDataSetChanged();
        } else {
            Log.w(TAG, "Adapter or ListView not available for refresh notification.");
        }
    }

    private void updateList() {
        if (getContext() == null || !isAdded() || binding == null) { // Add checks
            Log.w(TAG, "Cannot update list, fragment not ready or binding is null.");
            return;
        }
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "updateList: Fragment not attached or context is null.");
            return;
        }


        int i = 0, activate;
        int color, current;
        String UID, now, mac, date, serial;

        current = mPosition;
        if (current < 0) {
            if (binding.secondlist.getCount() > 0) {
                if (MainActivity.Selection != binding.secondlist.getFirstVisiblePosition()) {
                    MainActivity.Selection = binding.secondlist.getFirstVisiblePosition();
                    return;
                }
            }
            Log.i(TAG, String.format("First position %d", MainActivity.Selection));
            while (true) {
                UID = MainActivity.secondcsv.Row(getString(R.string.table2_key));
                if (UID == null) {
                    break;
                }
                if (UID.isEmpty()) {
                    break;
                }
                activate = Integer.parseInt(MainActivity.secondcsv.Column(getString(R.string.table2_col1)));
                serial = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                now = MainActivity.secondcsv.Column(getString(R.string.table2_col5));
                date = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
                if (activate == 0) {
                    color = Color.LTGRAY;
                    now = "------";
                } else {
                    if (now.isEmpty()) {
                        color = Color.RED;
                        now = "------";
                    } else {
                        color = Color.BLUE;
                        now = String.format("%010.3f", Float.parseFloat(now));
                    }
                }
                String b = String.format("%s:%s\n%s:%s",
                        getString(R.string.table2_col5), now,
                        getString(R.string.table2_col11), date);
                if (MainActivity.mListItems.size() <= i) {
                    mac = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                    Integer position = mCallback.Position(mac);
                    String a = String.format("%s(rssi:%d)", serial, mCallback.Rssi(position));
                    SampleListItem item;
                    item = new SampleListItem(
                            a,
                            b,
                            position.toString(),
                            color);
                    MainActivity.mListItems.add(item);
                } else {
                    Integer position = Integer.parseInt(MainActivity.mListItems.get(i).getKey());
                    if (position < 0) {
                        mac = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                        position = mCallback.Position(mac);
                        MainActivity.mListItems.get(i).setKey(position.toString());
                    }
                    String a = String.format("%s(rssi:%d)", serial, mCallback.Rssi(position));
                    MainActivity.mListItems.get(i).setTitle(a);
                    MainActivity.mListItems.get(i).setContents(b);
                    MainActivity.mListItems.get(i).setColor(color);
                }
                i++;
            }
            binding.secondlist.setOnItemClickListener(onItemClickListener);
        } else {
            if (current < MainActivity.mListItems.size()) {
                serial = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                now = MainActivity.secondcsv.Cell(current, getString(R.string.table2_col5));
                date = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
                if (now.isEmpty()) {
                    if (mState > 0) {
                        color = Color.MAGENTA;
                        now = "Reading from meter...";
                    } else {
                        color = Color.RED;
                        now = "------";
                    }
                } else {
                    color = Color.BLUE;
                    now = String.format("%010.3f", Float.parseFloat(now));
                }
                Integer position = Integer.parseInt(MainActivity.mListItems.get(current).getKey());
                String a = String.format("%s(rssi:%d)", serial, mCallback.Rssi(position));
                String b = String.format("%s:%s\n%s:%s",
                        getString(R.string.table2_col5), now,
                        getString(R.string.table2_col11), date);
                MainActivity.mListItems.get(current).setColor(color);
                MainActivity.mListItems.get(current).setTitle(a);
                MainActivity.mListItems.get(current).setContents(b);
            }
        }
        // レイアウトからリストビューを取得
        SampleListAdapter adapter = new SampleListAdapter(getActivity(), R.layout.custom_list, MainActivity.mListItems);
        binding.secondlist.setAdapter(adapter);
        Log.i(TAG, String.format("Selection %d", MainActivity.Selection));
        binding.secondlist.setSelection(MainActivity.Selection);
        binding.secondlist.invalidate();
    }


    @Override
    public void invalidate() {
        updateList();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.Selection = position;
            Log.i(TAG, "Second to Forth");
            MainActivity.rootcsv.Cell(position, getString(R.string.table2_key));
            MainActivity.msecondKey = MainActivity.secondcsv.Cell(position, getString(R.string.table2_key));
            MainActivity.mSerialID = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
            MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
            MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.mSerialID);
            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_meterFirstFragment_to_meterSecondFragment);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // fileUploadViewModel.getNewFileUploaded().removeObservers(getViewLifecycleOwner()); // Not strictly needed with getViewLifecycleOwner
        binding = null; // Good practice
        Log.d(TAG, TAG + " - onDestroyView");
    }

    // Your stopper variable needs to be declared as a member of FirstFragment
    private boolean stopper = true; // Example declaration

    @Override
    public void batch() {
        if (stopper) {
            binding.secondlist.setOnItemClickListener(null);
            // final Handler handler = new Handler(); // This was correctly commented out

            batchRunnable = new Runnable() { // Assign to member runnable
                @Override
                public void run() {
                    if (!isAdded() || getContext() == null) { // Crucial check
                        Log.w(TAG, "Batch runnable: Fragment not attached.");
                        // Clean up if necessary
                        mState = -1;
                        mPosition = -1;
                        stopper = true;
                        // Consider removing callbacks if the handler is still active
                        // batchHandler.removeCallbacks(this); // Optional: if you want to stop further execution here
                        return;
                    }

                    int ret = 0; // Declare ret here or just before its first use in case 1

                    switch (mState) {
                        case -1:
                            mCallback.showToast("Start!");
                            mPosition = -1;
                            batchHandler.postDelayed(this, MainActivity.mTick); // Use member handler
                            mState++;
                            mPrintData.clear();
                            break;
                        case 0:
                            while (!abort) {
                                if (++mPosition < MainActivity.secondcsv.size()) {
                                    if (mPosition > 3) {
                                        MainActivity.Selection = mPosition - 3;
                                    } else {
                                        MainActivity.Selection = 0;
                                    }
                                    // Ensure context is available before getString
                                    if (!isAdded() || getContext() == null) {
                                        Log.w(TAG, "Batch runnable case 0: Fragment not attached.");
                                        stopper = true; // Or handle error appropriately
                                        return;
                                    }
                                    MainActivity.msecondKey = MainActivity.secondcsv.Cell(mPosition, getString(R.string.table2_key));
                                    int activate = 0;
                                    if (MainActivity.msecondKey != null) {
                                        if (!MainActivity.msecondKey.isEmpty()) {
                                            activate = Integer.parseInt(MainActivity.secondcsv.Column(getString(R.string.table2_col1)));
                                        }
                                    }
                                    if (activate > 0) {
                                        printData = new PrintData();
                                        MainActivity.oldcsv.Find(getString(R.string.table2_key), MainActivity.msecondKey);
                                        String sid = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                                        String val = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
                                        MainActivity.mSerialID = sid;

                                        printData.old_value[0] = MainActivity.oldcsv.Column(getString(R.string.table2_col4));/*fix date*/
                                        printData.old_value[1] = MainActivity.oldcsv.Column(getString(R.string.table2_col5));/*Imp*/
                                        printData.old_value[2] = MainActivity.mSerialID;
                                        if (val.isEmpty()) {
                                            Log.i(TAG, String.format("Batch Check %d", mPosition));
                                            MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                                            MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.mSerialID);
                                            batchHandler.postDelayed(this, MainActivity.mTick); // Use member handler
                                            mState++;
                                            break;
                                        } else {
//                                          printData.now_value[0] = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
//                                          printData.now_value[1] = MainActivity.secondcsv.Column(getString(R.string.table2_col4));
//                                          printData.now_value[2] = MainActivity.secondcsv.Column(getString(R.string.table2_col5));
//                                          printData.now_value[3] = MainActivity.secondcsv.Column(getString(R.string.table2_col7));
//                                          mPrintData.add(printData);
                                            Log.i(TAG, String.format("Batch Skip %d", mPosition));
                                        }
                                    } else {
                                        Log.i(TAG, String.format("Batch Skip %d", mPosition));
                                    }
                                } else {
                                    abort = true;
                                }
                            }
                            if (abort) {
                                MainActivity.secondcsv.writeFile();
                                if (mPosition < MainActivity.secondcsv.size()) {
                                    mCallback.showToast("Batch Abort!");
                                    Log.i(TAG, "Batch abort");
                                } else {
                                    mCallback.showToast("Batch Finish!");
                                    Log.i(TAG, "Batch Finish");
                                    for (int i = mPrintData.size(); i > 0; ) {
                                        i--;
                                        mCallback.OutputBillingData(mPrintData.get(i).now_value, mPrintData.get(i).old_value);
                                    }
                                }
                                mState = -1;
                                mPosition = -1;
                                mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
                                mCallback.fragmentOrder(MainActivity.ODR_SCAN_ON);
                                mCallback.fragmentMessage(-1);
                                stopper = true;
                            }
                            break;
                        case 1:
                            // Declare 'ret' here as it's first assigned and used in this scope
                            ret = mCallback.fragmentMessage(MainActivity.MSG_READER);
                            if (stopper) {
                                mCallback.fragmentOrder(MainActivity.ODR_DISCONNECT);
                                ret = -3;
                            }
                            switch (ret) {
                                case 1:
                                case 2:
                                case 5:
                                    batchHandler.postDelayed(this, MainActivity.mTick); // Use member handler
                                    break;
                                case 0:
                                    mState = 0;
                                    mCallback.showToast(MainActivity.mSerialID + " finish to read");
                                    updateList(); // Ensure updateList also checks isAdded()
                                    batchHandler.postDelayed(this, 1000); // Use member handler
                                    break;
                                case -6: /*Skip*/
                                case -50:
                                case -99:
                                    mState = 0;
                                    mCallback.showToast(String.format("Batch next %d", ret));
                                    Log.i(TAG, String.format("Batch next %d", ret));
                                    updateList(); // Ensure updateList also checks isAdded()
                                    batchHandler.postDelayed(this, MainActivity.mTick); // Use member handler
                                    break;
                                case -1:
                                case -2:
                                case -3:
                                case -5: /*abort*/
                                default:
                                    mState = 0;
                                    mCallback.showToast(String.format("Detect error %d", ret));
                                    batchHandler.postDelayed(this, MainActivity.mTick); // Use member handler
                                    abort = true;
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            mCallback.fragmentOrder(MainActivity.ODR_SCAN_OFF);
            mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
            abort = false;
            stopper = false;
            batchHandler.post(batchRunnable); // Use member handler, and use batchRunnable
        }
    }

    @Override
    public int DataArrived(final ArrayList<String> in, final boolean last) {
        int ret = 0;
        if (stopper) {
            return ret;
        }
        super.DataArrived(in, last);
        if (last) {
            switch (MainActivity.mSubStage) {
                case 2:
                    if (mTemp.size() > 1) {
                        if (!mTemp.get(1).equals("success (0)")) {
                            ret = -5;
                        }
                    } else {
                        ret = -5;
                    }
                    break;
                case 4:
                    if (mTemp.size() > 1) {
                        mRecordCount = Integer.parseInt(mTemp.get(1));
                        MainActivity.CounterParameter.setLength(0);
                        MainActivity.CounterParameter.append(String.format("020406%08x06%08x120001120000", mRecordCount, mRecordCount));
                    } else {
                        ret = -5;
                    }
                    break;
                case 6:
                    if (mTemp.size() > 9) {
                        MainActivity.secondcsv.Update(mTemp.get(1), getString(R.string.table2_col4));
                        printData.now_value[2] = String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(2)));
                        printData.now_value[3] = String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(6)));
                        MainActivity.secondcsv.Update(printData.now_value[2], getString(R.string.table2_col5));
                        MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(3))), getString(R.string.table2_col6));
                        MainActivity.secondcsv.Update(printData.now_value[3], getString(R.string.table2_col7));
                        MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(7))), getString(R.string.table2_col8));
                        MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(100.0, mTemp.get(8))), getString(R.string.table2_col9));
                        MainActivity.secondcsv.Update(mTemp.get(9), getString(R.string.table2_col10));
                        MainActivity.secondcsv.Update(mTemp.get(0), getString(R.string.table2_col11));
                        printData.now_value[0] = mTemp.get(0);  /*read date*/
                        printData.now_value[1] = mTemp.get(1);  /*fixed date*/
                        mPrintData.add(printData);
                    } else {
                        ret = -5;
                    }
                    break;
            }
            mTemp.clear();
        }
        return ret;
    }


}