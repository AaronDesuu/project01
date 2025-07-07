package com.blemeterkai.meterkai;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.blemeterkai.meterkai.utils.LevelUtils.Level;
import static com.blemeterkai.meterkai.utils.LevelUtils.getLevel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
//import androidx.navigation.ui.NavigationUI;

import com.blemeterkai.meterkai.auth.SessionManager;
import com.blemeterkai.meterkai.bluetooth.BluetoothLeService;
import com.blemeterkai.meterkai.bluetooth.BluetoothPrintService;
import com.blemeterkai.meterkai.bluetooth.DeviceList;
import com.blemeterkai.meterkai.data.parser.CSVParser;
import com.blemeterkai.meterkai.databinding.ActivityMainBinding;
import com.blemeterkai.meterkai.dlms.DLMS;
import com.blemeterkai.meterkai.ui.login.LoginFragment;
import com.blemeterkai.meterkai.ui.main.item.ItemFragment;
import com.blemeterkai.meterkai.ui.main.item.SampleListItem;
import com.blemeterkai.meterkai.utils.AppHandler;
import com.blemeterkai.meterkai.utils.AssetUtils;
import com.blemeterkai.meterkai.utils.FileUtils;
import com.blemeterkai.meterkai.utils.Trail;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.woosim.printer.WoosimService;

import com.blemeterkai.meterkai.data.model.BillingProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

// enum imports


public class MainActivity extends AppCompatActivity implements
        ItemFragment.messageManager, LoginFragment.messageManager {

    public static final String DEVICE_NAME = null;
    public static final int MESSAGE_DEVICE_NAME = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_READ = 3;
    public final static int ODR_SCAN_ON = 0;
    public final static int ODR_SCAN_OFF = (ODR_SCAN_ON + 1);
    public final static int ODR_SCAN_RESET = (ODR_SCAN_OFF + 1);
    public final static int ODR_UPDATE = (ODR_SCAN_RESET + 1);
    public final static int ODR_LIST_CLEAR = (ODR_UPDATE + 1);
    public final static int ODR_RELEASE = (ODR_LIST_CLEAR + 1);
    public final static int ODR_DISCONNECT = (ODR_RELEASE + 1);
    public final static int MSG_CONNECT = 0;
    public final static int MSG_STATE_CHECKER = MSG_CONNECT + 1;
    public final static int MSG_SETUP = MSG_STATE_CHECKER + 1;
    public final static int MSG_CHECKER = MSG_SETUP + 1;
    public final static int MSG_READER = MSG_CHECKER + 1;
    public final static int MSG_TESTER = MSG_READER + 1;
    public final static int MSG_INSTANT = MSG_TESTER + 1;
    public final static int MSG_MEASURE1 = MSG_INSTANT + 1;
    public final static int MSG_MEASURE2 = MSG_MEASURE1 + 1;
    public final static int MSG_MEASURE3 = MSG_MEASURE2 + 1;
    public final static int MSG_SET_CLOCK = MSG_MEASURE3 + 1;
    public final static int MSG_EVENT_RECORD = MSG_SET_CLOCK + 1;
    public final static int MSG_ENERGY_RECORD = MSG_EVENT_RECORD + 1;
    public final static int MSG_BILLING_RECORD = MSG_ENERGY_RECORD + 1;
    public final static int MSG_BREAKER = MSG_BILLING_RECORD + 1;
    public final static int MSG_ALERT_CLEAR = MSG_BREAKER + 1;
    public final static int MSG_CHANGE_THRESH = MSG_ALERT_CLEAR + 1;
    public static final int REQUEST_ENABLE_BT = 1;
    private static final boolean D = true;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int timeout = 60;
    public static float[] ratio = {
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    public static StringBuffer CounterParameter = new StringBuffer();
//    public static ActionBar mActionBar;
    public static int mScanTick = 2000;
    public static int mTick = 200;
    public static int mInterval = 0;
    public static byte mAttr, mMode, mSel;
    public static String mConnect;
    public static String Login;
    public static String mfirstName;
    public static String mfirstKey;
    public static String msecondName;
    public static String msecondKey;
    public static int Selection;
    public static String mSerialID;
    public static String mAddress;
    public static int mFragmentid;
    public static int mSubStage;
    public static DLMS d;
    public static boolean mScanning;
    public static ArrayList<String> mTemp = new ArrayList<String>();
    public static File folderFiles;
    public static File folderCache;
    public static File folderExternal;
    public static File folderDownload;
    public static File folderDocument;
    public static CSVParser login;
    public static CSVParser rootcsv = null;
    public static CSVParser firstcsv;
    public static CSVParser secondcsv;
    public static CSVParser ratecsv = null;
    public static CSVParser oldcsv = null;
    public static CSVParser printercsv = null;
    public static CSVParser fourthcsv;
    public static Trail trail;
    public static ArrayList<SampleListItem> mListItems = new ArrayList<>();
    public static BluetoothPrintService mPrintService = null;
    static String root_column;
    private static int mPosition;
    private static final String TAG = MainActivity.class.getSimpleName();
    // The handler that gets information back from the BluetoothPrintService
    private final AppHandler mHandler = new AppHandler(this);
    private final StringBuffer mParameter = new StringBuffer();
    private final DeviceList mDeviceTemp = new DeviceList();
    private final DeviceList mDeviceList = new DeviceList();
    // Device scan callback.
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
                        @Override
                        public void run() {
                            String name = device.getName();
                            if (name != null) {
                                if (name.contains("F5")) {
                                    mDeviceList.addDevice(device, rssi);
                                    Log.i(TAG, String.format("ScanLeDevice %s", device.getAddress()));
                                }
                            }
                        }
                    });
                }
            };
    private SessionManager sessionManager;
    private NavController navController;

    //  public static String[] now_value = {"", "", "", ""};
    //  public static String[]old_value = {"", ""};
    private WoosimService mWoosim = null;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemFragment mItemFragment;
    private int mCurrentMessage;
    // added NavControl and BottomNav
    private int mRetry;
    private ArrayList<String> mReceive;
    private String mAddressShort = null;
    private byte mDataIndex;
    private String mPassword;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private int mArrived;
    private byte[] mData;
    private int mTimer;
    private int mCount;
    private int mTotal;
    private int mStage;
    private int mStep;
    private int mPrmState;
    private boolean mServiceActive;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder write_read) {
            Log.i(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) write_read).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.i(TAG, "Fail to initialize of Bluetooth service.");
            } else {
                Log.i(TAG, "Success to initialize of Bluetooth service.");
                mServiceActive = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, " onServiceDisconnected");
        }
    };
    private boolean mBinding;
    private int mScan;
    private int mConnected;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = 1;
                Log.i(TAG, " ACTION_GATT_CONNECTED");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, " ACTION_GATT_DISCONNECTED");
                mConnected = -1;
                if (mStage != 0) {
                    Log.i(TAG, "Restart gatt service");
                    mStage = 0;
                    mStep = 0;
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, " ACTION_GATT_SERVICES_DISCOVERED");
                mConnected = 2;
                mArrived = 0;
            } else if (BluetoothLeService.ACTION_GATT_ERROR.equals(action)) {
                Log.i(TAG, " ACTION_GATT_ERROR");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i(TAG, " ACTION_DATA_AVAILABLE");
                mData = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                mArrived++;
            }
        }
    };
    private boolean mkeep = false;
    private boolean mPermission = false;
    private String Tag;
    private BottomNavigationView bottomNavView;
    private boolean mProgressing = false;

    public static String SerialID() {
        if (mSerialID != null) {
            return String.format("%s, %s", mSerialID, mAddress);
        } else {
            return null;
        }
    }


    // Handles various events fired by the write_read.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private static float trimFloat(final float in) {
        String str = String.format("%.02f", in);
        return Float.parseFloat(str);
    }

    private void OutputBillingData(final String[] now_value, final String[] old_value, final boolean withPrinting) {
        BillingProcess.processBillingData(now_value, old_value, withPrinting, this.ratio, this.d, this.folderExternal);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_ERROR);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
