package com.example.money.e_paper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class bluetooth_41 {
    Timer mTimer1;
    private BluetoothAdapter mBluetoothAdapter1;//our local adapter
    private static final long SCAN_PERIOD1 = 1000; //5 seconds
    private static List<BluetoothDevice> mDevices1 = new ArrayList<BluetoothDevice>();//discovered devices in range
    private BluetoothDevice mDevice1; //external BLE device (Grove BLE module)
    private static BluetoothGatt mBluetoothGatt1; //provides the GATT functionality for communication
    public static String DEVICE_NAME1 = "B";
   // private static final String DEVICE_NAME = "AORUS_X5";
    private static final String GROVE_SERVICE1 = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_TX1 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_RX1 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public boolean BLe_stus1 =false ,connect_stus1 =false;


//    private static final String DEVICE_NAME = "CC2650 SensorTag";
//    private static final String GROVE_SERVICE = "f000aa80-0451-4000-b000-0000000000000";
//    private static final String CHARACTERISTIC_TX = "f000aa82-0451-4000-b000-00000000000";
//    private static final String CHARACTERISTIC_RX = "f000aa81-0451-4000-b000-00000000000";

    public static BluetoothGattService mBluetoothGattService1; //service on mBlueoothGatt
    private Activity activity1;

   public String rssi_string2;
    public int rssi_int2;
    BluetoothDevice device1;
    public  boolean B_disable =false;
    public bluetooth_41(Activity activity1, Activity activity) {


    }

//    public bluetooth_40(Integer[] a, int[] b, ImageView[] imageView, Integer[] imageView1, ImageView imageView2) {
//        this.a = a;
//        this.b = b;
//        this.imageView = imageView;
//        this.imageView1 = imageView1;
//        this.imageView2 = imageView2;
//    }

    public bluetooth_41(Activity activity) {
        this.activity1 = activity;
        Log.e("money","one1");
        final BluetoothManager mBluetoothManager = (BluetoothManager) activity.getSystemService(activity.BLUETOOTH_SERVICE);
        mBluetoothAdapter1 = mBluetoothManager.getAdapter();
        searchForDevices();




    }


    public static void bluetoothset(String blumessage) {
        sendMessage1(blumessage);
    }



    private static void statusUpdate(final String msg) {
        new Runnable() {
            @Override
            public void run() {
                Log.w("BLE", msg);
            }
        };

    }


    private void searchForDevices() {
        mTimer1 = new Timer();
        scanLeDevice();
        mTimer1.schedule(new TimerTask() {
            @Override
            public void run() {
                findGroveBLE();
                //Log.e("money","two");
            }
        }, SCAN_PERIOD1);


    }
//----------------rssi

    public static void sendMessage1(String _msg) {
        if (mBluetoothGattService1 == null)
            return;
       // Log.i("SS", "22");

        statusUpdate("Finding Characteristic...");

        BluetoothGattCharacteristic gattCharacteristic1 =
                mBluetoothGattService1.getCharacteristic(UUID.fromString(CHARACTERISTIC_TX1));

        if (gattCharacteristic1 == null) {
            statusUpdate("Couldn't find TX characteristic: " + CHARACTERISTIC_TX1);
            return;
        }

        statusUpdate("Found TX characteristic: " + CHARACTERISTIC_TX1);

        statusUpdate("Sending message 'Hello Grove BLE'");

        String msg = _msg;

        byte b = 0x00;
        byte[] temp = msg.getBytes();
        byte[] tx = new byte[temp.length + 1];
        tx[0] = b;

        for (int i = 0; i < temp.length; i++)
            tx[i + 1] = temp[i];

        gattCharacteristic1.setValue(tx);
        mBluetoothGatt1.writeCharacteristic(gattCharacteristic1);

    }
   public static void  sendMessage_byte(byte[] bbytes) {
       Log.i("jim","ble_sentin");
       if (mBluetoothGattService1 == null)
           return;
       BluetoothGattCharacteristic gattCharacteristic =
               mBluetoothGattService1.getCharacteristic(UUID.fromString(CHARACTERISTIC_TX1));

       if (gattCharacteristic == null) {
           statusUpdate("Couldn't find TX characteristic: " + CHARACTERISTIC_TX1);
           return;
       }
       byte b1 = 0x00;
       byte[] temp1 = bbytes;
       byte[] tx1 = new byte[temp1.length + 1];
       for (int i = 0; i < temp1.length; i++)
           tx1[i + 1] = temp1[i];
       gattCharacteristic.setValue(tx1);
       mBluetoothGatt1.writeCharacteristic(gattCharacteristic);
   }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter1.startLeScan(mLeScanCallback);

                try {
                    Log.e("money","scan");
                    Thread.sleep(SCAN_PERIOD1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter1.stopLeScan(mLeScanCallback);
            }
        }.start();

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (device != null) {
                if (mDevices1.indexOf(device) == -1)//to avoid duplicate entries
                {
                    if (DEVICE_NAME1.equals(device.getName())) {
                        mDevice1 = device;//we found our device!
                        Log.i(" money ", "Added " + device.getName() + ": " + device.getAddress());
                    }
                    mDevices1.add(device);
                    statusUpdate("Found device " + device.getName());
                }
            }
        }
    };

    private void findGroveBLE() {
        if (mDevices1 == null || mDevices1.size() == 0) {
            Log.i("money","no dvicw");
            statusUpdate("No BLE devices found");
            return;
        } else if (mDevice1 == null) {
            Log.i("money","dvice null");
            statusUpdate("Unable to find Grove BLE");
            return;
        } else {
            Log.i("money","find");
            statusUpdate("Found Grove BLE V1");
            statusUpdate("Address: " + mDevice1.getAddress());
            connectDevice();
        }
    }

    private boolean connectDevice() {
         device1 = mBluetoothAdapter1.getRemoteDevice(mDevice1.getAddress());
        if (device1 == null) {
            Log.i("money1","device_null");
            statusUpdate("Unable to connect");
            return false;
        }
        // directly connect to the device
        statusUpdate("Connecting11 ...");
        mBluetoothGatt1 = device1.connectGatt(activity1, false, mGattCallback);
        BLe_stus1 =true;
        //Read_Rssi1();
        return true;
    }
    public void Ble_Disconnect1(){

        mBluetoothGatt1.disconnect();
        mBluetoothGatt1.disconnect();



       // mBluetoothGatt1 =null;
        Log.i("jim","disconnect_b");
    }
    public void Ble_Reconnect1(){
        mBluetoothGatt1 = device1.connectGatt(activity1, false, mGattCallback);


    }
