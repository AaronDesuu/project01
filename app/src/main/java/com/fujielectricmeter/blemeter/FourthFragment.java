package com.fujielectricmeter.blemeter;

import static com.fujielectricmeter.blemeter.MainActivity.folderExternal;
import static com.fujielectricmeter.blemeter.MainActivity.oldcsv;
import static com.fujielectricmeter.blemeter.MainActivity.printercsv;
import static com.fujielectricmeter.blemeter.MainActivity.ratecsv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.fujielectricmeter.blemeter.databinding.FragmentFourthBinding;

import java.util.ArrayList;

public class FourthFragment extends ItemFragment {

    private FragmentFourthBinding binding;
    private static Integer mCnt = 0;
    private int mSelectButton;
    private AlertDialog.Builder builder;
    private int mRecordCount;
    public static PrintData mPrintData = new PrintData();

    private void buttonFunction(final int msg) {
        if (mSelectButton < 0) {
            if (mCallback.messageID() < 0) {
                final Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Integer ret = 0;
                        ret = mCallback.fragmentMessage(mSelectButton);
                        if (stopper) {
                            ret = -3;
                        }
                        if (ret > 0) {
                            handler.postDelayed(this, MainActivity.mTick);
                        } else {
                            stopper = true;
                            mSelectButton = -1;
                            mCallback.fragmentMessage(-1);
                            switch (ret) {
                                case -99:
                                    binding.textView.setText("Can't communicate with meter\nPlease check Power supply");
                                    break;
                                case -50:
                                    binding.textView.setText("Detect timeout");
                                    break;
                                case -5: /*abort*/
                                    break;
                                case -1:
                                    binding.textView.setText("Fail!");
                                    break;
                                case 0:
                                    break;
                                case -3:
                                case -2:
                                default:
                                    binding.textView.setText("Abort!(" + ret.toString() + ")");
                                    break;
                            }
                        }
                    }
                };
                mCnt = 0;
                stopper = false;
                mSelectButton = msg;
                handler.post(r);
            } else {
                mCallback.showToast("Before task is running. Please wait.");
            }
        } else {
            stopper = true;
        }
    }

    private String CreateData() {
        String data1, data2, data3, data4, data5, data6, data7, data8, data9;
        data1 = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
        data2 = MainActivity.secondcsv.Column(getString(R.string.table2_col4));
        data3 = MainActivity.secondcsv.Column(getString(R.string.table2_col5));
        data4 = MainActivity.secondcsv.Column(getString(R.string.table2_col6));
        data5 = MainActivity.secondcsv.Column(getString(R.string.table2_col7));
        data6 = MainActivity.secondcsv.Column(getString(R.string.table2_col8));
        data7 = MainActivity.secondcsv.Column(getString(R.string.table2_col9));
        data8 = MainActivity.secondcsv.Column(getString(R.string.table2_col10));
        data9 = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
        return new String(
                getString(R.string.table2_col2) + ":" + data1 + "\n" +
                        getString(R.string.table2_col4) + ":" + data2 + "\n" +
                        getString(R.string.table2_col5) + ":" + data3 + "\n" +
                        getString(R.string.table2_col6) + ":" + data4 + "\n" +
                        getString(R.string.table2_col7) + ":" + data5 + "\n" +
                        getString(R.string.table2_col8) + ":" + data6 + "\n" +
                        getString(R.string.table2_col9) + ":" + data7 + "\n" +
                        getString(R.string.table2_col10) + ":" + data8 + "\n" +
                        getString(R.string.table2_col11) + ":" + data9 + "\n"
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach.");
        Activity a = getActivity();
        if (a instanceof FourthFragment.messageManager == false) {
            throw new ClassCastException("Activity have to implement FourthFragment.messageManager");
        }
        mCallback = (FourthFragment.messageManager) a;
        builder = new AlertDialog.Builder(context);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i(TAG, "onCreateView.");
        mCallback.fragment(this);
        if (MainActivity.mSerialID != null) {
            MainActivity.mActionBar.setTitle(String.format("SerialID: %s", MainActivity.mSerialID));
        }
        binding = FragmentFourthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated.");
        MainActivity.mFragmentid = 4;
        mCallback.fragmentOrder(MainActivity.ODR_UPDATE);

        if (MainActivity.mSerialID == null) {
            NavHostFragment.findNavController(FourthFragment.this)
                    .navigate(R.id.action_FourthFragment_to_SecondFragment);
        } else {
            mCallback.fragmentOrder(MainActivity.ODR_SCAN_OFF);

            String csvfile = "registration.csv";
            if (MainActivity.fourthcsv != null) {
                if (!MainActivity.fourthcsv.Present().equals(csvfile)) {
                    MainActivity.fourthcsv.readFile(csvfile);
                } else {
                    MainActivity.fourthcsv.Reset();
                }
                MainActivity.Selection = 0;
            } else {
                MainActivity.fourthcsv = new CSVParser(MainActivity.folderExternal);
                if (MainActivity.fourthcsv.exist(csvfile)) {
                    MainActivity.fourthcsv.readFile(csvfile);
                } else {
                    CSVParser csv = new CSVParser("meter.csv", MainActivity.folderExternal);
                    MainActivity.fourthcsv.Copy(csv, csvfile, MainActivity.folderExternal);
                    MainActivity.fourthcsv.writeFile();
                }
            }
            MainActivity.fourthcsv.Find(getString(R.string.table2_key), MainActivity.msecondKey);

            String oldfile = MainActivity.d.PreviousYearMonth() + "_meter.csv";
            MainActivity.oldcsv = new CSVParser(folderExternal);
            if (!MainActivity.oldcsv.exist(oldfile)) {
                MainActivity.oldcsv.readFile("registration.csv");
            } else {
                MainActivity.oldcsv.readFile(oldfile);
            }
            MainActivity.oldcsv.Find(getString(R.string.table2_key), MainActivity.msecondKey);
            mPrintData.old_value[0] = MainActivity.oldcsv.Column(getString(R.string.table2_col4));
            mPrintData.old_value[1] = MainActivity.oldcsv.Column(getString(R.string.table2_col5));


            mSelectButton = -1;
            stopper = true;
            if (MainActivity.getLevel() < 3) {
                binding.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button1);
                        buttonFunction(MainActivity.MSG_SETUP);
                        MainActivity.trail.operation("MSG_SETUP button");
                    }
                });
                binding.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setAnime(binding.button2);
                        if (ratecsv.size() > 0 && printercsv.size() > 0 && !mPrintData.old_value[1].isEmpty()) {
                            String val = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
                            if (val.isEmpty()) {
                                binding.textView.setText("Communicating...");
                                buttonFunction(MainActivity.MSG_READER);
                            } else {
                                String[] now_value = {"", "", "", ""};
                                mPrintData.now_value[0] = MainActivity.secondcsv.Column(getString(R.string.table2_col11));  /*read date*/
                                mPrintData.now_value[1] = MainActivity.secondcsv.Column(getString(R.string.table2_col4));  /*fixed date*/
                                mPrintData.now_value[2] = MainActivity.secondcsv.Column(getString(R.string.table2_col5));  /*Imp*/
                                mPrintData.now_value[3] = MainActivity.secondcsv.Column(getString(R.string.table2_col7));  /*Imp Max*/
                                MainActivity.printImageText(mPrintData.now_value, mPrintData.old_value);
                            }
                        }
                        MainActivity.trail.operation("MSG_READER button");
                    }
                });
                binding.button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button3);
                        buttonFunction(MainActivity.MSG_ENERGY_RECORD);
                        MainActivity.trail.operation("MSG_ENERGY_RECORD button");
                    }
                });
                binding.button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button4);
                        buttonFunction(MainActivity.MSG_EVENT_RECORD);
                        MainActivity.trail.operation("MSG_EVENT_RECORD button");
                    }
                });
                binding.button5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button5);
                        buttonFunction(MainActivity.MSG_BILLING_RECORD);
                        MainActivity.trail.operation("MSG_EVENT_RECORD button");
                    }
                });
                binding.button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button6);
                        buttonFunction(MainActivity.MSG_SET_CLOCK);
                        MainActivity.trail.operation("MSG_SET_CLOCK button");
                    }
                });
                if (Integer.parseInt(MainActivity.rootcsv.Column(getString(R.string.table2_col1))) > 0) {
                    binding.button1.setEnabled(false);
                } else {
                    binding.button2.setEnabled(false);
                    binding.button3.setEnabled(false);
                    binding.button4.setEnabled(false);
                    binding.button5.setEnabled(false);
                }
            } else {
                binding.button1.setText(R.string.current_read);
                binding.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.textView.setText("Communicating...");
                        setAnime(binding.button1);
                        buttonFunction(MainActivity.MSG_READER);
                        MainActivity.trail.operation("MSG_READER button");
                    }
                });
                binding.button2.setVisibility(View.INVISIBLE);
                binding.button3.setVisibility(View.INVISIBLE);
                binding.button4.setVisibility(View.INVISIBLE);
                binding.button5.setVisibility(View.INVISIBLE);
                binding.button6.setVisibility(View.INVISIBLE);
            }
        }