//        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        return intentFilter;
    }

    public int Rssi(final int position) {
        return mDeviceList.Rssi(position);
    }

    public int Position(final String address) {
        return mDeviceList.Position(address);
    }



    public void OutputBillingData(final String[] now_value, final String[] old_value) {

        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Printing confirmation");
        builder.setMessage("Serial ID: " + old_value[2] + "\nBilling data will be saved to JSON.\nWould you like to print receipt?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OutputBillingData(now_value, old_value, true);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OutputBillingData(now_value, old_value, false);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        builder.show();
    }

    private boolean copyAssetsFile() {
        try {
            InputStream inputStream = getAssets().open("logo3.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(new File(folderFiles + "/logo3.jpg"), false);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) >= 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            // 何かテキトーに
            return false;
        }
        return true;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void Disconnect(final boolean all) {
        Log.i(TAG, "Disconnect()");
        if (mConnected > 0) {
            Log.i(TAG, "mBluetoothLeService.disconnect()");
            mBluetoothLeService.disconnect();
        }
        if (mServiceActive) {
            Log.i(TAG, "unbindService(mServiceConnection)");
            unbindService(mServiceConnection);
            mServiceActive = false;
        }
        if (all) {
            mStage = 0;
            mSubStage = 0;
            mStep = 0;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) ||
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            ) {
                // パーミッションの許可を取得する
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                mPermission = true;
            }
        } else {
            if (
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                // パーミッションの許可を取得する
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 1000);
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可を取得する
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,}, 1000);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, " onActivityResult()");
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            showToast("You choose disable bluetooth. Exit this app");
            return;
        }
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    mPrintService = new BluetoothPrintService(mHandler);
                } else {
                    if (D) {
                        int btIsNotEnabled = Log.e(Tag, "BT is not enabled");
                    }
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void connectDevice(Intent data, boolean secure) {

        String address = printercsv.Column(getString(R.string.table2_col3));
        // Get the device MAC address
        //if (data.getExtras() != null)
        //address = data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mPrintService.connect(device, secure);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        folderFiles = getFilesDir();
//      folderFiles = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        folderCache = getCacheDir();
        folderExternal = getExternalFilesDir(null);
        folderDocument = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
        folderDownload = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

        AssetUtils.copyAssetsToInternalStorage(this, "csv", "files");

        trail = new Trail();
        Log.i(TAG, "MainActivity - onCreate");

        if (d == null) {
            d = new DLMS(getApplicationContext());
        } else {
            Log.i(TAG, " reuse d.");
        }
        mTick = 100;

        checkPermission();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("PackageManager.FEATURE_BLUETOOTH_LE");
            finish();
            return;
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            showToast("Bluetooth IC does not found!");
            finish();
            return;
        }
        copyAssetsFile();
        CSVParser csv = new CSVParser(folderExternal);

        mAddressShort = "UnknownMeter";
        mInterval = 0;
        mCurrentMessage = -1;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.mainContainerFragment).build();

        //mActionBar = getSupportActionBar();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                mItemFragment.invalidate();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(r);


        // Initializes list view adapter.
        mServiceActive = false;
        mBinding = false;
        mConnected = 0;
        mStage = 0;
        mStep = 0;
        mPrmState = 0;
        mConnect = null;
        mScan = 1;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                //              redrawMenu();
                break;
            case MESSAGE_TOAST:
                //              Toast.makeText(getApplicationContext(), msg.getData().getInt(TOAST), Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_READ:
                mWoosim.processRcvData((byte[]) msg.obj, msg.arg1);
                break;
            case WoosimService.MESSAGE_PRINTER:
                if (msg.arg1 == WoosimService.MSR) {
                    if (msg.arg2 == 0) {
                        Toast.makeText(getApplicationContext(), "MSR reading failure", Toast.LENGTH_SHORT).show();
                    } else {
                        byte[][] track = (byte[][]) msg.obj;
                        if (track[0] != null) {
                            String str = new String(track[0]);
                            //                         mTrack1View.setText(str);
                        }
                        if (track[1] != null) {
                            String str = new String(track[1]);
                            //                         mTrack2View.setText(str);
                        }
                        if (track[2] != null) {
                            String str = new String(track[2]);
                            //                        mTrack3View.setText(str);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, " onStart.");

        if (!mBluetoothAdapter.isEnabled()) {
            // Request to enable bluetooth.
            // Bluetooth session will then be setup during onActivityResult
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission();
        Log.i(TAG, " onResume.");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //     registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        rootcsv = new CSVParser("meter.csv", folderExternal);
        printercsv = new CSVParser("printer.csv", folderExternal);
        ratecsv = new CSVParser("rate.csv", folderExternal);
        if (ratecsv.size() > 0) {
            int numberOfColumns = ratio.length; // Or a fixed number like 21 if it's always that many

            for (int i = 0; i < numberOfColumns; i++) {
                String resourceName = "table3_col" + (i + 1);

                int resourceId = getResources().getIdentifier(resourceName, "string", getPackageName());

                if (resourceId != 0) { // Check if the resource was found
                    String columnName = getString(resourceId);
                    String csvValue = ratecsv.Column(columnName);

                    if (csvValue != null && !csvValue.isEmpty()) {
                        try {
                            ratio[i] = Float.parseFloat(csvValue);
                        } catch (NumberFormatException e) {
                            // For example, set a default value or log an error
                            ratio[i] = 0.0f; // Default value
                            Log.e("MainActivity", "Error parsing float for column: " + columnName + ", value: " + csvValue, e);
                            // Consider showing a toast or a more user-friendly error
                        }
                    } else {
                        // Handle cases where the column is missing or empty in the CSV
                        ratio[i] = 0.0f; // Default value
                        Log.w("MainActivity", "Column not found or empty in CSV: " + columnName);
                        // Consider how critical missing data is.
                    }
                } else {
                    // This usually indicates a programming error (mismatch in naming or missing resource)
                    Log.e("MainActivity", "String resource not found: " + resourceName);
                    ratio[i] = 0.0f; // Default or error value
                    // This is a more critical error, you might want to stop processing or throw an exception.
                }
            }
        } else {
            Log.w("MainActivity", "ratecsv is empty. Ratios not loaded.");
            // Initialize 'ratio' array with default values if necessary
            // for (int i = 0; i < ratio.length; i++) {
            //     ratio[i] = 0.0f; // Default value
            // }
        }
        if (mBluetoothAdapter.isEnabled()) {
            if (mPrintService == null) {
                // Initialize the BluetoothPrintService to perform bluetooth connections
                mPrintService = new BluetoothPrintService(mHandler);
                mWoosim = new WoosimService(mHandler);
            } else {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mPrintService.getState() == BluetoothPrintService.STATE_NONE) {
                    // Start the Bluetooth print services
                    mPrintService.start();
                }
            }
            if (printercsv.size() > 0) {
                String address = printercsv.Column(getString(R.string.table2_col3));
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mPrintService.connect(device, false);
            }
        }
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
    }

    @Override
    protected void onPause() {
        if (mPrintService != null)
            mPrintService.stop();
        super.onPause();
        Log.i(TAG, " onPause");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, " onDestroy");
        super.onDestroy();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        boolean ret = true;
        int id;
        AlertDialog.Builder builder;
        View view;

        Log.i(TAG, " onOptionsItemSelected");
        id = item.getItemId();
        switch (id) {
            case R.id.menu_batch:
                mItemFragment.batch();
                break;
            case R.id.menu_stop:
                mItemFragment.interrupt();
                break;
            case R.id.menu_exit:
                finish();
                break;
            case R.id.menu_user:
                builder = new AlertDialog.Builder(this);
                view = View.inflate(builder.getContext(), R.layout.registration, null);
                EditText user = view.findViewById(R.id.edit_login);
                EditText pass = view.findViewById(R.id.edit_password);
                builder.setView(view);
                builder.setTitle("ユーザの追加/変更");
                builder.setMessage("新しいユーザーを追加します。既存ユーザーの場合は、パスワードを変更します。");
                builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("未実装！！！");
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
                break;
            case R.id.menu_password:
                builder = new AlertDialog.Builder(this);
                view = View.inflate(builder.getContext(), R.layout.password, null);
                EditText old_pass = view.findViewById(R.id.editpassword0);
                EditText new_pass = view.findViewById(R.id.editpassword1);
                builder.setView(view);
                builder.setTitle("パスワード変更");
                builder.setMessage("パスワードを変更します。");
                builder.setPositiveButton(getString(R.string.change), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean find = false;
                        boolean success = false;
                        while (!find) {
                            String account = MainActivity.login.Row(getString(R.string.login));
                            if (account == null) {
                                break;
                            }
                            if (account.equals(MainActivity.Login)) {
                                find = true;
                                String password = MainActivity.login.Column(getString(R.string.password));
                                if (password != null) {
                                    if (password.equals(old_pass.getText().toString())) {
                                        MainActivity.login.Update(new_pass.getText().toString(), getString(R.string.password));
                                        MainActivity.login.writeFile();
                                        success = true;
                                    }
                                }
                            }
                        }
                        if (success) {
                            showToast("成功しました。");
                        } else {
                            showToast("失敗しました。");
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
                break;
            case android.R.id.home://16908332
                switch (mFragmentid) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        break;
                }
                ret = super.onOptionsItemSelected(item);
                break;

            case R.id.menu_disconnect:
                Disconnect(true);
                ret = true;
                break;

            case R.id.menu_scan:
                Disconnect(true);
//              scanLeDevice();
                ret = true;
                break;
            case R.id.menu_load:
                break;
            case R.id.menu_save:
                String data, name;
                if (mFragmentid == 2) {
                    name = d.CurrentYearMonth() + "_meter";
                    data = d.CurrentYearMonth() + "_meter.csv";
                } else {
                    name = d.CurrentYearMonth() + "_" + mSerialID;
                    data = d.CurrentYearMonth() + "_" + mSerialID + ".json";
                }
                String read = FileUtils.readFile(data, folderExternal);
                if (!read.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_SUBJECT, name);
                    intent.putExtra(Intent.EXTRA_TEXT, read);
                    intent.setType("text/plain");
                    startActivity(intent);
                } else {
                    showToast("No File!!!");
                }
                ret = true;
                break;
            case R.id.menu_select:
                final Handler handler;
                final Runnable r;
                Disconnect(true);
                handler = new Handler();
                r = new Runnable() {
                    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    @Override
                    public void run() {
                        int ret = fragmentMessage(0);
                        if (ret > 0) {
                            handler.postDelayed(this, mTick);
                        }
                    }
                };
                handler.post(r);
                ret = true;
                break;
            case R.id.menu_share:
                if (mSerialID != null) {
                    try {
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Json file");
//                        shareIntent.setType("application/json");
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"<E-mail address>"});
                        File file = new File(folderExternal, d.CurrentYearMonth() + "_" + mSerialID + ".json");
                        if (file.exists()) {
                            Uri uri = FileProvider.getUriForFile(this, "com.fujielectricmeter.blemeter", file);
                            ArrayList<Uri> uris = new ArrayList<Uri>();
                            uris.add(uri);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(Intent.createChooser(shareIntent, "Email:").
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, " onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Default visibility states
        setMenuItemVisibility(menu, R.id.menu_disconnect, false);
        setMenuItemVisibility(menu, R.id.menu_scan, false);
        setMenuItemVisibility(menu, R.id.menu_select, false);
        setMenuItemVisibility(menu, R.id.menu_load, false);
        setMenuItemVisibility(menu, R.id.menu_save, true);
        setMenuItemVisibility(menu, R.id.menu_exit, true);

        // Conditional visibility
        setMenuItemVisibility(menu, R.id.menu_share, mFragmentid == 4);
        setMenuItemVisibility(menu, R.id.menu_batch, mFragmentid == 2);

        boolean isUserLevelLowEnough = (Level != null && Integer.parseInt(Level) <= 1);

        if (mFragmentid < 2) {
            setMenuItemVisibility(menu, R.id.menu_user, isUserLevelLowEnough);
            setMenuItemVisibility(menu, R.id.menu_password, Level != null); // Only visible if Level is not null
            setMenuItemVisibility(menu, R.id.menu_stop, false);
        } else {
            setMenuItemVisibility(menu, R.id.menu_user, false);
            setMenuItemVisibility(menu, R.id.menu_password, false);
            setMenuItemVisibility(menu, R.id.menu_stop, mItemFragment != null && mItemFragment.running());
        }
        // Example for the mServiceActive part:
        // setMenuItemVisibility(menu, R.id.menu_scan, true); // If this is always true now
        // setMenuItemVisibility(menu, R.id.menu_disconnect, mServiceActive);


        return true;
    }

    private void setMenuItemVisibility(Menu menu, int itemId, boolean visible) {
        MenuItem item = menu.findItem(itemId);
        if (item != null) {
            item.setVisible(visible);
        } else {
            Log.w(TAG, "Menu item with ID " + itemId + " not found.");
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public int Release() {
        int ret = 0;
        if (mTimer > timeout) {
            Log.i(TAG, "Release - Timeout");
            mTimer = 0;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Detect timeout");
            builder.setMessage("No data receive from meter\nPlease try again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Disconnect(true);
//                    scanLeDevice();
                }
            });
            builder.show();
        } else {
            switch (mStage) {
                case 2:
                    ret = sessionRelease();
                    if (ret == 2) {
                        mStage--;
                    }
                    break;
                case 1:
                    break;
                default:
                    if (ret == 0) {
                        mSubStage = 0;
                    }
                    break;
            }
        }
        return ret;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private int sessionRelease() {
        byte[] send = null;
        int ret;
        int[] res = new int[2];
        ret = 0; /*0:fail,1:processing,2:established*/

        switch (mStep) {
            case 0:
                Log.i(TAG, "Release session...");
                send = d.Release();
                if (send != null) {
                    mStep++;
                    mTimer = 0;
                    mArrived = 0;
                    mBluetoothLeService.write(send);
                    ret = 1;
                    Log.i(TAG, String.format("Release:%d", send.length));
                }
                break;
            case 2:
                send = d.Close(res, mData);
                if (res[0] != 0) {
                    if (send != null) {
                        Log.i(TAG, "Close connecting...");
                        mStep++;
                        mTimer = 0;
                        mArrived = 0;
                        mBluetoothLeService.write(send);
                        ret = 1;
                        Log.i(TAG, String.format("Close:%d", send.length));
                    }
                }
                break;
            case 4:
                d.Finish(res, mData);
                if (res[0] != 0) {
                    Log.i(TAG, "sessionRelease - Finish");
                } else {
                    Log.i(TAG, "Fail to finish.");
                }
                break;
            case 1:
            case 3:
                mTimer++;
                if (mArrived > 0) {
                    mStep++;
                    Log.i(TAG, String.format("mArrived1:%d", mData.length));
                }
                ret = 1;
                break;
            default:
                break;
        }
        if (ret != 1) {
            mStep = 0;
        }
        return ret;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private int Connection(final int message) {
        int ret = 0;

        switch (mStep) {
            case 0:
                d.setCurrentLevel(getLevel());
                mRetry = 0;
                mConnect = null;
                mPosition = Position(mAddress);
                if (mDeviceList.Rssi(mPosition) > -200) {
                    mConnect = mAddress;
                    Log.i(TAG, "Try to connect " + mAddress);
                }
                if (mConnect != null) {
                    mStep++;
                    ret = 1;
                } else {
                    ret = -99;
                }
                break;
            case 1:
                if (!mServiceActive) {
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    Log.i(TAG, " bindService");
                    mTimer = 0;
                }
                mStep++;
                ret = 1;
                break;
            case 2:
                if (!mServiceActive) {
                    mTimer++;
                } else {
                    Log.i(TAG, " mServiceActive true");
                    mStep++;
                    mTimer = 0;
                }
                ret = 1;
                break;
            case 3:
                if (mBluetoothLeService.connect(mConnect)) {
                    mConnected = 0;
                    Log.i(TAG, "BLE service connecting...");
                    mStep++;
                    mTimer = 0;
                    ret = 1;
                } else {
                    Log.i(TAG, "Fail to connect service");
                    if (mRetry < 3) {
                        mRetry++;
                    } else {
                        ret = -98;
                    }
                }
                break;
            case 4:
                ret = 1;
                switch (mConnected) {
                    case 0:
                    case 1:
                        mTimer++;   /*20*/
                        break;
                    case -1:
                        if (mRetry < 3) {
                            mRetry++;
                            Log.i(TAG, "Retry connect service");
                            mStep = 3;
                        } else {
                            mStep++;
                        }
                        break;
                    case 2:
                        mStep++;
                        break;
                }
                break;
            case 5:
                if (mConnected == 2) {
                    mStep++;
                    ret = 2;
                } else {
                    if (mConnected == -1) {
                        ret = -97;
                    } else {
                        Log.i(TAG, "Reject to connect service...");
                        ret = -96;
                    }
                }
                break;
            default:
                break;
        }

        if (ret != 1) {
            mStep = 0;
        }
        return ret;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private int sessionEstablish(final int mesage) {
        byte[] send = null;
        int ret;
        int[] res = new int[2];
        ret = 0;/*0:fail,1:processing,2:established*/

        switch (mStep) {
            case 0:
                send = d.Open();
                if (send != null) {
                    mStep++;
                    mTimer = 0;
                    mArrived = 0;
                    mBluetoothLeService.write(send);
                    ret = 1;
                    Log.i(TAG, String.format("Open:%d", send.length));
                }
                break;
            case 2:
                send = d.Session(res, mData);
                if (res[0] != 0) {
                    if (send != null) {
                        mStep++;
                        mTimer = 0;
                        mArrived = 0;
                        mBluetoothLeService.write(send);
                        ret = 1;
                        Log.i(TAG, String.format("Session:%d", send.length));
                    }
                } else {
                    ret = -89;
                    Log.i(TAG, "Fail to connect HDLC.");
                }
                break;

            case 4:
                send = d.Challenge(res, mData);
                if (res[0] != 0) {
                    if (send != null) {
                        mStep++;
                        mTimer = 0;
                        mBluetoothLeService.write(send);
                        ret = 1;
                        Log.i(TAG, String.format("Challenge:%d", send.length));
                    } else {/*チャレンジ不要*/
                        if (d.Rank() == DLMS.RANK_POWER || d.Rank() == DLMS.RANK_READER || d.Rank() == DLMS.RANK_PUBLIC) {
                            ret = 2;
                        } else {
                            ret = -88;
                        }
                    }
                } else {
                    ret = -87;
                }
                break;
            case 6:
                send = d.Confirm(res, mData);
                if (res[0] != 0) {
                    if (d.Rank() == DLMS.RANK_ADMIN || d.Rank() == DLMS.RANK_SUPER) {
                        ret = 2;
                        Log.i(TAG, "Confirm");
                    } else {

                    }
                } else {
                    ret = -86;
                }
                break;
            case 1:
            case 3:
            case 5: /*received */
                mTimer++;
                if (mArrived > 0) {
                    mStep++;
                    Log.i(TAG, String.format("DataArrived-session:%d", mData.length));
                }
                ret = 1;
                break;
            default:
                break;
        }
        if (ret != 1) {
            mStep = 0;
        }
        return ret;
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private int AccessData(final int mode, final int index, final int attr, final boolean modeling) {
        byte[] send = null;

        int ret = 0;
        int feedback;
        switch (mStep) {
            case 0:
                mCount++;
                switch (mode) {
                    case 0:
                        Log.i(TAG, String.format("Getting index:%d, attr:%d", index, attr));
                        send = d.getReq(index, (byte) attr, mSel, mParameter.toString(), mDataIndex);
                        break;
                    case 1:
                        Log.i(TAG, String.format("Setting index:%d, attr:%d", index, attr));
                        send = d.setReq(index, (byte) attr, mSel, mParameter.toString(), mDataIndex);
                        break;
                    case 2:
                        Log.i(TAG, String.format("Calling index:%d, attr:%d", index, attr));
                        send = d.actReq(index, (byte) attr, mParameter.toString(), mDataIndex);
                        break;
                }
                mTimer = 0;
                mArrived = 0;
                mBluetoothLeService.write(send);
                mStep++;
                ret = 1;
                break;
            case 2:
                int[] res = new int[2];
                res[0] = 0;
                res[1] = 0;
                mReceive = d.DataRes(res, mData, modeling);
                if (res[1] < 0) {
                    feedback = mItemFragment.DataArrived(mReceive, true);
                    ret = res[1];
                } else {
                    ret = res[0];
                    if (mkeep) {
                        feedback = mItemFragment.DataArrived(mReceive, false);
                    } else {
                        feedback = mItemFragment.DataArrived(mReceive, res[0] == 0);
                    }
                }
                if (feedback != 0) {
                    ret = feedback;
                }
                break;
            case 1:
            default:
                mTimer++;
                if (mArrived > 0 && mTimer > 5) {
                    mStep++;
                    Log.i(TAG, String.format("DataArrived-access:%d", mData.length));
                }
                ret = 1;
                break;
        }
        if (ret != 1) {
            mStep = 0;
        }
        return ret;
    }

    @Override
    public void fragment(ItemFragment fragment) {
        mItemFragment = fragment;
    }

    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT})
    @Override
    public int fragmentOrder(final int order_id) {

        switch (order_id) {
            case ODR_SCAN_ON:
                if (!mScanning) {
                    if (mBluetoothAdapter != null) {
                        mDeviceList.Deactivate();
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                        mScanning = true;
                        Log.i(TAG, "startLeScan");
                    }
                }
                break;
            case ODR_SCAN_RESET:
                mDeviceList.Deactivate();
                break;
            case ODR_SCAN_OFF:
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                    Log.i(TAG, "stopLeScan");
                }
                break;
            case ODR_UPDATE:
                invalidateOptionsMenu();
                break;
            case ODR_DISCONNECT:
                Disconnect(true);
                break;
            case ODR_RELEASE:
                final Handler handler;
                final Runnable r;
                handler = new Handler();
                r = new Runnable() {
                    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    @Override
                    public void run() {
                        int ret = 0;
                        ret = Release();
                        if (ret > 0) {
                            handler.postDelayed(this, mTick);
                        } else {
                            Disconnect(true);
                        }
                    }
                };
                handler.post(r);
                break;
        }
        return order_id;
    }

    @Override
    public void onLoginSuccess() {

    }

    private int Parameter(final int message_id) {
        // Default return value, can be overridden by specific cases
        int ret = 0;
        // By default, mStep is reset if ret is not 1.
        // If a specific case needs a different behavior for mStep, it should handle it.
        boolean resetStep = true;

        // Common setup for many cases
        mParameter.setLength(0); // Clear mParameter at the beginning for most cases

        switch (message_id) {
            case MSG_SET_CLOCK:
                mSel = 0;
                long sec = d.CurrentDatetimeSec() + 1;
                mParameter.append("090c").append(d.SecToRawDatetime(sec));
                ret = 3;
                break;

            case MSG_EVENT_RECORD:
            case MSG_ENERGY_RECORD:
            case MSG_BILLING_RECORD:
                mSel = 0;
                appendCounterParameterIfPresent();
                ret = 3;
                break;

            case MSG_SETUP:
                // mParameter is already cleared
                mSel = 0; // Default mSel for MSG_SETUP
                ret = handleSetupSubStage(); // Delegate to a helper method
                break;

            case MSG_CHANGE_THRESH:
                mSel = 0;
                mParameter.append("01010204128001120032110c1101");
                ret = 3;
                break;

            case MSG_READER:
                // mParameter is already cleared
                // mSel will be set within handleReaderSubStage
                ret = handleReaderSubStage(); // Delegate to a helper method
                break;

            default:
                // If message_id is not handled, ret remains 0.
                // Consider if mStep should be reset in this case.
                // If ret is 0 here, mStep will be reset by the final check.
                Log.w(TAG, "Parameter: Unhandled message_id: " + message_id);
                break;
        }

        if (resetStep && ret != 1) {
            mStep = 0;
        }
        return ret;
    }

    /**
     * Handles the logic for MSG_SETUP based on mSubStage.
     * Assumes mParameter has been cleared.
     *
     * @return The calculated 'ret' value for MSG_SETUP.
     */
    private int handleSetupSubStage() {
        int ret = 0; // Default for unhandled substages
        long sec;

        switch (mSubStage) {
            case 1:
                sec = d.CurrentDatetimeSec() + 1;
                mParameter.append("090c").append(d.SecToRawDatetime(sec));
                ret = 3;
                break;
            case 3:
                mParameter.append("120001");
                ret = 3;
                break;
            case 5:
                ret = 3; // mParameter remains empty
                break;
            case 7:
                mSel = 2; // Override default mSel for this substage
                appendCounterParameterIfPresent();
                ret = 3;
                break;
            default:
                Log.w(TAG, "Parameter (MSG_SETUP): Unhandled mSubStage: " + mSubStage);
                break;
        }
        return ret;
    }

    /**
     * Handles the logic for MSG_READER based on mSubStage.
     * Assumes mParameter has been cleared.
     *
     * @return The calculated 'ret' value for MSG_READER.
     */
    private int handleReaderSubStage() {
        int ret = 0; // Default for unhandled substages
        mSel = 0;    // Default mSel for MSG_READER, can be overridden

        switch (mSubStage) {
            case 1:
                mParameter.append("120001");
                ret = 3;
                break;
            case 3:
                // mParameter remains empty
                ret = 3;
                break;
            case 5:
                mSel = 2; // Override default mSel for this substage
                appendCounterParameterIfPresent();
                ret = 3;
                break;
            default:
                Log.w(TAG, "Parameter (MSG_READER): Unhandled mSubStage: " + mSubStage);
                break;
        }
        return ret;
    }


    private void appendCounterParameterIfPresent() {
        if (CounterParameter.length() != 0) {
            mParameter.append(CounterParameter.toString());
            CounterParameter.setLength(0);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private int Access(final int message_id) {
        int ret = 0;

        switch (message_id) {
            case MSG_CONNECT:/*接続のみ*/
                break;

            case MSG_SET_CLOCK:
                ret = AccessData(1, DLMS.IST_DATETIME_NOW, 2, false);
                break;

            case MSG_EVENT_RECORD:
                ret = AccessData(0, DLMS.IST_POWER_QUALITY, 2, false);
                break;

            case MSG_ENERGY_RECORD:
                ret = AccessData(0, DLMS.IST_LOAD_PROFILE, 2, false);
                break;

            case MSG_BILLING_RECORD:
                ret = AccessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
                break;

            case MSG_SETUP:
                switch (mSubStage) {
                    case 2:
                        ret = AccessData(1, DLMS.IST_DATETIME_NOW, 2, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 4:
                        ret = AccessData(2, DLMS.IST_DEMAND_RESET, 1, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 6:
                        ret = AccessData(0, DLMS.IST_BILLING_PARAMS, 7, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 8:
                        ret = AccessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
                        break;
                    default:
                        break;
                }
                break;
            case MSG_CHANGE_THRESH:
                mDataIndex = 8;
                ret = AccessData(1, DLMS.IST_DETECT, 2, false);
                break;

            case MSG_READER:
                switch (mSubStage) {
                    case 2:
                        ret = AccessData(2, DLMS.IST_DEMAND_RESET, 1, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 4:
                        ret = AccessData(0, DLMS.IST_BILLING_PARAMS, 7, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 6:
                        ret = AccessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
                        break;
                    default:
                        break;
                }
                break;

            default:
                Log.i(TAG, "Not implemented function...");
                break;
        }
        if (ret == 0) {
            Log.i(TAG, "Access - Finish");
        }
        return ret;
    }

    @Override
    public int messageID() {
        return mCurrentMessage;
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public int fragmentMessage(final int message) {
        int ret = 0;

        Log.i(TAG, String.format("Message now:%d, new: %d, Stage:%d", mCurrentMessage, message, mStage));
        mCurrentMessage = message;
        if (message <= 0) {
            if (message == 0) {
                mCurrentMessage = -1;
            } else {
                return ret;
            }
        }
        if (mProgressing) {
            Log.i(TAG, String.format("Waiting Process:%b, Scan: %b", mProgressing, mScanning));
            return 1;
        }
        mProgressing = true;
        if (mTimer > timeout) {
            mTimer = 0;
            Log.i(TAG, "fragmentMessage - Timeout");
            if (false) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Detect timeout");
                builder.setMessage("No data receive from meter\nPlease try again");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
            ret = -50;
            Disconnect(true);
        } else {
            switch (mStage) {
                case 0:
                    ret = Connection(message);
                    if (ret == 2) {
                        mStage++;
                    }
                    if (ret < 0) {
                        Disconnect(true);
                    }
                    break;
                case 1:
                    ret = sessionEstablish(message);
                    if (ret == 2) {
                        mStage++;
                        mSubStage = 0;
                    }
                    if (ret < 0) {
                        Disconnect(true);
                    }
                    break;
                case 2:
                    switch (mSubStage) {
                        case 0:
                            mTotal = 0;
                            mDataIndex = 0;
                            mkeep = false;
                            mSubStage++;
                            ret = 1;
                            break;
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 9:
                        case 11:
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            ret = Parameter(message);
                            if (ret == 3) {
                                ret = 1;
                                mSubStage++;
                            }
                            break;
                        case 2:
                        case 4:
                        case 6:
                        case 8:
                        case 10:
                        case 12:
                            ret = Access(message);
                            if (ret <= 0) {
                                if (ret < 0) {
                                    Log.i(TAG, "Detect error...");
//                                    showToast("Detect error...");
                                }
                                mSubStage = 0;
                                mDataIndex = 0;
                            } else {
                                if (ret > 2) {
                                    if (ret == 3) {
                                        mSubStage = 1;
                                    } else {
                                        mSubStage++;
                                    }
                                }
                            }
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            break;
                        default:
                            mSubStage = 0;
                            break;
                    }
                    Log.i(TAG, String.format("fragmentMessage - result :%d", ret));
                    if (ret == 0) {
                        ret = 2;
                        mStage++;
                    } else {
                        if (ret < 0) {
                            Disconnect(true);
                        }
                    }
                    break;
                case 3:
                    ret = sessionRelease();
                    if (ret <= 0) {
                        Disconnect(true);
                    }
                    break;

            }
        }
        mProgressing = false;
        return ret;
    }
}