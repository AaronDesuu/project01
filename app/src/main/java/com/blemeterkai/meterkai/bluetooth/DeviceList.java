package com.blemeterkai.meterkai.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import androidx.annotation.RequiresPermission; // Make sure this import is present if not automatically added

import java.util.ArrayList;

// Adapter for holding devices found through scanning.
public class DeviceList {

    private final ArrayList<ScanDevice> mScanDevice;

    public DeviceList() {
        mScanDevice = new ArrayList<ScanDevice>();
    }

    public int size() {
        return mScanDevice.size();
    }

    public void addDevice(BluetoothDevice device, Integer rssi) {
        boolean find = false;
        for (int i = 0; i < mScanDevice.size(); i++) {
            // Check if device is not null before calling getAddress()
            if (Device(i) != null && Device(i).getAddress().equals(device.getAddress())) {
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
        if (position >= 0 && position < mScanDevice.size()) {
            return mScanDevice.get(position).Device();
        }
        return null; // Or throw an exception, depending on desired behavior
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
        if (Address == null) return -1;
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
        if (position >= 0 && position < mScanDevice.size() && mScanDevice.get(position).Device() != null) {
            return mScanDevice.get(position).Address();
        }
        return null; // Or handle appropriately
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public String Name(int position) {
        if (position >= 0 && position < mScanDevice.size() && mScanDevice.get(position).Device() != null) {
            return mScanDevice.get(position).Name();
        }
        return null; // Or handle appropriately
    }

    public void clear() {
        mScanDevice.clear();
    }

    public boolean Activate(int position) {
        if (position >= 0 && position < mScanDevice.size()) {
            return mScanDevice.get(position).Activate();
        }
        return false;
    }

    public boolean Check(int position) {
        if (position >= 0 && position < mScanDevice.size()) {
            return mScanDevice.get(position).Check();
        }
        return false;
    }

    public void Done(int position) {
        if (position >= 0 && position < mScanDevice.size()) {
            mScanDevice.get(position).Check(true);
        }
    }

    // Inner class ScanDevice
    // You can keep it as a public inner class, or make it a package-private top-level class
    // in the same file if it's only used by DeviceList.
    // For simplicity, keeping it as a public inner class is fine if it's tightly coupled.
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
            // Add null check for mDev
            return (mDev != null) ? mDev.getAddress() : null;
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        public String Name() {
            // Add null check for mDev
            return (mDev != null) ? mDev.getName() : null;
        }

        public int Rssi() {
            return mRssi;
        }

        public boolean Check() {
            return mChk;
        }

        public boolean Activate() {
            return Rssi() != -200;
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
}