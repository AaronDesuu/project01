package com.fujielectricmeter.blemeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
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

    @Override
    public void batch() {
        if (stopper) {
            binding.secondlist.setOnItemClickListener(null);
            binding.secondlist.setOnItemLongClickListener(null);
            mCallback.fragmentOrder(MainActivity.ODR_SCAN_OFF);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    int ret = 0;
                    switch (mState) {
                        case -1:
                            mCallback.showToast("スタート！");
                            mPosition = 0;
                            handler.postDelayed(this, MainActivity.mTick);
                            mState++;
                            break;
                        case 0:
                            while (!stopper) {
                                if (mPosition < MainActivity.secondcsv.size()) {
                                    if (mPosition > 3) {
                                        MainActivity.Selection = mPosition - 3;
                                    } else {
                                        MainActivity.Selection = 0;
                                    }
                                    MainActivity.msecondKey = MainActivity.secondcsv.Cell(mPosition, getString(R.string.table2_key));
                                    MainActivity.msecondName = MainActivity.secondcsv.Column(getString(R.string.table2_col1));
                                    String val = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
                                    if (val.isEmpty()) {
                                        Log.i(TAG, String.format("Batch Check %d", mPosition));
                                        String sid = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                                        MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.msecondName);
                                        MainActivity.mSerialID = sid;
                                        MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                                        setDateRangeParameter();
                                        handler.postDelayed(this, MainActivity.mTick);
                                        mState++;
                                        invalidate();
                                        break;
                                    } else {
                                        mPosition++;
                                        Log.i(TAG, String.format("Batch Skip %d", mPosition));
                                    }
                                } else {
                                    stopper = true;
                                }
                            }
                            if (stopper) {
                                MainActivity.secondcsv.writeFile();
                                if (mPosition < MainActivity.secondcsv.size()) {
                                    mCallback.showToast("中止しました。");
                                    Log.i(TAG, "Batch abort");
                                } else {
                                    mCallback.showToast("終了！");
                                    Log.i(TAG, "Batch Finish");
                                }
                                mState = -1;
                                mPosition = -1;
                                mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
                                mCallback.fragmentOrder(MainActivity.ODR_SCAN_ON);
                                invalidate();
                            }
                            break;
                        case 1:
                            ret = mCallback.fragmentMessage(MainActivity.MSG_READER);
                            switch (ret) {
                                case 0:
                                    mCallback.showToast(MainActivity.msecondName + "を検針しました。");
                                case -100:
                                    mState = 0;
                                    invalidate();
                                    Log.i(TAG, "Batch next");
                                    mPosition++;
                                    break;
                                case -1:
                                case -2:
                                case -5: /*abort*/
                                    mState = 0;
                                    stopper = true;
                                    break;
                                default:
                                    break;
                            }
                            handler.postDelayed(this, MainActivity.mTick);
                            break;
                        default:
                            break;
                    }
                }
            };
            mCallback.fragmentOrder(MainActivity.ODR_UPDATE);
            mCallback.setInterval(false);
            stopper = false;
            handler.post(r);
        }
    }

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

        boolean create = true;

        String csvfile = "building-" + MainActivity.mfirstKey + ".csv";
        if (MainActivity.secondcsv != null) {
            if (!MainActivity.secondcsv.Present().equals(csvfile)) {
                MainActivity.mListItems.clear();
                MainActivity.secondcsv.readFile(csvfile);
                MainActivity.Selection = 0;
            } else {
                create = false;
                MainActivity.secondcsv.Reset();
            }
        } else {
            MainActivity.secondcsv = new CSVParser(csvfile, MainActivity.folderExternal);
        }
        int i = 0;
        int color;
        String UID, sid, now;
        if (mPosition < 0) {
            while (true) {
                UID = MainActivity.secondcsv.Row(getString(R.string.table2_key));
                if (UID == null) {
                    break;
                }
                sid = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                now = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
                if (sid.isEmpty() && now.isEmpty()) {
                    color = Color.RED;
                } else {
                    if (now.isEmpty()) {
                        color = Color.MAGENTA;
                        now = "------";
                    } else {
                        color = Color.BLUE;
                        now = String.format("%07.1f [kWh]", Float.parseFloat(now));
                    }
                }
                String b = String.format("%s:%s %s:%s",
                        getString(R.string.table2_col2), sid,
                        getString(R.string.table2_col21), now);
                if (create) {
                    SampleListItem item;
                    String a = MainActivity.secondcsv.Column(getString(R.string.table2_col1));
                    item = new SampleListItem(
                            a,
                            b,
                            UID,
                            color);
                    MainActivity.mListItems.add(item);
                } else {
                    MainActivity.mListItems.get(i).setColor(color);
                    MainActivity.mListItems.get(i).setContents(b);
                }
                i++;
            }
            binding.secondlist.setOnItemClickListener(onItemClickListener);
            binding.secondlist.setOnItemLongClickListener(onItemLongClickListener);
        } else {
            sid = MainActivity.secondcsv.Cell(mPosition, getString(R.string.table2_col2));
            now = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
            if (now.isEmpty()) {
                if (mState > 0) {
                    color = Color.RED;
                    now = "検針中...";
                } else {
                    color = Color.MAGENTA;
                    now = "------";
                }
            } else {
                color = Color.BLUE;
                now = String.format("%07.1f [kWh]", Float.parseFloat(now));
            }
            String b = String.format("%s:%s %s:%s",
                    getString(R.string.table2_col2), sid,
                    getString(R.string.table2_col21), now);
            MainActivity.mListItems.get(mPosition).setColor(color);
            MainActivity.mListItems.get(mPosition).setContents(b);
        }
        // レイアウトからリストビューを取得
        SampleListAdapter adapter = new SampleListAdapter(getActivity(), R.layout.custom_list, MainActivity.mListItems);
        binding.secondlist.setAdapter(adapter);
        binding.secondlist.setSelection(MainActivity.Selection);
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
        if (MainActivity.mfirstKey == null) {
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        } else {
            updateList();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        updateList();
        binding.secondlist.invalidate();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.Selection = position;
            Log.i(TAG, "Second to Forth");
            MainActivity.msecondKey = MainActivity.secondcsv.Cell(position, getString(R.string.table2_key));
            MainActivity.msecondName = MainActivity.secondcsv.Column(getString(R.string.table2_col1));
            MainActivity.mSerialID = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
            MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
            MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.msecondName);
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FourthFragment);
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mPosition = position;
            String val = MainActivity.secondcsv.Cell(position, getString(R.string.table2_col21));
            if (val.isEmpty()) {
                MainActivity.Selection = position;
                Log.i(TAG, "Second to Forth");
                MainActivity.msecondKey = MainActivity.secondcsv.Cell(position, getString(R.string.table2_key));
                MainActivity.msecondName = MainActivity.secondcsv.Column(getString(R.string.table2_col1));
                MainActivity.mSerialID = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
                MainActivity.mAddress = MainActivity.secondcsv.Column(getString(R.string.table2_col3));
                MainActivity.trail.operation(MainActivity.msecondKey + "," + MainActivity.msecondName);
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FourthFragment);
                return true;
            } else {
                MainActivity.trail.operation("検針値のクリア");
                builder.setTitle("検針値のクリア");
                builder.setMessage("検針値をクリアしますか？");
                builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Second to Third");
                        MainActivity.msecondKey = MainActivity.secondcsv.Cell(mPosition, getString(R.string.table2_key));
                        MainActivity.msecondName = MainActivity.secondcsv.Column(getString(R.string.table2_col1));
                        MainActivity.secondcsv.Update("", getString(R.string.table2_col18));
                        MainActivity.secondcsv.Update("", getString(R.string.table2_col20));
                        MainActivity.secondcsv.Update("", getString(R.string.table2_col21));
                        MainActivity.secondcsv.Update("", getString(R.string.table2_col23));
                        invalidate();
                    }
                });
                builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
                return true;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int DataArrived(final ArrayList<String> in, final boolean last) {
        int ret = 0;
        if (stopper) {
            return ret;
        }
        super.DataArrived(in, last);
        if (last) {
            ret = super.MessageBleMeter();
            mTemp.clear();
        }
        return ret;
    }
}