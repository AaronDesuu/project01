package com.fujielectricmeter.blemeter;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.fujielectricmeter.blemeter.databinding.FragmentSecondBinding;

import java.util.ArrayList;

public class ItemFragment extends Fragment {
    public interface messageManager {
        void fragment(ItemFragment fragment);
        int fragmentMessage(final int message_id);
        int fragmentOrder(final int order_id);
        int messageID();
        int setInterval(final boolean enable);
        void showToast(String text);
    }
    public final static String TAG = ItemFragment.class.getSimpleName();
    public boolean stopper = true;
    public Button mCurrentButton;
    public ArrayList<String> mTemp = new ArrayList<String>();
    public messageManager mCallback;
    public int DataArrived(final ArrayList<String> in, final boolean last) {
        mTemp.addAll(in);
        return 0;
    };
    public String getData() {
        return "";
    };
    public void invalidate(){
    }
    public void batch(){
    }
    public void interrupt() {
        stopper = true;
    }
    public boolean running() {
        return !stopper;
    }

    public void setAnime(Button btn) {
        mCurrentButton = btn;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        btn.startAnimation(animationSet);
    }
    public void setDateRangeParameter(){

        long from, to;

        MainActivity.CounterParameter.setLength(0);
        MainActivity.CounterParameter.append("0204020412000809060000010000ff0f02120000");
        String order = MainActivity.secondcsv.Column(getString(R.string.table2_col15));
        if (order.isEmpty()) {
            from = MainActivity.d.CurrentDatetimeSec();
            from -= from % 86400;
        } else {
            Integer year = Integer.parseInt(order.substring(0, 4));
            Integer mon = Integer.parseInt(order.substring(4, 6));
            Integer day = Integer.parseInt(order.substring(6, 8));
            String datetime = String.format("%04d/%02d/%02d 00:00:00", year, mon, day);
            from = MainActivity.d.DatetimeToSec(datetime);
        }
        to = from + 86400 - 1800;
        MainActivity.CounterParameter.append(String.format("19%s19%s", MainActivity.d.SecToRawDatetime(from), MainActivity.d.SecToRawDatetime(to)));
        MainActivity.CounterParameter.append("0102020412000809060000010000ff0f02120000020412000309060100010800ff0f02120000");
    }

    public int MessageBleMeter(){
        int ret = 0;
        switch (MainActivity.mSubStage) {
            case 2:
                if (mTemp.size() > 1) {
                    int size = mTemp.size();
                    String order = MainActivity.secondcsv.Column(getString(R.string.table2_col15));
                    if (!order.isEmpty()) {
                        String finish = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
                        if (finish.isEmpty()) {
                            String date = String.format("%s%s%s",
                                    mTemp.get(0).substring(0, 4),
                                    mTemp.get(0).substring(5, 7),
                                    mTemp.get(0).substring(8, 10));
                            MainActivity.secondcsv.Update(date, getString(R.string.table2_col18));
                            String from = MainActivity.secondcsv.Column(getString(R.string.table2_col19));
                            String to = String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(size - 1)));
                            Float val = Float.parseFloat(to) - Float.parseFloat(from);
                            MainActivity.secondcsv.Update(to, getString(R.string.table2_col20));
                            MainActivity.secondcsv.Update(String.format("%.1f", val), getString(R.string.table2_col21));
                            MainActivity.secondcsv.Update(mTemp.get(size - 2), getString(R.string.table2_col23));
                        }
                    }
                }
                Log.i(TAG, "MSG_CHECKER 2");
                break;
            case 4:
                if (mTemp.size() == 17) {
                    String finish = MainActivity.secondcsv.Column(getString(R.string.table2_col21));
                    if (finish.isEmpty()) {
                        String date = String.format("%s%s%s",
                                mTemp.get(0).substring(0, 4),
                                mTemp.get(0).substring(5, 7),
                                mTemp.get(0).substring(8, 10));
                        MainActivity.secondcsv.Update(date, getString(R.string.table2_col18));
                        String from = MainActivity.secondcsv.Column(getString(R.string.table2_col19));
                        String to = String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(8)));
                        Float val = Float.parseFloat(to) - Float.parseFloat(from);
                        MainActivity.secondcsv.Update(to, getString(R.string.table2_col20));
                        MainActivity.secondcsv.Update(String.format("%.1f", val), getString(R.string.table2_col21));
                        MainActivity.secondcsv.Update(mTemp.get(0), getString(R.string.table2_col23));
                    }
//                                String value = String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(8)));
//                                MainActivity.secondcsv.Update(value, getString(R.string.table2_col6));/*順*/
                    //mTemp.get(10),/*電圧1*/
//                                MainActivity.secondcsv.Update(mTemp.get(10), getString(R.string.table2_col7));
                    //mTemp.get(11),/*電圧3*/
//                                MainActivity.secondcsv.Update(mTemp.get(11), getString(R.string.table2_col8));
                    //mTemp.get(14)),/*電力1*/
//                                MainActivity.secondcsv.Update(mTemp.get(14), getString(R.string.table2_col9));
                    //mTemp.get(15)),/*電力2*/
//                                MainActivity.secondcsv.Update(mTemp.get(15), getString(R.string.table2_col10));
//                                MainActivity.secondcsv.Update(String.format("%.1f", MainActivity.d.Float(10.0, mTemp.get(9))), getString(R.string.table2_col11));
                } else {
                    MainActivity.trail.result(String.format("計量値読み出し失敗 %s", mTemp.get(0)));
                    ret = -5;
                }
                Log.i(TAG, "MSG_CHECKER 4");
                break;
            case 6:
                Log.i(TAG, "MSG_CHECKER 6");
                break;
            case 8:
                Log.i(TAG, "MSG_CHECKER 8");
                break;
        }
        return ret;
    }

}
