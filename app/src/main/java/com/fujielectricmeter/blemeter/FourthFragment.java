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
    private boolean mUpdate;
    private int mbreaker;
    private AlertDialog.Builder builder;

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
                            binding.button1.setEnabled(true);
                            stopper = true;
                            mSelectButton = -1;
                            mCallback.fragmentMessage(-1);
                            switch (ret) {
                                case -100:
                                    binding.textView.setText("通信装置に接続できません。\n　メーターの電源が入っているか\n　モジュラが正しく挿入されているか\n確認してください。");
                                    break;
                                case -5: /*abort*/
                                    break;
                                case -1:
                                    MainActivity.mTemp.add("No data");
                                    MainActivity.mTemp.add("0");
                                    MainActivity.mTemp.add("0");
                                    binding.textView.setText("失敗しました。");
                                    break;
                                case 0:
                                    break;
                                case -2:
                                default:
                                    binding.textView.setText("中止しました。");
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
            MainActivity.mActionBar.setTitle(String.format("場所: %s 計器: %s",MainActivity.msecondName,MainActivity.mSerialID));
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
            MainActivity.mfirstKey = null;
            NavHostFragment.findNavController(FourthFragment.this)
                    .navigate(R.id.action_FourthFragment_to_SecondFragment);
        } else {
            mCallback.fragmentOrder(MainActivity.ODR_SCAN_OFF);
            mCnt = 0;
            mSelectButton = -1;
            stopper = true;
            String SerialID = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
            if (!MainActivity.mSerialID.equals(SerialID)) {
                MainActivity.secondcsv.Update(MainActivity.mSerialID, getString(R.string.table2_col2));
                MainActivity.secondcsv.Update(MainActivity.mAddress, getString(R.string.table2_col3));
                binding.textView.setText("場所と計器番号を関連付けました。");
                MainActivity.trail.operation(MainActivity.mSerialID + "," + MainActivity.mAddress + " を保存");
            } else {
                MainActivity.trail.operation(MainActivity.mSerialID + "," + MainActivity.mAddress + " を選択");
            }
            mUpdate = false;
            String Check = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
            if (Check.isEmpty()) {
                //               binding.button1.setEnabled(false);
                binding.button2.setEnabled(false);
                binding.button3.setEnabled(false);
                binding.button4.setEnabled(false);
//                binding.textView.setText("通信して計器状態を確認しています。");
//                MainActivity.CounterParameter.setLength(0);
//                buttonFunction(MainActivity.MSG_CHECKER);
            }
            binding.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnime(binding.button1);
                    binding.textView.setText("通信して計器状態を確認しています。");
                    buttonFunction(MainActivity.MSG_CHECKER);
                    MainActivity.trail.operation("MSG_CHECKER button");
                }
            });
            binding.button2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    builder.setTitle("開閉器の操作");
                    builder.setMessage("開閉器を操作して、負荷の切断／接続ますか？");
                    builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            binding.textView.setText("通信しています。");
                            setAnime(binding.button2);
                            buttonFunction(MainActivity.MSG_BREAKER);
                            MainActivity.trail.operation("MSG_BREAKER button");
                        }
                    });
                    builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                    return false;
                }
           });
            binding.button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textView.setText("通信しています。");
                    setAnime(binding.button3);
                    buttonFunction(MainActivity.MSG_SETUP);
                    MainActivity.trail.operation("MSG_SETUP button");
                }
            });
            binding.button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnime(binding.button4);
                    if (mUpdate) {
                        MainActivity.secondcsv.writeFile();
                        MainActivity.trail.result("ファイルに保存");
                    } else {
                        MainActivity.trail.result("ファイル更新なし");
                    }
                    MainActivity.trail.operation("Confirm button");
                    NavHostFragment.findNavController(FourthFragment.this)
                            .navigate(R.id.action_FourthFragment_to_SecondFragment);
                }
            });
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
                case MainActivity.MSG_CHECKER:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            Log.i(TAG,"MSG_CHECKER 2");
                            binding.textView.setText("タンパー検出を無効にしました。");
                            MainActivity.CounterParameter.setLength(0);
                            MainActivity.CounterParameter.append("0204020412000809060000010000ff0f02120000");
                            String order = MainActivity.secondcsv.Column(getString(R.string.table2_col15));
                            long from, to;
                            String datetime;
                            if (order.isEmpty()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                Date date = new Date();
                                datetime = sdf.format(date);
                                from = MainActivity.d.DatetimeToSec(
                                        String.format("%04d/%02d/%02d %02d:%02d:%02d",
                                                Integer.parseInt(datetime.substring(0, 4)),
                                                Integer.parseInt(datetime.substring(4, 6)),
                                                Integer.parseInt(datetime.substring(6, 8)),
                                                Integer.parseInt(datetime.substring(8, 10)),
                                                Integer.parseInt(datetime.substring(10, 12)),
                                                Integer.parseInt(datetime.substring(12, 14))
                                        ));
                                from -= from % 86400;
                            } else {
                                Integer year = Integer.parseInt(order.substring(0, 4));
                                Integer mon = Integer.parseInt(order.substring(4, 6));
                                Integer day = Integer.parseInt(order.substring(6, 8));
                                datetime = String.format("%04d/%02d/%02d 00:00:00", year, mon, day);
                                from = MainActivity.d.DatetimeToSec(datetime);
                            }
                            to = from + 86400 - 1800;
                            MainActivity.CounterParameter.append(String.format("19%s19%s", MainActivity.d.SecToRawDatetime(from), MainActivity.d.SecToRawDatetime(to)));
                            MainActivity.CounterParameter.append("0102020412000809060000010000ff0f02120000020412000309060100010800ff0f02120000");
                            break;
                        case 4:
                            Log.i(TAG,"MSG_CHECKER 4");
                            if (mTemp.size() > 1) {
                                int size = mTemp.size();
                                binding.textView.append(String.format("\n%s 時点の計量値 %.1f [kWh]",
                                        mTemp.get(size - 2).toString(),
                                        MainActivity.d.Float(10.0, mTemp.get(size - 1))));
                                        MainActivity.secondcsv.Update(String.format("%.1f",MainActivity.d.Float(10.0, mTemp.get(size - 1))), getString(R.string.table2_col16));
                            }
                            break;
                        case 6:
                            Log.i(TAG,"MSG_CHECKER 4");
                            if (mTemp.size() == 17) {
                                String format = "\n計器時刻: %s\n有効電力 順: %07.1f, 逆: %07.1f [kWh]\n電圧 1側: %s, 3側: %s [V]\n電流 1側: %s, 3側: %s [A]\n電力 順: %s, 逆: %s [kW]\nフリッカー状態: %s";
                                String out = String.format(format,
                                        mTemp.get(1),/*日時*/
                                        MainActivity.d.Float(10.0, mTemp.get(8)),/*順*/
                                        MainActivity.d.Float(10.0, mTemp.get(9)),/*逆*/
                                        mTemp.get(10),/*電圧1*/
                                        mTemp.get(11),/*電圧3*/
                                        mTemp.get(12),/*電流1*/
                                        mTemp.get(13),/*電流3*/
                                        MainActivity.d.Float(1000.0, mTemp.get(14)),/*電力1*/
                                        MainActivity.d.Float(1000.0, mTemp.get(15)),/*電力2*/
                                        MainActivity.d.arrange_boolean(new String("発生"), new String("停止"), mTemp.get(16)) /*フリッカー*/);
                                binding.textView.append(out);
                                MainActivity.trail.result(out);
                                long volt1, volt3;
                                volt1 = Long.parseLong(mTemp.get(10));
                                volt3 = Long.parseLong(mTemp.get(11));
                                if (volt1 < 80 || volt3 < 80 || volt1 > 120 || volt3 > 120) {
                                    if (volt1 < 80) {
                                        binding.textView.append("\n1側電圧が80V未満です。");
                                    }
                                    if (volt1 > 120) {
                                        binding.textView.append("\n1側電圧が120V超過です。");
                                    }
                                    if (volt3 < 80) {
                                        binding.textView.append("\n3側電圧が80V未満です。");
                                    }
                                    if (volt3 > 120) {
                                        binding.textView.append("\n3側電圧が120V超過です。");
                                    }
                                    binding.textView.append("\n施工状態を確認後、戻るボタンを押してバーコードを撮り直して下さい。");
                                } else {
                                    binding.textView.append("\n1側、3側の電圧は正常範囲内でした。\n端子カバーを閉じて、\n[初期設定]ボタンを押してください。");
                                    binding.button1.setEnabled(true);
                                    binding.button2.setEnabled(true);
                                    binding.button3.setEnabled(true);
                                }
                            }
                            break;
                    }
                    break;
                case MainActivity.MSG_MEASURE3:
                    if (mTemp.size() == 17) {
                        String format = "計器時刻: %s\n有効電力 順: %07.1f, 逆: %07.1f [kWh]\n電圧 1側: %s, 3側: %s [V]\n電流 1側: %s, 3側: %s [A]\n電力 順: %s, 逆: %s [kW]\nフリッカー状態: %s";
                        String out = String.format(format,
                                mTemp.get(1),/*日時*/
                                MainActivity.d.Float(10.0, mTemp.get(8)),/*順*/
                                MainActivity.d.Float(10.0, mTemp.get(9)),/*逆*/
                                mTemp.get(10),/*電圧1*/
                                mTemp.get(11),/*電圧3*/
                                mTemp.get(12),/*電流1*/
                                mTemp.get(13),/*電流3*/
                                MainActivity.d.Float(1000.0, mTemp.get(14)),/*電力1*/
                                MainActivity.d.Float(1000.0, mTemp.get(15)),/*電力2*/
                                MainActivity.d.arrange_boolean(new String("発生"), new String("停止"), mTemp.get(16)) /*フリッカー*/);
                        binding.textView.setText(out);
                        MainActivity.trail.result(out);
                        long volt1, volt3;
                        volt1 = Long.parseLong(mTemp.get(10));
                        volt3 = Long.parseLong(mTemp.get(11));
                        if (volt1 < 80 || volt3 < 80 || volt1 > 120 || volt3 > 120) {
                            if (volt1 < 80) {
                                binding.textView.append("\n1側電圧が80V未満です。");
                            }
                            if (volt1 > 120) {
                                binding.textView.append("\n1側電圧が120V超過です。");
                            }
                            if (volt3 < 80) {
                                binding.textView.append("\n3側電圧が80V未満です。");
                            }
                            if (volt3 > 120) {
                                binding.textView.append("\n3側電圧が120V超過です。");
                            }
                        } else {
                            binding.textView.append("\n\n端子カバーを閉じて、\n[初期設定]ボタンを押してください。");
                            if(MainActivity.getLevel()<2) {
                                binding.button2.setEnabled(true);
                            }
                            binding.button3.setEnabled(true);
                        }
                    }
                    break;
                case MainActivity.MSG_SETUP:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            binding.button4.setEnabled(false);
                            if (!mTemp.get(0).equals("object undefined (4)")) {
                                binding.textView.setText("端子カバーが空いている状態を検出しました。\n端子カバーを閉じてください");
                                MainActivity.trail.result("端子カバー開を検出");
                                ret = -5;
                            } else {
                                binding.textView.setText("端子カバーが閉じていることを検出しました。");
                                MainActivity.trail.result("端子カバー閉確認");
                            }
                            break;
                        case 4:
                            if (mTemp.size() == 2) {
                                mbreaker = Integer.parseInt(mTemp.get(1));
                                if(mbreaker==0) {
                                    binding.textView.append("\n開閉器が開いていることを確認しました。");
                                }else{
                                    binding.textView.append("\n開閉器が閉じていることを確認しました。");
                                }
                            } else {
                                binding.textView.append("\n開閉器の状態の取得に失敗しました。再度行ってください。");
                                ret = -5;
                            }
                            break;
                        case 6:
                            if (mTemp.size() == 2) {
                                binding.textView.append("\nタンパー検出を有効にしました。");
                                MainActivity.trail.result(String.format("フリッカー設定 %s", mTemp.get(1)));
                            } else {
                                binding.textView.append("\nタンパー検出を有効にできませんでした。再度行ってください。");
                                MainActivity.trail.result(String.format("フリッカー設定失敗 %s", mTemp.get(0)));
                                ret = -5;
                            }
                            break;
                        case 8:
                            if (mTemp.size() == 2) {
                                binding.textView.append("\n現在時刻に設定しました。");
                                MainActivity.trail.result(String.format("時刻設定 %s", mTemp.get(1)));
                            } else {
                                binding.textView.append("\n時刻設定に失敗しました。再度行ってください。");
                                MainActivity.trail.result(String.format("時刻設定失敗 %s", mTemp.get(0)));
                                ret = -5;
                            }
                            break;
                        case 10: /*measure*/
                            if (mTemp.size() == 17) {
                                MainActivity.secondcsv.Update(mTemp.get(0), getString(R.string.table2_col14));
                                String date = String.format("%s%s%s",
                                        mTemp.get(0).substring(0, 4),
                                        mTemp.get(0).substring(5, 7),
                                        mTemp.get(0).substring(8, 10));
                                String date2 = String.format("%s年%s月%s日",
                                        mTemp.get(0).substring(0, 4),
                                        mTemp.get(0).substring(5, 7),
                                        mTemp.get(0).substring(8, 10));
                                String value = String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(8)));
                                MainActivity.secondcsv.Update(date, getString(R.string.table2_col4));
                                MainActivity.secondcsv.Update(value, getString(R.string.table2_col6));/*順*/
                                binding.textView.append(String.format("\n 初期検針日%s\n 初期指示数%s [kWh]\nをファイルに保存しました。", date2, value));
                                //mTemp.get(10),/*電圧1*/
                                MainActivity.secondcsv.Update(mTemp.get(10), getString(R.string.table2_col7));
                                //mTemp.get(11),/*電圧3*/
                                MainActivity.secondcsv.Update(mTemp.get(11), getString(R.string.table2_col8));
                                //mTemp.get(14)),/*電力1*/
                                MainActivity.secondcsv.Update(mTemp.get(14), getString(R.string.table2_col9));
                                //mTemp.get(15)),/*電力2*/
                                MainActivity.secondcsv.Update(mTemp.get(15), getString(R.string.table2_col10));
                                MainActivity.secondcsv.Update(String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(9))), getString(R.string.table2_col11));
                                if (mbreaker == 0) {
                                    MainActivity.secondcsv.Update("切断", getString(R.string.table2_col12));
                                } else {
                                    MainActivity.secondcsv.Update("接続", getString(R.string.table2_col12));
                                }
                                MainActivity.secondcsv.Update("正常", getString(R.string.table2_col13));
                                mUpdate = true;
                            } else {
                                binding.textView.append("\n初回検針値を取得しファイルに失敗しました。");
                                MainActivity.trail.result(String.format("計量値読み出し失敗 %s", mTemp.get(0)));
                                ret = -5;
                            }
                            break;
                        case 12:/*record*/
                            if (mTemp.size() == 2) {
                                binding.textView.append("\nイベントログを初期化しました。");
                                binding.textView.append("\n[確定]ボタンを押して、次の作業を行ってください。");
                                MainActivity.trail.result(String.format("イベントレコードリセット %s", mTemp.get(1)));
                                binding.button4.setEnabled(true);
                            } else {
                                binding.textView.append("\nイベントログの初期化に失敗しました。再度行ってください。");
                                MainActivity.trail.result(String.format("イベントレコードリセット失敗 %s", mTemp.get(0)));
                                ret = -5;
                            }
                            break;
                    }
                    break;
                case MainActivity.MSG_BREAKER:
                    switch (MainActivity.mSubStage) {
                        case 2:
                            if (mTemp.size() == 2) {
                                mbreaker = Integer.parseInt(mTemp.get(1));
                                MainActivity.CounterParameter.setLength(0);
                                if (mbreaker == 0) {
                                    MainActivity.CounterParameter.append("1101");
                                } else {
                                    MainActivity.CounterParameter.append("1100");
                                }
                            } else {
                                binding.textView.setText("開閉器操作に失敗しました。");
                                ret = -5;
                            }
                            break;
                        case 4:
                            if (mTemp.size() == 2) {
                                if (mbreaker == 0) {
                                    mbreaker = 1;
                                    binding.textView.setText("開閉器を閉めました。");

                                } else {
                                    binding.textView.setText("開閉器を開けました。");
                                }
                            } else {
                                binding.textView.setText("開閉器操作に失敗しました。");
                                ret = -5;
                            }
                            break;
                    }
                    break;
            }
            mTemp.clear();
        }
        return ret;
    }
}