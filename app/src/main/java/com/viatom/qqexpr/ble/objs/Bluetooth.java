package com.viatom.qqexpr.ble.objs;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Bluetooth implements Parcelable {

    public static final String BT_NAME_O2 = "O2";
    public static final String BT_NAME_SNO2 = "O2BAND";
    public static final String BT_NAME_SPO2 = "SleepO2";
    public static final String BT_NAME_O2RING = "O2Ring";
    public static final String BT_NAME_WEARO2 = "WearO2";
    public static final String BT_NAME_SLEEPU = "SleepU";
    public static final String BT_NAME_ER1 = "VBeat";
    public static final String BT_NAME_ER1_N = "ER1";
    public static final String BT_NAME_ER2 = "DuoEK";
    public static final String BT_NAME_PULSEBIT_EX = "Pulsebit";
    public static final String BT_NAME_OXY_LINK = "Oxylink";
    public static final String BT_NAME_KIDS_O2 = "KidsO2";
    public static final String FETAL_DEVICE_NAME = "MD1000AF4";//7 OEM
    public static final String BT_NAME_BABY_O2 = "BabyO2";
    public static final String BT_NAME_OXY_SMART = "OxySmart";
    public static final String BT_NAME_TV221U = "VTM 20F";//4 OEM
    public static final String BT_NAME_PC100 = "PC-100:";//5 OEM
    public static final String BT_NAME_PC60FW = "PC-60F_SN";//6 OEM
    public static final String BT_NAME_AOJ20A = "AOJ-20A";//2 OEM
    public static final String BT_NAME_BP2 = "BP2";
    public static final String BT_NAME_OXYFIT = "Oxyfit";
    public static final String BT_NAME_VCOMIN = "VCOMIN";//3 OEM
    public static final String BT_NAME_CHECK_POD = "Checkme Pod";
    public static final String BT_NAME_BP2A = "BP2A";
    public static final String BT_NAME_BODY_FAT = "Viatom";//1 OEM
    private static final String BABYTONE = "Babytone";
    public static final String DEVICE_NAME_BODY_FAT = "Body Fat";
    public static final String BT_NAME_O2M = "O2M";
    public static final String BT_NAME_CHECKME_O2M = "Checkme O2 Max";
    public static final String BT_NAME_BPM = "BPM-188";
    public static final String BT_NAME_BPM_B02 = "BPM-B02";

    private static final String BPM_PRODUCT_NAME = " B02T";

    public static final int MODEL_UNRECOGNIZED = 0;
    public static final int MODEL_CHECKO2 = 1;
    public static final int MODEL_SNOREO2 = 2;
    public static final int MODEL_SLEEPO2 = 3;
    public static final int MODEL_O2RING = 4;
    public static final int MODEL_WEARO2 = 5;
    public static final int MODEL_SLEEPU = 6;
    public static final int MODEL_ER1 = 7;
    public static final int MODEL_ER2 = 8;
    public static final int MODEL_PULSEBITEX = 9;
    public static final int MODEL_OXYLINK = 10;
    public static final int MODEL_KIDSO2 = 11;
    public static final int MODEL_FETAL = 12;
    public static final int MODEL_BABYO2 = 13;
    public static final int MODEL_OXYSMART = 14;
    public static final int MODEL_TV221U = 15;
    public static final int MODEL_ER1_N = 16;
    public static final int MODEL_PC100 = 17;
    public static final int MODEL_AOJ20A = 18;
    public static final int MODEL_BP2 = 19;
    public static final int MODEL_OXYFIT = 20;
    public static final int MODEL_VCOMIN = 21;
    public static final int MODEL_CHECK_POD = 22;
    public static final int MODEL_BP2A = 23;
    public static final int MODEL_BODY_FAT = 24;
    //O2plus
    public static final int MODEL_O2M = 25;
    public static final int MODEL_BPM = 26;

    @IntDef({MODEL_CHECKO2, MODEL_SNOREO2, MODEL_SLEEPO2, MODEL_O2RING, MODEL_WEARO2, MODEL_SLEEPU, MODEL_ER1, MODEL_ER2, MODEL_PULSEBITEX,
            MODEL_OXYLINK, MODEL_KIDSO2, MODEL_FETAL, MODEL_BABYO2, MODEL_OXYSMART, MODEL_TV221U, MODEL_ER1_N, MODEL_PC100, MODEL_AOJ20A,
            MODEL_BP2, MODEL_OXYFIT, MODEL_VCOMIN, MODEL_CHECK_POD, MODEL_BP2A, MODEL_BODY_FAT, MODEL_O2M, MODEL_BPM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MODEL {
    }

    public @MODEL
    static int getDeviceModel(String deviceName) {
        if (deviceName.contains(BT_NAME_PC100))
            return Bluetooth.MODEL_PC100;
        return MODEL_UNRECOGNIZED;
    }

    public static final String[] DEVICE_MODEL_NAME = {"UNKNOW", "Checkme O2", "SnoreO2", "SleepO2", "O2Ring", "WearO2", "SleepU", "VBeat",
            "DuoEK", "Pulsebit EX", "Oxylink", "KidsO2", FETAL_DEVICE_NAME, BT_NAME_BABY_O2, BT_NAME_OXY_SMART, BT_NAME_TV221U, BT_NAME_ER1_N,
            BT_NAME_PC100, BT_NAME_AOJ20A, BT_NAME_BP2, BT_NAME_OXYFIT, BT_NAME_VCOMIN, BT_NAME_CHECK_POD, BT_NAME_BP2A, BT_NAME_BODY_FAT,BT_NAME_CHECKME_O2M, BT_NAME_BPM};

    public static final String[] DEVICE_PRODUCT_NAME = {"UNKNOW", "Checkme O2", "SnoreO2", "SleepO2", "O2Ring", "WearO2", "SleepU", "VisualBeat",
            "DuoEK", "Pulsebit EX", "Oxylink", "KidsO2", BABYTONE, BT_NAME_BABY_O2, BT_NAME_OXY_SMART, "FS20F", "ER1", "Spot-Check Monitor",
            "Infrared Thermometer", BT_NAME_BP2, BT_NAME_OXYFIT, BT_NAME_VCOMIN, BT_NAME_CHECK_POD, BT_NAME_BP2A, DEVICE_NAME_BODY_FAT, BT_NAME_CHECKME_O2M, BPM_PRODUCT_NAME};

    public static final String[] DEVICE_MODEL_NAME_INTERNAL = {"unknow", "ceo2", "snoreo2", "sleepo2", "o2ring", "wearo2", "sleepu", "er1",
            "er2", "pulsebit_ex", "oxylink", "kidso2", FETAL_DEVICE_NAME.toLowerCase(), BT_NAME_BABY_O2.toLowerCase(), BT_NAME_OXY_SMART,
            BT_NAME_TV221U, "er1_n", BT_NAME_PC100.toLowerCase(), BT_NAME_AOJ20A.toLowerCase(), BT_NAME_BP2.toLowerCase(),
            BT_NAME_OXYFIT.toLowerCase(), BT_NAME_VCOMIN.toLowerCase(), BT_NAME_CHECK_POD.toLowerCase(), BT_NAME_BP2A.toLowerCase(),
            DEVICE_NAME_BODY_FAT.toLowerCase(), BT_NAME_O2M.toLowerCase(), BT_NAME_BPM.toLowerCase()};

    @MODEL
    private int model;
    private String name;
    private BluetoothDevice device;
    private String macAddr;
    private int rssi;

    public Bluetooth(@MODEL int model, String name, BluetoothDevice device, int rssi) {
        this.model = model;
        this.name = name == null ? "" : name;
        this.device = device;
        this.macAddr = device.getAddress();
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bluetooth) {
            Bluetooth b = (Bluetooth) obj;
            return (this.macAddr.equals(b.getMacAddr()));
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(model);
        out.writeString(name);
        out.writeParcelable(device, flags);
        out.writeString(macAddr);
        out.writeInt(rssi);
    }

    public static final Creator<Bluetooth> CREATOR = new Creator<Bluetooth>() {
        public Bluetooth createFromParcel(Parcel in) {
            return new Bluetooth(in);
        }

        public Bluetooth[] newArray(int size) {
            return new Bluetooth[size];
        }
    };

    private Bluetooth(Parcel in) {
        model = in.readInt();
        name = in.readString();
        device = in.readParcelable(Bluetooth.class.getClassLoader());
        macAddr = in.readString();
        rssi = in.readInt();
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @MODEL
    public int getModel() {
        return model;
    }

    public void setModel(@MODEL int model) {
        this.model = model;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
