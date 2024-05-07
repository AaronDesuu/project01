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

        int Position(final String Address);

        int Rssi(final int position);

        void Print(final String data);

        void showToast(final String text);
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

}
