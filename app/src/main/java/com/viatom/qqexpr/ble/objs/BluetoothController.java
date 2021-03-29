package com.viatom.qqexpr.ble.objs;

import android.util.Log;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class BluetoothController {
    private final static String TAG = "BLE";
    private final static String UPDATER = "Updater";

    public static ArrayList<Integer> getModelList() {
        return modelList;
    }

    public static ArrayList<Integer> getModelListForAdapter() {
        return modelList;
    }


    public static void setModelList(ArrayList<Integer> modelList) {
        BluetoothController.modelList = modelList;
    }

    private static ArrayList<String> connectedList = new ArrayList<String>();
    private static ArrayList<Bluetooth> bleDevices = new ArrayList<Bluetooth>();
    private static ArrayList<Bluetooth> connectedDevices = new ArrayList<Bluetooth>();
    private static ArrayList<Integer> modelList = new ArrayList<Integer>();
    private static ArrayList<ScanDeviceBean> scanMyList = new ArrayList<>();
    private static ArrayList<ScanDeviceBean> scanDefaultList = new ArrayList<>();

    public void setConnectedList(ArrayList<String> list) {
        connectedList = list;
    }

    synchronized public static boolean addDevice(Bluetooth b) {
        boolean needNotify = false;
        Log.d(TAG, b.getName() + " mac: " + b.getMacAddr());

        if (!bleDevices.contains(b)) {
            bleDevices.add(b);
            needNotify = true;
        }

        return needNotify;
    }

    synchronized static public void clear() {
        bleDevices = new ArrayList<Bluetooth>();
        connectedDevices = new ArrayList<Bluetooth>();
        modelList = new ArrayList<Integer>();
        scanDefaultList = new ArrayList<>();
        scanMyList = new ArrayList<>();
    }

    synchronized public static ArrayList<Bluetooth> getDevices() {
        return bleDevices;
    }

    synchronized public static ArrayList<Bluetooth> getDevices(@Bluetooth.MODEL int model) {
        ArrayList<Bluetooth> list = new ArrayList<Bluetooth>();
        for (Bluetooth b : bleDevices) {
            if (b.getModel() == model) {
                list.add(b);
            }
        }
        return list;
    }

    synchronized public static ArrayList<Bluetooth> getMyDevices(@Bluetooth.MODEL int model) {
        ArrayList<Bluetooth> list = new ArrayList<Bluetooth>();
        for (ScanDeviceBean scanDeviceBean : scanMyList) {
            if (scanDeviceBean.getModel() == model && scanDeviceBean.getBluetooth() != null) {
                list.add(scanDeviceBean.getBluetooth());
            }
        }
        return list;
    }

    synchronized public static ArrayList<Bluetooth> getConnectedDevices() {
        return connectedDevices;
    }
    synchronized public static ArrayList<ScanDeviceBean> getScanDevices() {
        return scanMyList;
    } synchronized public static ArrayList<ScanDeviceBean> getScanDefaultDevices() {
        return scanDefaultList;
    }

    synchronized public static String getDeviceName(String address) {
        Optional<Bluetooth> optional = bleDevices.stream().filter(b -> b.getMacAddr().equals(address))
                .findFirst();
        return optional.map(Bluetooth::getName).orElse("");
    }
}
