package com.fujielectricmeter.blemeter;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

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
import android.icu.util.Calendar;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fujielectricmeter.blemeter.databinding.ActivityMainBinding;
import com.woosim.printer.WoosimCmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        ItemFragment.messageManager {

    public static final String DEVICE_NAME = null;
    private static final boolean D = true;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_READ = 3;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    public static float[] ratio = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    public static String[] now_value = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",};
    public static String[] old_value = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",};
    public static Float[] total_value = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,};
    public static int MESSAGE_DEVICE_NAME;
    private final String TAG = MainActivity.class.getSimpleName();
    public static StringBuffer CounterParameter = new StringBuffer();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ItemFragment mItemFragment;
    private int mCurrentMessage;
    public static ActionBar mActionBar;
    public static int mScanTick = 2000;
    public static int mTick = 200;
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
    private boolean mServiceActive;
    private boolean mBinding;
    private int mScan;
    public static boolean mScanning;
    protected static BluetoothPrintService mPrintService = null;
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
    public static CSVParser ratecsv;
    public static CSVParser oldcsv;

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
            "1,0,0000001286,48:23:35:0E:47:E3,,,,,,,,",
            "2,0,0000001251,48:23:35:10:57:43,,,,,,,,",
            "3,0,0000001252,48:23:35:10:57:A6,,,,,,,,",
            "4,0,0000001253,48:23:35:10:4D:5C,,,,,,,,",
            "5,0,0000001254,48:23:35:10:53:3F,,,,,,,,",
            "6,0,0000001255,48:23:35:0E:37:21,,,,,,,,",
            "7,0,0000001256,48:23:35:0E:33:65,,,,,,,,",
            "8,0,0000001257,48:23:35:0E:31:CE,,,,,,,,",
            "9,0,0000001258,48:23:35:0E:3D:1A,,,,,,,,",
            "10,0,0000001259,48:23:35:10:55:7E,,,,,,,,",
            "11,0,0000001260,48:23:35:0E:3C:34,,,,,,,,",
            "12,0,0000001261,48:23:35:0E:2C:79,,,,,,,,",
            "13,0,0000001262,48:23:35:0E:2B:DD,,,,,,,,",
            "14,0,9999999999,48:23:35:10:4F:ED,,,,,,,,",
            "15,0,9999999998,48:23:35:02:68:50,,,,,,,,",
            "16,0,9999999997,48:23:35:0E:33:CF,,,,,,,,",
    };
    //final static String PRINTER1 = "68:84:7E:65:A9:BA";
    //private static String mPrintData = null;
    private String Tag;


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

    public String readFile(final String name, File folder) {
        StringBuffer buffer = new StringBuffer();
        File file = new File(folder, name);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "SHIFT_JIS"))) {
                while (true) {
                    String read = br.readLine();
                    if (read != null) {
                        buffer.append("\n" + read);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
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

    public static void printImageText() {
        mPrintService.write(WoosimCmd.initPrinter());
        mPrintService.write(WoosimCmd.setPageMode());
        mPrintService.write(WoosimCmd.PM_setArea(0, 0, 600, 4500));

/*        total_value[0] = Float.parseFloat(now_value[2]) - Float.parseFloat(old_value[2]);
        total_value[1] = total_value[0] * ratio[0] +
                Float.parseFloat(now_value[3]) * ratio[1] + total_value[0] * ratio[2];
        total_value[2] = Float.parseFloat(now_value[3]) * ratio[3] + 1 * ratio[4] + 1 * ratio[5];
        total_value[3] = total_value[0] * ratio[6] + total_value[0] * ratio[7];
        total_value[4] = total_value[0] * ratio[8] + total_value[0] * ratio[9];
        total_value[5] = total_value[0] * ratio[10] + total_value[3] * ratio[11] +
                total_value[0] * ratio[12] + total_value[0] * ratio[13] +
                total_value[0] * ratio[14] + total_value[0] * ratio[15];
        total_value[6] = total_value[0] * ratio[16] + total_value[0] * ratio[17] +
                total_value[0] * ratio[18] + total_value[2] * ratio[19] + total_value[4] * ratio[20];
        total_value[7] = total_value[1] + total_value[2] + total_value[3] +
                total_value[4] + total_value[5] + total_value[6]; */


        String _str1 =
                "================================================================\n" +
                        /*期間 月(September) 年　　レートの種類:レート名　　　　*/
                        "Period     :%s %04d       Rate Type     : %s\n";
        String str1 = String.format(_str1, "September",2024, "typA");
        String _str2 =
                /*メーター：シリアル番号/契約番号？     乗数   */
                "Meter      :%d %s     Multiplier    :1.0\n" +
                        /*日時 MM/DD/YYYY 　　　　　　　　　　　　　　　　　　　　　　　　　今回検針値 6.3 */
                        "Period To  :%02tm/%02td/%tY                   Pres Reading  :%6.03f\n";
        String str2 = String.format(_str2, 4002829, "BK0798", now_value[0], now_value[2]);
        String _str3 =
                /*日時 MM/DD/YYYY 　　　　　　　　　　　　　　　　　　前回検針値 6.3 */
                "Period From:%02tm/%02td/%tY                  Prev Reading : %6.03f\n" +
                        /*使用電力の瞬時値:2.3 　　　　　　　　　　　　　　　　　　         使用量 6.3 */
                        "Demand KW : %2.03f                         Total KWH Used : %6.03f\n";
        String str3 = String.format(_str3,old_value[1], old_value[2], now_value[3], total_value[0]);
        String str4 =
                "================================================================\n";
        String str5 =
                "CHARGES                 RATE              AMOUNT\n" +
                        "GEN/TRANS CHARGES\n";
        String _str6 =
                /*change name          　　　　　　　　　　　charge rate　　　rate*使用電力*/
                "  Generation System Charge    :       " + "%2.04f" + "/kwh" + " %,6.02f\n" +
                        "  Transmission Demand Charge  :       " + "%4.02f" + "/kw " + " %,6.02f\n" +
                        "  System Loss Charge          :       " + "%2.03f" + "/kwh" + " %,6.02f\n\n";
        String str6 = String.format(_str6, ratio[0], total_value[0] * ratio[0], ratio[1], Float.parseFloat(now_value[3]) * ratio[1], ratio[2], total_value[0] * ratio[2]);
        String _str7 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　　　　　　　　　　　　　　 GEN/TRANS CHARGESの小計 */
                        "                                       SUB TOTAL" + " %,6.02f\n\n";
        String str7 = String.format(_str7, total_value[1]);
        String str8 =
                "DISTRIBUTION CHARGES\n";
        String _str9 =
                /*change name          　　　　　　　　　　　charge rate　　　rate*使用電力*/
                "  Distribution Demand Charge  :       " + "%.02f" + " /kw " + " %,6.02f\n" +
                        "  Supply Fix Charge           :       " + "%.02f" + " /cst" + " %,6.02f\n" +
                        "  Metering Fix Charge         :       " + "%.02f" + " /cst" + " %,6.02f\n";
        String str9 = String.format(_str9, ratio[3], Float.parseFloat(now_value[3]) * ratio[3], ratio[4], 1 * ratio[4], ratio[5], 1 * ratio[5]);
        String _str10 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　DISTRIBUTION CHARGESの小計 */
                        "                                       SUB TOTAL" + " %,6.02f\n\n";
        String str10 = String.format(_str10, total_value[2]);
        String str11 =
                "REINVESTMENT FUND FOR\n" +
                        "SUSTAINABLE CAPEX\n";
        String _str12 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Reinvestment Fund for CAPEX :       " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  Member's CAPEX Contribution :       " + "%.04f" + "/kwh" + " %,6.02f\n";
        String str12 = String.format(_str12, ratio[6], total_value[0] * ratio[6], ratio[7], total_value[0] * ratio[7]);
        String _str13 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　REINVESTMENT FUND FOR SUSTAINABLE CAPEXの小計 */
                        "                                       SUB TOTAL" + " %,6.02f\n\n";
        String str13 = String.format(_str13, total_value[3]);
        String str14 =
                "OTHER CHARGES\n";
        String _str15 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Lifeline Discount/Subsidy   :      " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  Senior Citizen Subsidy      :      " + "%.04f" + "/kwh" + " %,6.02f\n";
        String str15 = String.format(_str15, ratio[8], total_value[0] * ratio[8], ratio[9], total_value[0] * ratio[9]);
        String _str16 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　                          OTHER CHARGESの小計 */
                        "                                       SUB TOTAL" + " %,6.02f\n\n";
        String str16 = String.format(_str16, total_value[4]);
        String str17 =
                "UNIVERSAL CHARGES\n";
        String _str18 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Missionary Elec(NPC-SPUG)   :       " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  Missionary Elec(RED)        :       " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  Environmental Charge        :       " + "%.04f" + "/kwh" + " %,6.02f\n";
        String str18 = String.format(_str18, ratio[10], total_value[0] * ratio[10], ratio[11], total_value[0] * ratio[11], ratio[5], total_value[0] * ratio[12]);
        String _str19 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Feed In Tariff Allowance    :       " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  NPC Stranded Contract       :       " + "%.04f" + "/kwh" + " %,6.02f\n" +
                        "  NPC Stranded Debts          :       " + "%.04f" + "/kwh" + " %,6.02f\n";
        String str19 = String.format(_str19, ratio[13], total_value[0] * ratio[13], ratio[14], total_value[0] * ratio[14], ratio[15], total_value[0] * ratio[15]);
        String _str20 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　　UNIVERSAL CHARGESの小計 */
                        "                                      SUB TOTAL" + " %,6.02f\n\n";
        String str20 = String.format(_str20, total_value[5]);
        String str21 =
                "VALUE ADDED TAX\n";
        String _str22 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Generation VAT              :       " + " %.04f" + "/kwh" + " %,6.02f\n" +
                        "  Transmission VAT            :       " + " %.04f" + "/kwh" + " %,6.02f\n" +
                        "  System Loss VAT             :       " + " %.04f" + "/kwh" + " %,6.02f\n";
        String str22 = String.format(_str22, ratio[16], total_value[0] * ratio[16], ratio[17], total_value[0] * ratio[17], ratio[18], total_value[0] * ratio[18]);
        String _str23 =
                /*change name          　　　　　　　charge rate　　　　　　rate*使用電力*/
                "  Distribution VAT            :          " + " %.04f" + "%" + " %,6.02f\n" +
                        "  Other VAT                   :          " + " %.04f" + "%" + " %,6.02f\n";
        String str23 = String.format(_str23, ratio[19], total_value[2] * ratio[19], ratio[20], total_value[4] * ratio[20]);
        String _str24 =
                "                                                ----------------\n" +
                        /*　　　　　　　　　　  　VALUE ADDED TAXの小計 */
                        "                                      SUB TOTAL" + " %,6.02f\n\n";
        String str24 = String.format(_str24, total_value[6]);


        String str25 =
                "----------------------------------------------------------------\n";
        String _str26 =
                /*現在の請求額*/
                "CURRENT BILL                                       Php" + " %,6.02f\n";
        String str26 = String.format(_str26, total_value[7]);
        String _str27 =
                /*各小計の合計の請求額*/
                "TOTAL AMOUNT                       Php" + " %,6.02f\n";
        String str27 = String.format(_str27, total_value[7]);
        String str28 =
                "================================================================\n";
        String _str29 =
                /*値引額*/
                "Discount                               " + " %,6.02f\n\n";
        String str29 = String.format(_str29, 111111.11f);
        String _str30 =
                /*合計の請求額から値引きされた金額*/
                "Amount Before Due                      " + " %,6.02f\n\n";
        String str30 = String.format(_str30, 111111.11f);
        String _str31 =
                /*利息額*/
                "Interest                               " + " %,6.02f\n\n";
        String str31 = String.format(_str31, 111111.11f);
        String _str32 =
                /*合計の請求額から利息額が追加された金額*/
                "Amount After Due                       " + " %,6.02f\n\n";
        String str32 = String.format(_str32, 111111.11f);

        String str33 =
                /*支払い期日　           月(Oct)　dd,yyyy　*/
                "DUE DATE:           " + "%s　%td,%tY\n" +
                        "DISCO DATE:         " + "%s　%td,%tY\n\n";

        String str34 =
                "NOTE:Please pay this electric bill on or before DUE DATE otherwise,\n" +
                        "     we will be forced to discontinue serving your electric needs.\n\n";
        String str35 =
                "This is not an Official Receipt.\n" +
                        "Payment of this bill does not mean payment of previous delinquencies if any.\n\n";
        String str36 =
                "             **PLEASE PRESENT THIS STATEMENT UPON PAYMENT**\n\n" +
                        /*検針担当：名前 　　　　　　　　　　検診日時 曜日(Thu) dd 月(Oct) yyyy　HH:mm:ss */
                        "Reader:%s                   " + "Thu 10 Oct 2024 11:39:33\n\n";
        String str37 =
                /*フォーマットのバージョン*/
                "Version : v1.00.1";

        mPrintService.write(WoosimCmd.PM_setPosition(0, 0));
        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str1.getBytes());
        mPrintService.write(str2.getBytes());
        mPrintService.write(str3.getBytes());
        mPrintService.write(str4.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str5.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str6.getBytes());
        mPrintService.write(str7.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str8.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str9.getBytes());
        mPrintService.write(str10.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str11.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str12.getBytes());
        mPrintService.write(str13.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str14.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str15.getBytes());
        mPrintService.write(str16.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str17.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str18.getBytes());
        mPrintService.write(str19.getBytes());
        mPrintService.write(str20.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str21.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str22.getBytes());
        mPrintService.write(str23.getBytes());
        mPrintService.write(str24.getBytes());
        mPrintService.write(str25.getBytes());
        mPrintService.write(str26.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str27.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
        mPrintService.write(str28.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str29.getBytes());
        mPrintService.write(str30.getBytes());
        mPrintService.write(str31.getBytes());
        mPrintService.write(str32.getBytes());


        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        mPrintService.write(str33.getBytes());

        mPrintService.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_SMALL));
        mPrintService.write(str34.getBytes());
        mPrintService.write(str35.getBytes());
        mPrintService.write(str36.getBytes());
        mPrintService.write(str37.getBytes());

        mPrintService.write(WoosimCmd.PM_printStdMode());
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

    private void connectDevice(Intent data, boolean secure) {
        String address = "1C:B8:57:50:01:D9";
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

        //       mPrintData = null;

        root_column = getString(R.string.table2_key) + "," +
                getString(R.string.table2_col1) + "," +
                getString(R.string.table2_col2) + "," +
                getString(R.string.table2_col3) + "," +
                getString(R.string.table2_col4) + "," +
                getString(R.string.table2_col5) + "," +
                getString(R.string.table2_col6) + "," +
                getString(R.string.table2_col7) + "," +
                getString(R.string.table2_col8) + "," +
                getString(R.string.table2_col9) + "," +
                getString(R.string.table2_col10) + "," +
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

        ratecsv = new CSVParser(folderExternal);
        if (!ratecsv.readFile("rate.csv")) {
            //ファイルがなかった場合の処理を後で考える。
        }


        ratio[0] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col1)));
        ratio[1] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col2)));
        ratio[2] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col3)));
        ratio[3] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col4)));
        ratio[4] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col5)));
        ratio[5] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col6)));
        ratio[6] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col7)));
        ratio[7] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col8)));
        ratio[8] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col9)));
        ratio[9] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col10)));
        ratio[10] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col11)));
        ratio[11] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col12)));
        ratio[12] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col13)));
        ratio[13] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col14)));
        ratio[14] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col15)));
        ratio[15] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col16)));
        ratio[16] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col17)));
        ratio[17] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col18)));
        ratio[18] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col19)));
        ratio[19] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col20)));
        ratio[20] = Float.parseFloat(ratecsv.Column(getString(R.string.table3_col21)));

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

    // The handler that gets information back from the BluetoothPrintService
    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, " onStart.");
        mPrintService = new BluetoothPrintService(mHandler);

        String address = "1C:B8:57:50:01:D9";
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mPrintService.connect(device, true);
//        mPrintService.start();
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
        if (mPrintService != null) {
            if (mPrintService.getState() == BluetoothPrintService.STATE_NONE) {
//                mPrintService.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, " onPause");
    }

    @Override
    protected void onDestroy() {
        if (mPrintService != null) mPrintService.stop();
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
                String csvfile = MainActivity.d.CurrentYearMonth() + "_meter.csv";
                String data = readFile(csvfile, folderExternal);
                if (data != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_SUBJECT, csvfile);
                    intent.putExtra(Intent.EXTRA_TEXT, data);
                    intent.setType("text/plain");
                    startActivity(intent);
                } else {
                    showToast("No data!!!");
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
        menu.findItem(R.id.menu_load).setVisible(false);
        menu.findItem(R.id.menu_save).setVisible(false);
        menu.findItem(R.id.menu_user).setVisible(false);
        menu.findItem(R.id.menu_share).setVisible(true);
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
    public final static int MSG_BILLING_RECORD = MSG_ENERGY_RECORD + 1;
    public final static int MSG_BREAKER = MSG_BILLING_RECORD + 1;
    public final static int MSG_ALERT_CLEAR = MSG_BREAKER + 1;
    public final static int MSG_CHANGE_THRESH = MSG_ALERT_CLEAR + 1;

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
            case MSG_BILLING_RECORD:
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
            case MSG_CHANGE_THRESH:
                mSel = 0;
                mParameter.setLength(0);
                mParameter.append("01010204128001120032110c1101");
                ret = 3;
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

            case MSG_BILLING_RECORD:
                ret = accessData(0, DLMS.IST_BILLING_PARAMS, 2, false);
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
            case MSG_CHANGE_THRESH:
                mDataIndex = 8;
                ret = accessData(1, DLMS.IST_DETECT, 2, false);
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