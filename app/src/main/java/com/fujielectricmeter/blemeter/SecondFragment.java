package com.fujielectricmeter.blemeter;

import static com.fujielectricmeter.blemeter.MainActivity.folderExternal;

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

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.fujielectricmeter.blemeter.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class SecondFragment extends ItemFragment {

    public FragmentSecondBinding binding;
    private AlertDialog.Builder builder;
    private int mPosition;
    private int mState;
    private int mRecordCount;
    private boolean abort = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "- onAttach.");
        Activity a = getActivity();
        if (a instanceof SecondFragment.messageManager == false) {
            throw new ClassCastException("Activity have to implement SecondFragment.messageManager");
        }
        mCallback = (SecondFragment.messageManager) a;
        builder = new AlertDialog.Builder(context);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i(TAG, TAG + "- onCreateView.");
        mCallback.fragment(this);
        if (MainActivity.mfirstKey != null) {
            MainActivity.mActionBar.setTitle(MainActivity.mfirstName);
        }
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void updateList() {

        int i = 0, activate;
        int color, current;
        String UID, now, mac, date, serial;

        current = mPosition;
        if (current < 0) {
            if(binding.secondlist.getCount()>0) {
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, TAG + "- onViewCreated");
        MainActivity.mFragmentid = 2;
        mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
        mCallback.fragmentOrder(MainActivity.ODR_SCAN_ON);
        mPosition = -1;
        mState = -1;
        stopper = true;
        MainActivity.msecondName = null;
        MainActivity.msecondKey = null;
        MainActivity.mSerialID = null;
        MainActivity.mAddress = null;

        String csvfile = MainActivity.d.CurrentYearMonth() + "_meter.csv";
        if (MainActivity.secondcsv != null) {
            if (!MainActivity.secondcsv.Present().equals(csvfile)) {
                MainActivity.secondcsv.readFile(csvfile);
                MainActivity.Selection = 0;
            } else {
                MainActivity.secondcsv.Reset();
            }
        } else {
            MainActivity.secondcsv = new CSVParser(folderExternal);
            if (MainActivity.secondcsv.exist(csvfile)) {
                MainActivity.secondcsv.readFile(csvfile);
            } else {
                CSVParser csv = new CSVParser("meter.csv", MainActivity.folderFiles);
                MainActivity.secondcsv.Copy(csv, csvfile, folderExternal);
                MainActivity.secondcsv.writeFile();
            }
            MainActivity.Selection = 0;
        }
        updateList();

        String oldfile= "122024_meter.csv";
        MainActivity.oldcsv=new CSVParser(folderExternal);
        if (!MainActivity.oldcsv.exist(oldfile)){
            MainActivity.oldcsv.readFile("registration.csv");
        } else {
            MainActivity.oldcsv.readFile(oldfile);
        }
        MainActivity.old_value[0]=MainActivity.oldcsv.Column(getString(R.string.table2_col11));
        MainActivity.old_value[1]=MainActivity.oldcsv.Column(getString(R.string.table2_col5));
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
            NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FourthFragment);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void batch() {
        if (stopper) {
            binding.secondlist.setOnItemClickListener(null);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    int ret = 0;
                    switch (mState) {
                        case -1:
                            mCallback.showToast("Start!");
                            mPosition = -1;
                            handler.postDelayed(this, MainActivity.mTick);
                            mState++;
                            break;
                        case 0:
                            while (!abort) {
                                if (++mPosition < MainActivity.secondcsv.size()) {
                                    if (mPosition > 3) {
                                        MainActivity.Selection = mPosition - 3;
                                    } else {
                                        MainActivity.Selection = 0;
                                    }
                                    MainActivity.msecondKey = MainActivity.secondcsv.Cell(mPosition, getString(R.string.table2_key));
                                    int activate = Integer.parseInt(MainActivity.secondcsv.Column(getString(R.string.table2_col1)));
                                    if (activate > 0) {
                                        String sid = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                                        String val = MainActivity.secondcsv.Column(getString(R.string.table2_col11));
                                        if (val.isEmpty()) {
                                            Log.i(TAG, String.format("Batch Check %d", mPosition));
                                            MainActivity.mSerialID = sid;
                                            MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                                            MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.mSerialID);
                                            handler.postDelayed(this, MainActivity.mTick);
                                            mState++;
                                            break;
                                        } else {
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
                                }
                                mState = -1;
                                mPosition = -1;
                                mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
                                mCallback.fragmentOrder(MainActivity.ODR_SCAN_ON);
                                mCallback.fragmentMessage(-1);
                            }
                            break;
                        case 1:
                            ret = mCallback.fragmentMessage(MainActivity.MSG_READER);
                            if(stopper){
                                mCallback.fragmentOrder(MainActivity.ODR_DISCONNECT);
                                ret = -3;
                            }
                            switch (ret) {
                                case 1:
                                case 2:
                                case 5:
                                    handler.postDelayed(this, MainActivity.mTick);
                                    break;
                                case 0:
                                    mState = 0;
                                    mCallback.showToast(MainActivity.mSerialID + " finish to read");
                                    updateList();
                                    handler.postDelayed(this, 1000);
                                    break;
                                case -6: /*Skip*/
                                case -50:
                                case -99:
                                    mState = 0;
                                    mCallback.showToast(String.format("Batch next %d",ret));
                                    Log.i(TAG, String.format("Batch next %d",ret));
                                    updateList();
                                    handler.postDelayed(this, MainActivity.mTick);
                                    break;
                                case -1:
                                case -2:
                                case -3:
                                case -5: /*abort*/
                                default:
                                    mState = 0;
                                    mCallback.showToast(String.format("Detect error %d",ret));
                                    handler.postDelayed(this, MainActivity.mTick);
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
            handler.post(r);
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
                        if(!mTemp.get(1).equals("success (0)")){
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
                        MainActivity.secondcsv.Update(mTemp.get(1),getString(R.string.table2_col4));
                        MainActivity.secondcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(2))),getString(R.string.table2_col5));
                        MainActivity.secondcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(3))),getString(R.string.table2_col6));
                        MainActivity.secondcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(6))),getString(R.string.table2_col7));
                        MainActivity.secondcsv.Update(String.format("%.3f",MainActivity.d.Float(1000.0, mTemp.get(7))),getString(R.string.table2_col8));
                        MainActivity.secondcsv.Update(String.format("%.3f",MainActivity.d.Float(100.0, mTemp.get(8))),getString(R.string.table2_col9));
                        MainActivity.secondcsv.Update(mTemp.get(9),getString(R.string.table2_col10));
                        MainActivity.secondcsv.Update(mTemp.get(0),getString(R.string.table2_col11));
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