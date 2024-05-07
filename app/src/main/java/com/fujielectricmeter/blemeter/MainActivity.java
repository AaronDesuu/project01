package com.fujielectricmeter.blemeter;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static java.lang.Integer.parseInt;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.ServiceConnection;
import android.database.sqlite.*;
import android.icu.text.SimpleDateFormat;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.fujielectricmeter.blemeter.databinding.ActivityMainBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;
import static com.seikoinstruments.sdk.thermalprinter.PrinterManager.PRINTER_MODEL_MP_B20;

public class MainActivity extends AppCompatActivity implements
        ItemFragment.messageManager {

    private final String TAG = MainActivity.class.getSimpleName();
    public static StringBuffer CounterParameter = new StringBuffer();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemFragment mItemFragment;
    private int mCurrentMessage;
    public static ActionBar mActionBar;
    public static int mScanTick = 2000;
    public static int mTick = 100;
    public static int mInterval = 0;
    private static final int timeout = 60;
    private int mRetry;
    private ArrayList<String> mReceive;
    private String mAddressShort = null;
    public static byte mAttr, mMode, mSel;
    private byte mDataIndex;
    private StringBuffer mParameter = new StringBuffer();
    private String mPassword;
    private BluetoothLeService mBluetoothLeService;
    private DeviceList mDeviceTemp = new DeviceList();
    private DeviceList mDeviceList = new DeviceList();
    public static String mConnect;
    public static String Login;
    public static String Level;
    public static String mfirstName;
    public static String mfirstKey;
    public static String msecondName;
    public static String msecondKey;
    public static int Selection;
    public static String mSerialID;
    public static String mAddress;
    private static int mPosition;
    public static int mFragmentid;
    private BluetoothAdapter mBluetoothAdapter;
    private int mArrived;
    private byte[] mData;
    private int mTimer;
    private int mCount;
    private int mTotal;
    private int mStage;
    public static int mSubStage;
    private int mStep;
    private int mPrmState;
    public static DLMS d;
    private Handler mHandler;
    private boolean mServiceActive;
    private boolean mBinding;
    private int mScan;
    public static boolean mScanning;
    private int mConnected;
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean mkeep = false;
    public static ArrayList<String> mTemp = new ArrayList<String>();
    public static File folderFiles;
    public static File folderCache;
    public static File folderExternal;
    public static File folderDownload;
    public static File folderDocument;

    public static CSVParser login;
    public static CSVParser rootcsv;
    public static CSVParser firstcsv;
    public static CSVParser secondcsv;
    public static CSVParser fourthcsv;
    public static Trail trail;
    public static ArrayList<SampleListItem> mListItems = new ArrayList<>();
    private boolean mPermission = false;
    private final String defaultAccount[] = {
            "Super,Reader,0",
            "Admin,Admin,1",
            "Power,Power,2",
            "Reader,Reader,3"};

    static String root_column;

    final static String[] root_row = {
            "1,0,2401000001,48:23:35:0E:2B:BE,,,,,,,,",
            "2,0,2401000002,48:23:35:0E:2B:E4,,,,,,,,",
            "3,0,2401000003,48:23:35:0E:2B:BF,,,,,,,,",
            "4,0,2401000004,48:23:35:0E:2B:E9,,,,,,,,",
            "5,0,2401000005,48:23:35:0E:2A:F7,,,,,,,,",
            "6,0,2401000006,48:23:35:0E:2A:73,,,,,,,,",
            "7,0,2401000007,48:23:35:0E:2B:C8,,,,,,,,",
            "8,0,2401000008,48:23:35:0E:2B:C6,,,,,,,,",
            "9,0,2401000009,48:23:35:0E:2A:44,,,,,,,,",
            "10,0,2401000010,48:23:35:0E:2C:6F,,,,,,,,",
            "11,0,2401000011,48:23:35:0E:2B:C7,,,,,,,,",
            "12,0,2401000012,48:23:35:0E:2B:C1,,,,,,,,",
            "13,0,2401000013,48:23:35:0E:2B:E3,,,,,,,,",
            "14,0,2401000014,48:23:35:0E:2C:6D,,,,,,,,",
            "15,0,2401000015,48:23:35:0E:2B:C5,,,,,,,,",
            "16,0,2401000016,48:23:35:0E:2C:7B,,,,,,,,",
            "17,0,2401000017,48:23:35:0E:2B:F6,,,,,,,,",
            "18,0,2401000018,48:23:35:0E:2C:6E,,,,,,,,",
            "19,0,2401000019,48:23:35:0E:2C:77,,,,,,,,",
            "20,0,2401000020,48:23:35:0E:2B:49,,,,,,,,",
            "21,0,2401000021,48:23:35:0E:2B:E5,,,,,,,,",
            "22,0,2401000022,48:23:35:0E:2C:78,,,,,,,,",
            "23,0,2401000023,48:23:35:0E:2B:E6,,,,,,,,",
            "24,0,2401000024,48:23:35:0E:2B:F4,,,,,,,,",
            "25,0,2401000025,48:23:35:0E:2B:BD,,,,,,,,",
            "26,0,2401000026,48:23:35:0E:2B:E2,,,,,,,,",
            "27,0,2401000027,48:23:35:0E:2B:EA,,,,,,,,",
            "28,0,2401000028,48:23:35:0E:2C:05,,,,,,,,",
            "29,0,2401000029,48:23:35:0E:2B:E1,,,,,,,,",
            "30,0,2401000030,48:23:35:0E:2B:C9,,,,,,,,",
            "31,0,2401000031,48:23:35:0E:2B:DE,,,,,,,,",
            "32,0,2401000032,48:23:35:0E:2A:42,,,,,,,,",
            "33,0,2401000033,48:23:35:0E:2C:76,,,,,,,,",
            "34,0,2401000034,48:23:35:0E:2A:FA,,,,,,,,",
            "35,0,2401000035,48:23:35:0E:2B:F7,,,,,,,,",
            "36,0,2401000036,48:23:35:0E:2B:EE,,,,,,,,",
            "37,0,2401000037,48:23:35:0E:2C:F8,,,,,,,,",
            "38,0,2401000038,48:23:35:0E:2B:E8,,,,,,,,",
            "39,0,2401000039,48:23:35:0E:2B:CC,,,,,,,,",
            "40,0,2401000040,48:23:35:0E:2C:7A,,,,,,,,",
            "41,0,2401000041,48:23:35:0E:2B:CA,,,,,,,,",
            "42,0,2401000042,48:23:35:0E:2B:C0,,,,,,,,",
            "43,0,2401000043,48:23:35:0E:2B:F0,,,,,,,,",
            "44,0,2401000044,48:23:35:0E:2B:EB,,,,,,,,",
            "45,0,2401000045,48:23:35:0E:2C:71,,,,,,,,",
            "46,0,2401000046,48:23:35:0E:2B:CB,,,,,,,,",
            "47,0,2401000047,48:23:35:0E:2B:DF,,,,,,,,",
            "48,0,2401000048,48:23:35:0E:2B:C2,,,,,,,,",
            "49,0,2401000049,48:23:35:0E:2A:3D,,,,,,,,",
            "50,0,2401000050,48:23:35:0E:2B:E0,,,,,,,,",
            "51,0,2403000000,48:23:35:10:4B:AD,,,,,,,,",
            "52,0,2403000000,48:23:35:02:64:72,,,,,,,,"
    };
    final static String PRINTER1 = "68:84:7E:65:A9:BA";
    private static PrinterManager mPrinterManager;
    private static String mPrintData = null;


    public int Rssi(final int position) {
        return mDeviceList.Rssi(position);
    }

    public int Position(final String address) {
        return mDeviceList.Position(address);
    }


    public static String SerialID() {
        if (mSerialID != null) {
            return String.format("%s, %s", mSerialID, mAddress);
        } else {
            return null;
        }
    }


    public static void deleteFile(final String name, final File folder) {
        File file = new File(folder, name);
        file.delete();
    }

    public static boolean writeFile(String data, String name, File folder) {
        boolean ok = true;
        File file = new File(folder, name);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "SHIFT_JIS"))) {
            bw.write(data);
            bw.close();
        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public static void writeFile(String data, File file) {
        // try-with-resources
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBinaryFile(byte[] data, String file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
        } catch (Exception e) {
        }
    }

    public static void ReadBinaryFile(byte[] data, String file) {
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();
        } catch (Exception e) {
        }
    }
    public void Print(final String data) {
        try {
            mPrinterManager.connect(PRINTER_MODEL_MP_B20, PRINTER1, true);
            mPrinterManager.sendDataFile(folderFiles + "/logo3.jpg");
            mPrinterManager.sendText("+------------------------------+\n");
            mPrinterManager.sendText("|        Electric bill         |\n");
            mPrinterManager.sendText("+------------------------------+\n\n");
            mPrinterManager.sendText(data);
            mPrinterManager.disconnect();
        } catch (PrinterException e) {
            e.printStackTrace();
            showToast(e.getMessage());
            try {
                mPrinterManager.disconnect();
            } catch (PrinterException e1) {
            }
        }
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
            }
            else{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, " onActivityResult()");
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            showToast("You choose disable bluetooth. Exit this app");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folderFiles = getFilesDir();
//        folderFiles = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        folderCache = getCacheDir();
        folderExternal = getExternalFilesDir(null);
        folderDocument = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
        folderDownload = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
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
            showToast("Bluetooth IC dose not found!");
            finish();
            return;
        }
        copyAssetsFile();
        mPrinterManager = new PrinterManager(getApplicationContext());
        mPrintData = null;

        root_column =   getString(R.string.table2_key)+","+
                getString(R.string.table2_col1)+","+
                getString(R.string.table2_col2)+","+
                getString(R.string.table2_col3)+","+
                getString(R.string.table2_col4)+","+
                getString(R.string.table2_col5)+","+
                getString(R.string.table2_col6)+","+
                getString(R.string.table2_col7)+","+
                getString(R.string.table2_col8)+","+
                getString(R.string.table2_col9)+","+
                getString(R.string.table2_col10)+","+
                getString(R.string.table2_col11);
        rootcsv = new CSVParser("meter.csv", folderFiles);
        if (!rootcsv.exist("meter.csv")) {
            rootcsv.New(root_column);
            for (int i = 0; i < root_row.length; i++) {
                rootcsv.Add(root_row[i]);
            }
            rootcsv.writeFile();
        }

        CSVParser csv = new CSVParser(folderExternal);
        login = new CSVParser(folderFiles);
        if (!login.readFile("login.csv")) {
            login.New(getString(R.string.login) + "," + getString(R.string.password) + "," + getString(R.string.authenticate));
            for (int i = 0; i < defaultAccount.length; i++) {
                login.Add(defaultAccount[i]);
            }
            login.writeFile();
        }
        if (csv.readFile("login.csv")) {
            boolean update = false;
            boolean find = false;
            while (!find) {
                String newAccount = csv.Row("Col1");
                if (newAccount == null) {
                    break;
                }
                String newPassword = csv.Column("Col2");
                login.Reset();
                while (true) {
                    String account = login.Row(getString(R.string.login));
                    if (account == null) {
                        break;
                    }
                    if (newAccount.equals(account)) {
                        if (newPassword.equals(login.Column(getString(R.string.password)))) {
                            int level = Integer.parseInt(login.Column(getString(R.string.authenticate)));
                            if (level < 2) {
                                update = true;
                            }
                        }
                        find = true;
                        break;
                    }
                }
            }
            while (update) {
                String newAccount = csv.Row("Col1");
                if (newAccount == null) {
                    break;
                }
                String newPassword = csv.Column("Col2");
                MainActivity.login.Reset();
                find = false;
                while (update) {
                    String account = login.Row(getString(R.string.login));
                    if (account == null) {
                        break;
                    }
                    if (newAccount.equals(account)) {
                        login.Update(newPassword, getString(R.string.password));
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    login.Add(newAccount + "," + newPassword + ",3");
                }
            }
            if (update) {
                login.writeFile();
                deleteFile("login.csv", folderExternal);
            }
        }

        mAddressShort = "UnknownMeter";
        mInterval = 0;
        mCurrentMessage = -1;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        mActionBar = getSupportActionBar();
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

    @Override
    public boolean onSupportNavigateUp() {
        Log.i(TAG, " onSupportNavigateUp()");
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
//      registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mPermission = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, " onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " onDestroy");
    }

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
                break;
            case R.id.menu_select:
                final Handler handler;
                final Runnable r;
                Disconnect(true);
                handler = new Handler();
                r = new Runnable() {
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
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String name = String.format("%s_%s_%s.csv", msecondName, mActionBar.getTitle().toString(), sdf.format(date).toString()).replace(':', '-');
                if (false) {
//                Intent intent = new Intent(Intent.ACTION_SENDTO);
                    Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                String[] addresses = new String[1];
//                byte[] b = MainActivity.d.getEmail();
//                String s = MainActivity.d.setStr2Str(b, 0, b.length);
//                addresses[0] = s;
//                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    intent.putExtra(Intent.EXTRA_SUBJECT, name);
                    intent.putExtra(Intent.EXTRA_TEXT, mItemFragment.getData());
                    intent.setType("text/plain");
//                Intent shareIntent = Intent.createChooser(intent, null);
                    startActivity(intent);
//                startActivity(shareIntent);
                } else {
                    byte[] bom = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
                    writeBinaryFile(bom, folderExternal + "/" + name);
                    File file = new File(folderExternal, name);
                    writeFile(mItemFragment.getData(), file);
//                       Uri contentUri = FileProvider.getUriForFile(this, "com.fujielectricmeter.blemeter", file);
//                       Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                       shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//                       shareIntent.setType("text/plane; charset=utf-8");
//                       this.startActivity(Intent.createChooser(shareIntent, "choose"));
                }
                ret = true;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_disconnect).setVisible(false);
        menu.findItem(R.id.menu_scan).setVisible(false);
        menu.findItem(R.id.menu_select).setVisible(false);
        menu.findItem(R.id.menu_share).setVisible(false);
        menu.findItem(R.id.menu_load).setVisible(false);
        menu.findItem(R.id.menu_save).setVisible(false);
        menu.findItem(R.id.menu_user).setVisible(false);
        if (mFragmentid < 2) {
            if (Level != null) {
                if (Integer.parseInt(Level) <= 1) {
                    menu.findItem(R.id.menu_user).setVisible(true);
                } else {
                    menu.findItem(R.id.menu_user).setVisible(false);
                }
                menu.findItem(R.id.menu_password).setVisible(true);
            } else {
                menu.findItem(R.id.menu_user).setVisible(false);
                menu.findItem(R.id.menu_password).setVisible(false);
            }
            menu.findItem(R.id.menu_batch).setVisible(false);
            menu.findItem(R.id.menu_stop).setVisible(false);
        } else {
            menu.findItem(R.id.menu_user).setVisible(false);
            menu.findItem(R.id.menu_password).setVisible(false);
            if (mItemFragment.running()) {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_batch).setVisible(false);
            } else {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_batch).setVisible(true);
            }
        }
        menu.findItem(R.id.menu_exit).setVisible(true);