//        MainActivity.mPrintService.start();
//        MainActivity.printImageText();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopper = true;
    }

    @Override
    public int DataArrived(final ArrayList<String> in, final boolean last) {
        int ret = 0;
        if (stopper) {
            return ret;
        }
        super.DataArrived(in, last);
        if (last) {
            switch (mSelectButton) {
                case MainActivity.MSG_READER:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            if (mTemp.size() > 1) {
                                if (!mTemp.get(1).equals("success (0)")) {
                                    ret = -5;
                                } else {
                                    binding.textView.setText("Demand reset success");
                                }
                            } else {
                                ret = -5;
                            }
                            break;
                        case 4:
                            if (mTemp.size() > 1) {
                                binding.textView.setText("Getting billing data...");
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
                                MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(2))), getString(R.string.table2_col5));
                                MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(3))), getString(R.string.table2_col6));
                                MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(6))), getString(R.string.table2_col7));
                                MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(7))), getString(R.string.table2_col8));
                                MainActivity.secondcsv.Update(String.format("%.3f", MainActivity.d.Float(100.0, mTemp.get(8))), getString(R.string.table2_col9));
                                MainActivity.secondcsv.Update(mTemp.get(9), getString(R.string.table2_col10));
                                MainActivity.secondcsv.Update(mTemp.get(0), getString(R.string.table2_col11));
                                MainActivity.secondcsv.writeFile();
                                binding.textView.setText("Success to get billing data. finish");
                                String[] now_value = {"", "", "", ""};
                                mPrintData.now_value[0] = mTemp.get(0);  /*read date*/
                                mPrintData.now_value[1] = mTemp.get(1);  /*fixed date*/
                                mPrintData.now_value[2] = mTemp.get(2);  /*Imp*/
                                mPrintData.now_value[3] = mTemp.get(6);  /*Imp Max*/
                                MainActivity.printImageText(mPrintData.now_value, mPrintData.old_value);

                            } else {
                                ret = -5;
                            }
                            break;
                    }
                    break;
                case MainActivity.MSG_SETUP:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            if (mTemp.size() > 1) {
                                if (!mTemp.get(1).equals("success (0)")) {
                                    binding.textView.setText("Fail to set clock");
                                    ret = -5;
                                } else {
                                    binding.textView.setText("Success to set clock");
                                }
                            } else {
                                binding.textView.setText("Fail to set clock");
                                ret = -5;
                            }
                            break;
                        case 4:
                            if (mTemp.size() > 1) {
                                if (!mTemp.get(1).equals("success (0)")) {
                                    binding.textView.append("\nFail to call demand reset");
                                    ret = -5;
                                } else {
                                    binding.textView.append("\nSuccess to call demand reset");
                                }
                            } else {
                                binding.textView.append("\nFail to call demand reset");
                                ret = -5;
                            }
                            break;
                        case 6:
                            if (mTemp.size() > 1) {
                                mRecordCount = Integer.parseInt(mTemp.get(1));
                                MainActivity.CounterParameter.setLength(0);
                                MainActivity.CounterParameter.append(String.format("020406%08x06%08x120001120000", mRecordCount, mRecordCount));
                            } else {
                                binding.textView.append("\nFail to get count of billing data");
                                ret = -5;
                            }
                            break;
                        case 8:
                            if (mTemp.size() > 9) {
                                MainActivity.fourthcsv.Update(mTemp.get(1), getString(R.string.table2_col4));
                                MainActivity.fourthcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(2))), getString(R.string.table2_col5));
                                MainActivity.fourthcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(3))), getString(R.string.table2_col6));
                                MainActivity.fourthcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(6))), getString(R.string.table2_col7));
                                MainActivity.fourthcsv.Update(String.format("%.3f", MainActivity.d.Float(1000.0, mTemp.get(7))), getString(R.string.table2_col8));
                                MainActivity.fourthcsv.Update(String.format("%.3f", MainActivity.d.Float(100.0, mTemp.get(8))), getString(R.string.table2_col9));
                                MainActivity.fourthcsv.Update(mTemp.get(9), getString(R.string.table2_col10));
                                MainActivity.fourthcsv.Update(mTemp.get(0), getString(R.string.table2_col11));
                                MainActivity.rootcsv.Update("1", getString(R.string.table2_col1));
                                MainActivity.secondcsv.Update("1", getString(R.string.table2_col1));
                                MainActivity.rootcsv.writeFile();
                                MainActivity.secondcsv.writeFile();
                                MainActivity.fourthcsv.writeFile();
                                binding.textView.append("\nSuccess to register meter\nFinish!");
                            } else {
                                binding.textView.append("\nFail to register meter\nFinish!");
                                ret = -5;
                            }
                            break;
                    }
                    break;
                case MainActivity.MSG_CHANGE_THRESH:
                    if (mTemp.size() > 1) {
                        if (!mTemp.get(1).equals("success (0)")) {
                            binding.textView.setText("Fail to change thresh value1");
                            ret = -5;
                        } else {
                            binding.textView.setText("Success to set thresh value1");
                        }
                    } else {
                        binding.textView.setText("Success to set thresh value1");
                        ret = -5;
                    }
                    break;
                case MainActivity.MSG_ENERGY_RECORD:
                    if (mTemp.size() > 5) {
                        String timestamp = mTemp.get(0).replace("/", "");
                        timestamp = timestamp.replace(":", "");
                        timestamp = timestamp.replace(" ", "_");
                        String filename = MainActivity.mSerialID + "_LP_" + timestamp + ".csv";
                        mTemp.remove(0);
                        CSVParser csv = new CSVParser(filename, MainActivity.folderExternal);
                        csv.New("Clock,Status,AveVolt[V],BlockImp[kW],BlockExp[kW]");
                        csv.Add(mTemp);
                        csv.writeFile();
                        binding.textView.setText("Success to get and save load profile records to file.");
                    } else {
                        binding.textView.setText("Fail to get and save load profile records");
                        ret = -5;
                    }
                    break;
                case MainActivity.MSG_EVENT_RECORD:
                    if (mTemp.size() > 3) {
                        String timestamp = mTemp.get(0).replace("/", "");
                        timestamp = timestamp.replace(":", "");
                        timestamp = timestamp.replace(" ", "_");
                        String filename = MainActivity.mSerialID + "_EV_" + timestamp + ".csv";
                        mTemp.remove(0);
                        CSVParser csv = new CSVParser(filename, MainActivity.folderExternal);
                        csv.New("Clock,Event,Volt[V]");
                        csv.Add(mTemp);
                        csv.writeFile();
                        binding.textView.setText("Success to get and save event records to file.");
                    } else {
                        binding.textView.setText("Fail to get and save event records");
                        ret = -5;
                    }
                    break;
                case MainActivity.MSG_BILLING_RECORD:
                    if (mTemp.size() > 9) {
                        String timestamp = mTemp.get(0).replace("/", "");
                        timestamp = timestamp.replace(":", "");
                        timestamp = timestamp.replace(" ", "_");
                        String filename = MainActivity.mSerialID + "_BL_" + timestamp + ".csv";
                        mTemp.remove(0);
                        CSVParser csv = new CSVParser(filename, MainActivity.folderExternal);
                        csv.New("Clock,Imp[kWh],Exp[kWh],Abs[kWh],Net[kWh],ImpMaxDemand[W],ExpMaxDemand[W],MinVolt[V],Alert");
                        csv.Add(mTemp);
                        csv.writeFile();
//                        binding.textView.setText("Clock:"+mTemp.get(27)+"\nImp[kWh]:"+String.format("%.3f", MainActivity.d.Float(1000.0,mTemp.get(28)))+"\nExp[kWh]:"+String.format("%.3f", MainActivity.d.Float(1000.0,mTemp.get(29)))+"\nAbs[kWh]:"+String.format("%.3f", MainActivity.d.Float(1000.0,mTemp.get(30)))+"\nNet[kWh]:"+String.format("%.3f", MainActivity.d.Float(1000.0,mTemp.get(31)))+"\nImpMaxDemand[W]"+mTemp.get(32)+"\nExpMaxDemand[W]"+mTemp.get(33)+"\nMinVolt[V]:"+String.format("%.2f", MainActivity.d.Float(100.0,mTemp.get(34)))+"\n\n\n\n\n");

                        //              binding.textView.setText("Success to get and save billing records to file.");
                    } else {
                        binding.textView.setText("Fail to get and save billing records");
                        ret = -5;
                    }
                    break;
                case MainActivity.MSG_SET_CLOCK:
                    if (mTemp.size() > 1) {
                        if (!mTemp.get(1).equals("success (0)")) {
                            binding.textView.setText("Fail to set clock");
                            ret = -5;
                        } else {
                            binding.textView.setText("Success to set clock");
                        }
                    } else {
                        binding.textView.setText("Fail to set clock");
                        ret = -5;
                    }
                    break;
            }
            mTemp.clear();
        } else {
            mCnt++;
            binding.textView.setText("Getting " + mCnt.toString() + " Blocks");
        }
        return ret;
    }
}