package com.viatom.qqexpr.ble.objs;

public class ScanDeviceBean {
    private int itemType;
    private int stickyName;
    private Bluetooth bluetooth;
    private int model;

    public ScanDeviceBean(int itemType, int stickyName) {
        this.itemType = itemType;
        this.stickyName = stickyName;
    }

    public ScanDeviceBean(int itemType, int stickyName, Bluetooth bluetooth) {
        this.itemType = itemType;
        this.stickyName = stickyName;
        this.bluetooth = bluetooth;
    }

    public ScanDeviceBean(int itemType, int stickyName, int model) {
        this.itemType = itemType;
        this.stickyName = stickyName;
        this.model = model;
    }


    public int getItemType() {
        return itemType;
    }

    public int getStickyName() {
        return stickyName;
    }

    public void setStickyName(int stickyName) {
        this.stickyName = stickyName;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

}