/*
        menu.findItem(R.id.menu_scan).setVisible(true);
        if (mServiceActive) {
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        if (mItemFragment.getData().isEmpty()) {
//            menu.findItem(R.id.menu_share).setVisible(true);
        } else {
//            menu.findItem(R.id.menu_share).setEnabled(true);
        }
 */
        return true;
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


    public static void setLevel(final int level) {

        Level = String.format("%d", level);
    }

    public static int getLevel() {
        int ret = -1;

        if (Level != null) {
            ret = Integer.parseInt(Level);
        }
        return ret;
    }

    public class ScanDevice {
        private BluetoothDevice mDev;
        private int mRssi;
        private boolean mChk;

        ScanDevice() {
            mDev = null;
            mChk = false;
            mRssi = -200;
        }

        ScanDevice(final BluetoothDevice dev, int rssi) {
            mDev = dev;
            mChk = false;
            mRssi = rssi;
        }

        public BluetoothDevice Device() {
            return mDev;
        }

        public String Address() {
            return mDev.getAddress();
        }

        public String Name() {
            return mDev.getName();
        }

        public int Rssi() {
            return mRssi;
        }

        public boolean Check() {
            return mChk;
        }

        public boolean Activate() {
            if (Rssi() == -200) {
                return false;
            } else {
                return true;
            }
        }

        public void Device(BluetoothDevice dev) {
            mDev = dev;
        }

        public void Rssi(int rssi) {
            mRssi = rssi;
        }

        public void Check(boolean chk) {
            mChk = chk;
        }
    }

    // Adapter for holding devices found through scanning.
    public class DeviceList {
        private ArrayList<ScanDevice> mScanDevice;

        public DeviceList() {
            mScanDevice = new ArrayList<ScanDevice>();
        }

        public int size() {
            return mScanDevice.size();
        }

        public void addDevice(BluetoothDevice device, Integer rssi) {

            boolean find = false;
            for (int i = 0; i < mScanDevice.size(); i++) {
                if (Device(i).getAddress().equals(device.getAddress())) {
                    mScanDevice.get(i).Rssi(rssi);
                    find = true;
                    break;
                }
            }
            if (!find) {
                mScanDevice.add(new ScanDevice(device, rssi));
            }
        }

        public void Deactivate() {
            for (int i = 0; i < mScanDevice.size(); i++) {
                mScanDevice.get(i).Rssi(-200);
            }
        }

        public BluetoothDevice Device(int position) {
            return mScanDevice.get(position).Device();
        }

        public int Rssi(final int position) {
            if (position >= 0) {
                if (position < mScanDevice.size()) {
                    return mScanDevice.get(position).Rssi();
                } else {
                    return -200;
                }
            } else {
                return -200;
            }
        }

        public int Position(final String Address) {
            int ret = -1;
            for (int i = 0; i < mScanDevice.size(); i++) {
                if (Address.equals(mScanDevice.get(i).Address())) {
                    ret = i;
                    break;
                }
            }
            return ret;
        }

        public String Address(int position) {
            return mScanDevice.get(position).Address();
        }

        public String Name(int position) {
            return mScanDevice.get(position).Name();
        }

        public void clear() {
            mScanDevice.clear();
        }

        public boolean Activate(int position) {
            return mScanDevice.get(position).Activate();
        }

        public boolean Check(int position) {
            return mScanDevice.get(position).Check();
        }

        public void Done(int position) {
            mScanDevice.get(position).Check(true);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
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
    // Handles various events fired by the write_read.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

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


    public int Release() {
        int ret = 0;
        if (mTimer > timeout) {
            Log.i(TAG, "Release - Timeout");
            mTimer = 0;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Detect timeout");
            builder.setMessage("No data receive from meter\nPlease try again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                        if (d.Rank() == d.RANK_POWER || d.Rank() == d.RANK_READER || d.Rank() == d.RANK_PUBLIC) {
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
                    if (d.Rank() == d.RANK_ADMIN || d.Rank() == d.RANK_SUPER) {
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

    private int accessData(final int mode, final int index, final int attr, final boolean modeling) {
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

    public final static int ODR_SCAN_ON = 0;
    public final static int ODR_SCAN_OFF = (ODR_SCAN_ON + 1);
    public final static int ODR_SCAN_RESET = (ODR_SCAN_OFF + 1);
    public final static int ODR_UPDATE = (ODR_SCAN_RESET + 1);
    public final static int ODR_LIST_CLEAR = (ODR_UPDATE + 1);
    public final static int ODR_RELEASE = (ODR_LIST_CLEAR + 1);
    public final static int ODR_DISCONNECT = (ODR_RELEASE + 1);

    @Override
    public int fragmentOrder(final int order_id) {

        switch (order_id) {
            case ODR_SCAN_ON:
                if (!mScanning) {
                    if (mBluetoothAdapter != null) {
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
    public final static int MSG_BREAKER = MSG_ENERGY_RECORD + 1;
    public final static int MSG_ALERT_CLEAR = MSG_BREAKER + 1;

    private int Parameter(final int message_id) {
        int ret = 0;
        long sec;
        switch (message_id) {
            case MSG_SET_CLOCK:
                mSel = 0;
                sec = d.CurrentDatetimeSec() + 1;
                mParameter.append("090c" + d.SecToRawDatetime(sec));
                ret = 3;
                break;
            case MSG_EVENT_RECORD:
            case MSG_ENERGY_RECORD:
                mSel = 0;
                mParameter.setLength(0);
                if (CounterParameter.length() != 0) {
                    mParameter.append(CounterParameter.toString());
                    CounterParameter.setLength(0);
                }
                ret = 3;
                break;
            case MSG_SETUP:
                mSel = 0;
                mParameter.setLength(0);
                switch (mSubStage) {
                    case 1:
                        sec = d.CurrentDatetimeSec() + 1;
                        mParameter.append("090c" + d.SecToRawDatetime(sec));
                        ret = 3;
                        break;
                    case 3:
                        mParameter.append("120001");
                        ret = 3;
                        break;
                    case 5:
                        ret = 3;
                        break;
                    case 7:
                        mSel = 2;
                        if (CounterParameter.length() != 0) {
                            mParameter.append(CounterParameter.toString());
                            CounterParameter.setLength(0);
                        }
                        ret = 3;
                        break;
                    default:
                        break;
                }
                break;

            case MSG_READER:
                switch (mSubStage) {
                    case 1:
                        mSel = 0;
                        mParameter.setLength(0);
                        mParameter.append("120001");
                        ret = 3;
                        break;
                    case 3:
                        mSel = 0;
                        mParameter.setLength(0);
                        ret = 3;
                        break;
                    case 5:
                        mSel = 2;
                        mParameter.setLength(0);
                        if (CounterParameter.length() != 0) {
                            mParameter.append(CounterParameter.toString());
                            CounterParameter.setLength(0);
                        }
                        ret = 3;
                        break;
                    default:
                        break;
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

    private int Access(final int message_id) {
        int ret = 0;

        switch (message_id) {
            case MSG_CONNECT:/*接続のみ*/
                break;

            case MSG_SET_CLOCK:
                ret = accessData(1, DLMS.IST_DATETIME_NOW, 2, false);
                break;

            case MSG_EVENT_RECORD:
                ret = accessData(0, DLMS.IST_POWER_QUALITY, 2, false);
                break;

            case MSG_ENERGY_RECORD:
                ret = accessData(0, DLMS.IST_LOAD_PROFILE, 2, false);
                break;

            case MSG_SETUP:
                switch (mSubStage) {
                    case 2:
                        ret = accessData(1, DLMS.IST_DATETIME_NOW, 2, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 4:
                        ret = accessData(2, DLMS.IST_DEMAND_RESET, 1, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 6:
                        ret = accessData(0, DLMS.IST_BILLING_PARAMS, 7, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 8:
                        ret = accessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
                        break;
                    default:
                        break;
                }
                break;

            case MSG_READER:
                switch (mSubStage) {
                    case 2:
                        ret = accessData(2, DLMS.IST_DEMAND_RESET, 1, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 4:
                        ret = accessData(0, DLMS.IST_BILLING_PARAMS, 7, false);
                        if (ret == 0) {
                            ret = 5;
                        }
                        break;
                    case 6:
                        ret = accessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
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


    private boolean mProgressing = false;

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
                    if (ret < 0) {
                        Disconnect(true);
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