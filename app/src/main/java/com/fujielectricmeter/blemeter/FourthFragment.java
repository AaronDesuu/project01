package com.fujielectricmeter.blemeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
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
import java.util.Date;
import java.util.Locale;

public class FourthFragment extends ItemFragment {

    private FragmentFourthBinding binding;
    private static int mCnt = 0;
    private int mSelectButton;
    private AlertDialog.Builder builder;
    private int mRecordCount;

    private void buttonFunction(final int msg) {
        if (mSelectButton < 0) {
            if (mCallback.messageID() < 0) {
                final Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        int ret = 0;
                        ret = mCallback.fragmentMessage(mSelectButton);
                        if (stopper) {
                            ret = -2;
                        }
                        if (ret > 0) {
                            handler.postDelayed(this, MainActivity.mTick);
                        } else {
                            stopper = true;
                            mSelectButton = -1;
                            mCallback.fragmentMessage(-1);
                            switch (ret) {
                                case -100:
                                    binding.textView.setText("Can't communicate with meter\nPlease check Power supply");
                                    break;
                                case -5: /*abort*/
                                    break;
                                case -1:
                                    MainActivity.mTemp.add("No data");
                                    MainActivity.mTemp.add("0");
                                    MainActivity.mTemp.add("0");
                                    binding.textView.setText("Fail!");
                                    break;
                                case 0:
                                    break;
                                case -2:
                                default:
                                    binding.textView.setText("Abort!");
                                    break;
                            }
                        }
                    }
                };
                mCallback.setInterval(false);
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
            String csvfile = MainActivity.d.CurrentYearMonth() + "_registration.csv";
            if (MainActivity.fourthcsv != null) {
                if (!MainActivity.fourthcsv.Present().equals(csvfile)) {
                    MainActivity.fourthcsv.readFile(csvfile);
                    MainActivity.Selection = 0;
                } else {
                    MainActivity.fourthcsv.Reset();
                }
            } else {
                MainActivity.fourthcsv = new CSVParser(MainActivity.folderExternal);
                if (MainActivity.fourthcsv.exist(csvfile)) {
                    MainActivity.fourthcsv.readFile(csvfile);
                } else {
                    CSVParser csv = new CSVParser("meter.csv", MainActivity.folderFiles);
                    MainActivity.fourthcsv.Copy(csv, csvfile, MainActivity.folderExternal);
                    MainActivity.fourthcsv.writeFile();
                }
            }
            MainActivity.fourthcsv.Find(getString(R.string.table2_col2),MainActivity.mSerialID);
            mCnt = 0;
            mSelectButton = -1;
            stopper = true;

            binding.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textView.setText("Communicating...");
                    setAnime(binding.button1);
                    buttonFunction(MainActivity.MSG_SETUP);
                    MainActivity.trail.operation("MSG_CHECKER button");
                }
            });
            binding.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textView.setText("Communicating...");
                    setAnime(binding.button2);
                    buttonFunction(MainActivity.MSG_ENERGY_RECORD);
                    MainActivity.trail.operation("MSG_CHECKER button");
                }
            });
            binding.button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textView.setText("Communicating...");
                    setAnime(binding.button3);
                    buttonFunction(MainActivity.MSG_EVENT_RECORD);
                    MainActivity.trail.operation("MSG_SETUP button");
                }
            });
            binding.button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textView.setText("Communicating...");
                    setAnime(binding.button4);
                    buttonFunction(MainActivity.MSG_SET_CLOCK);
                    MainActivity.trail.operation("MSG_SETUP button");
                }
            });
            if(Integer.parseInt(MainActivity.rootcsv.Column(getString(R.string.table2_col1)))>0){
                binding.button1.setEnabled(false);
            }
        }

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
                case MainActivity.MSG_SETUP:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            if (mTemp.size() > 1) {
                                if(!mTemp.get(1).equals("success (0)")){
                                    binding.textView.setText("Fail to set clock");
                                    ret = -5;
                                }
                                else{
                                    binding.textView.setText("Success to set clock");
                                }
                            } else {
                                binding.textView.setText("Fail to set clock");
                                ret = -5;
                            }
                            break;
                        case 4:
                            if (mTemp.size() > 1) {
                                if(!mTemp.get(1).equals("success (0)")){
                                    binding.textView.append("\nFail to call demand reset");
                                    ret = -5;
                                }
                                else{
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
                                MainActivity.fourthcsv.Update(mTemp.get(1),getString(R.string.table2_col4));
                                MainActivity.fourthcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(2))),getString(R.string.table2_col5));
                                MainActivity.fourthcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(3))),getString(R.string.table2_col6));
                                MainActivity.fourthcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(6))),getString(R.string.table2_col7));
                                MainActivity.fourthcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(7))),getString(R.string.table2_col8));
                                MainActivity.fourthcsv.Update(String.format("%.3f",MainActivity.d.Float(100.0, mTemp.get(8))),getString(R.string.table2_col9));
                                MainActivity.fourthcsv.Update(mTemp.get(9),getString(R.string.table2_col10));
                                MainActivity.fourthcsv.Update(mTemp.get(0),getString(R.string.table2_col11));
                                MainActivity.rootcsv.Update("1",getString(R.string.table2_col1));
                                MainActivity.secondcsv.Update("1",getString(R.string.table2_col1));
                                MainActivity.rootcsv.writeFile();
                                MainActivity.secondcsv.writeFile();
                                MainActivity.fourthcsv.writeFile();
                                binding.textView.append("\nSuccess to register meter");
                            } else {
                                binding.textView.append("\nFail to register meter");
                                ret = -5;
                            }
                            break;
                    }
                    break;
                case MainActivity.MSG_ENERGY_RECORD:
                    if (mTemp.size() > 5) {
                        String timestamp = mTemp.get(0).replace("/","");
                        timestamp = timestamp.replace(":","");
                        timestamp = timestamp.replace(" ","_");
                        String filename = MainActivity.mSerialID + "_LP_"+ timestamp + ".csv";
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
                        String timestamp = mTemp.get(0).replace("/","");
                        timestamp = timestamp.replace(":","");
                        timestamp = timestamp.replace(" ","_");
                        String filename = MainActivity.mSerialID + "_EV_"+ timestamp + ".csv";
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
                case MainActivity.MSG_SET_CLOCK:
                    if (mTemp.size() > 1) {
                        if(!mTemp.get(1).equals("success (0)")){
                            binding.textView.setText("Fail to set clock");
                            ret = -5;
                        }
                        else{
                            binding.textView.setText("Success to set clock");
                        }
                    } else {
                        binding.textView.setText("Fail to set clock");
                        ret = -5;
                    }
                    break;
            }
            mTemp.clear();
        }
        return ret;
    }
}