public void Read_Rssi1(){
    mBluetoothGatt1.readRemoteRssi();//rssi
}
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("money","Connected");
                statusUpdate("Connected");
                statusUpdate("Searching for services");
                mBluetoothGatt1.discoverServices();
                B_disable =false;


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                statusUpdate("Device disconnected");
                Log.i("money","disconnected");
                B_disable =true;
                mBluetoothGatt1.close();
                rssi_int2 =-60;
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices1 = mBluetoothGatt1.getServices();

                for (BluetoothGattService gattService1 : gattServices1) {
                    statusUpdate("Service discovered: " + gattService1.getUuid());
                    if (GROVE_SERVICE1.equals(gattService1.getUuid().toString())) {
                        Log.i("moneyxx","communication Service");
                        mBluetoothGattService1 = gattService1;
                        statusUpdate("Found communication Service");
                        sendMessage1("");
                    }
                }
                BluetoothGattCharacteristic GattCharacteristic_RX1 = mBluetoothGattService1.getCharacteristic(UUID.fromString(CHARACTERISTIC_RX1));
                mBluetoothGatt1.setCharacteristicNotification(GattCharacteristic_RX1, true);
            } else {
                statusUpdate("onServicesDiscovered received: " + status);
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            //Log.i("onCharacteristicChanged",TAG);
            byte[] data = characteristic.getValue();
            try {
                String str = new String(data, "UTF-8");
                statusUpdate(str);
                if (str.contains("a")) {
                    Log.i("Neo", "Find");

                }
			Log.i("Neo",""+str);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

            super.onReadRemoteRssi(gatt, rssi, status);
            //this.rssi =rssi;
            rssi_string2 =""+ rssi;
            rssi_int2 =rssi;
            Log.i("jim","rssixx___________"+rssi);
        }
    };





}



