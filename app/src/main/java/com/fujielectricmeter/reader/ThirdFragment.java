package com.fujielectricmeter.blemeter;

import static androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
//import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED;
import androidx.camera.mlkit.vision.MlKitAnalyzer;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.navigation.fragment.NavHostFragment;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fujielectricmeter.blemeter.databinding.FragmentThirdBinding;

public class ThirdFragment extends ItemFragment {

    private final static String TAG = ThirdFragment.class.getSimpleName();
    private FragmentThirdBinding binding;
    private messageManager mCallback;
    private Context mContext;
    BarcodeScanner barcodeScanner;
    ExecutorService cameraExecutor;
    private int quit = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,TAG+"onAttach.");
        Activity a = getActivity();
        if (a instanceof ThirdFragment.messageManager == false) {
            throw new ClassCastException("Activity have to implement ThirdFragment.messageManager");
        }
        mCallback = (ThirdFragment.messageManager) a;
        mContext = context;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mCallback.fragment(this);
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        MainActivity.mActionBar.setTitle(MainActivity.msecondName);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,TAG+"onViewCreated.");

        MainActivity.mFragmentid = 3;
        mCallback.fragmentOrder(MainActivity.ODR_UPDATE);

        String sid = MainActivity.secondcsv.Column(getString(R.string.table2_col2));
        PreviewView previewView = binding.viewFinder;
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        LifecycleCameraController cameraController = new LifecycleCameraController(mContext);
        cameraExecutor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                quit++;
                if (quit < 2) {
                    handler.postDelayed(this, 500);
                } else {
                    Log.i(TAG,"Third to Fourth");
                    NavHostFragment.findNavController(ThirdFragment.this)
                            .navigate(R.id.action_ThirdFragment_to_FourthFragment);
                }
            }
        };
        MlKitAnalyzer analyzer = new MlKitAnalyzer(
                List.of(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                cameraExecutor,
                result -> {
                    List barcodeResults = result.getValue(barcodeScanner);
                    if ((barcodeResults == null) ||
                            (barcodeResults.size() == 0) ||
                            (barcodeResults.get(0) == null)
                    ) {
                        previewView.getOverlay().clear();
                        if (MainActivity.mSerialID != null) {
                            MainActivity.trail.operation(MainActivity.mSerialID + "," + MainActivity.mAddress);
                            cameraExecutor.shutdownNow();
                            barcodeScanner.close();
                            handler.post(r);
                        }
                    } else {
                        if (MainActivity.mSerialID == null) {
                            QrCodeViewModel qrCodeViewModel = new QrCodeViewModel((Barcode) barcodeResults.get(0));
                            QrCodeDrawable qrCodeDrawable = new QrCodeDrawable(qrCodeViewModel);
                            previewView.getOverlay().clear();
                            previewView.getOverlay().add(qrCodeDrawable);
                            Barcode barcode = (Barcode) barcodeResults.get(0);
                            String val = barcode.getRawValue().toString();
                            if (val.contains("F6")) {
                                MainActivity.mSerialID = val.substring(9, 15);
                            }
                        }
                    }
                });
               cameraController.setImageAnalysisAnalyzer(cameraExecutor, analyzer);
               cameraController.bindToLifecycle(this);
               previewView.setController(cameraController);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
          if (MainActivity.mSerialID == null || MainActivity.mAddress == null) {
              cameraExecutor.shutdown();
              barcodeScanner.close();
          }
    }

}
