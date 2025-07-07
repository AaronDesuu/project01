package com.blemeterkai.meterkai.bluetooth; // Or your chosen package

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// You'll need to define these or import them if they are in a constants file
// For example:
import static com.blemeterkai.meterkai.MainActivity.REQUEST_ENABLE_BT; // If defined in MainActivity
// import static com.blemeterkai.meterkai.YourAppConstants.DEVICE_NAME;
// import static com.blemeterkai.meterkai.YourAppConstants.TOAST;

public class BluetoothController {

    private static final String TAG = "BluetoothController";

    private final Context context;
    private final Activity activity; // Needed for things like starting activities for result (old way) or permission checks
    // Consider if only context is enough, or if specific callbacks to activity are needed.

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothPrintService mPrintService; // Assuming this is your existing service

    // LiveData to observe Bluetooth state from MainActivity or ViewModels
    private final MutableLiveData<Boolean> isBluetoothEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<BluetoothDevice> connectedDevice = new MutableLiveData<>(null);
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    // Add more LiveData for other states like scanning, errors, etc.

    // For handling enable Bluetooth intent result
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    // For handling connect device intent result (if you use DeviceListActivity)
    private ActivityResultLauncher<Intent> connectDeviceLauncher;


    // Constructor
    public BluetoothController(Activity activity, BluetoothPrintService printService) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mPrintService = printService; // Or initialize it here if appropriate
        initializeBluetooth();
    }

    // Setter for ActivityResultLaunchers (to be called from Activity's onCreate)
    public void setEnableBluetoothLauncher(ActivityResultLauncher<Intent> launcher) {
        this.enableBluetoothLauncher = launcher;
    }

    public void setConnectDeviceLauncher(ActivityResultLauncher<Intent> launcher) {
        this.connectDeviceLauncher = launcher;
    }

    private void initializeBluetooth() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "Bluetooth not supported on this device.");
                toastMessage.postValue("Bluetooth IC does not found!");
                // Consider a callback or other mechanism if the activity needs to finish
            } else {
                isBluetoothEnabled.postValue(bluetoothAdapter.isEnabled());
            }
        } else {
            Log.e(TAG, "BluetoothManager not available.");
            toastMessage.postValue("Bluetooth Manager not available!");
        }
    }

    // --- Public Methods to be called from MainActivity/ViewModel ---

    public boolean isBleSupported() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public LiveData<Boolean> getBluetoothEnabledState() {
        return isBluetoothEnabled;
    }

    public LiveData<BluetoothDevice> getConnectedDevice() {
        return connectedDevice;
    }

    public LiveData<String> getToastMessages() {
        return toastMessage;
    }


    public void checkAndRequestBluetoothEnable() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // This permission is for Android 12+ for operations like enabling BT.
                // The actual request for this permission should be handled separately.
                // For now, we assume it's granted or not needed for older OS.
                // If targeting S+ and this is missing, the intent might not work as expected or crash.
                Log.w(TAG, "BLUETOOTH_CONNECT permission not granted for enabling Bluetooth programmatically.");
                // Fallback to manual enabling or guide user. For now, try anyway.
            }
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (enableBluetoothLauncher != null) {
                enableBluetoothLauncher.launch(enableBtIntent);
            } else {
                Log.e(TAG, "EnableBluetoothLauncher not set in BluetoothController");
                toastMessage.postValue("Error: Bluetooth launcher not configured.");
            }
        } else if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            isBluetoothEnabled.postValue(true); // Already enabled
        }
    }

    // Call this from the ActivityResultCallback of enableBluetoothLauncher
    public void onBluetoothEnableResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            isBluetoothEnabled.postValue(true);
            if (mPrintService == null) { // Or however your mPrintService is initialized after BT is enabled
                // mPrintService = new BluetoothPrintService(mHandler); // mHandler needs to be passed or handled via callbacks/LiveData
                Log.d(TAG, "BluetoothPrintService needs to be initialized.");
                toastMessage.postValue("Bluetooth enabled. Printer service ready.");
            }
        } else {
            isBluetoothEnabled.postValue(false);
            toastMessage.postValue("Bluetooth not enabled by user.");
            // Decide if activity should finish, communicate this back via LiveData or a callback
        }
    }

    // Call this from the ActivityResultCallback of connectDeviceLauncher (if using DeviceListActivity)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDeviceConnectResult(int resultCode, Intent data, boolean secure) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String address = data.getExtras().getString("EXTRA_DEVICE_ADDRESS"); // Replace with your actual extra key
            if (address != null) {
                connectToDevice(address, secure);
            } else {
                toastMessage.postValue("No device address received.");
            }
        } else {
            toastMessage.postValue("Device connection cancelled or failed.");
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT) // For Android 12+
    public void connectToDevice(String address, boolean secure) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            toastMessage.postValue("Bluetooth is not enabled.");
            Log.w(TAG, "Attempted to connect while Bluetooth is not enabled or adapter is null.");
            return;
        }
        if (mPrintService == null) {
            toastMessage.postValue("Print service not available.");
            Log.e(TAG, "mPrintService is null, cannot connect.");
            return;
        }

        // BLUETOOTH_CONNECT permission check (for Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                toastMessage.postValue("Bluetooth connect permission missing.");
                Log.e(TAG, "BLUETOOTH_CONNECT permission not granted.");
                // You need to request this permission from the Activity.
                // This controller can't directly request it. It can notify the Activity.
                return;
            }
        }


        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            mPrintService.connect(device, secure);

            connectedDevice.postValue(device);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid Bluetooth address: " + address, e);
            toastMessage.postValue("Invalid Bluetooth address.");
        }
    }


    // --- GATT Receiver (Placeholder - adapt to your mGattUpdateReceiver) ---
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Process GATT actions and update LiveData
            // Example:
            // if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            //     isConnected.postValue(true);
            // } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            //     isConnected.postValue(false);
            //     connectedDevice.postValue(null);
            // } // ... other actions
            Log.d(TAG, "GATT Update Received: " + action);
            // This part needs to be adapted based on YOUR actual BroadcastReceiver logic
            // and how you want to expose that data (e.g., through LiveData).
        }
    };

    public void registerGattUpdateReceiver() {
        // Adapt makeGattUpdateIntentFilter() from your MainActivity
        // final IntentFilter intentFilter = makeGattUpdateIntentFilter();
        // context.registerReceiver(mGattUpdateReceiver, intentFilter);
        Log.d(TAG, "GATT Update Receiver registered (placeholder - implement properly)");
    }

    public void unregisterGattUpdateReceiver() {
        try {
            context.unregisterReceiver(mGattUpdateReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "GATT Update Receiver not registered or already unregistered.");
        }
    }

    // --- Cleanup ---
    public void cleanup() {
        unregisterGattUpdateReceiver();
        // Any other cleanup for mPrintService if needed
        Log.d(TAG, "BluetoothController cleaned up.");
    }

    // You'll need to transfer your makeGattUpdateIntentFilter() from MainActivity here,
    // or make it accessible.
    // private static IntentFilter makeGattUpdateIntentFilter() {
    //    final IntentFilter intentFilter = new IntentFilter();
    //    intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
    //    // ... add other actions
    //    return intentFilter;
    // }
}