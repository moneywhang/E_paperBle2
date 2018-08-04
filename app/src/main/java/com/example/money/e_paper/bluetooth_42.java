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


public class bluetooth_42 {
    Timer mTimer2;
    private BluetoothAdapter mBluetoothAdapter2;//our local adapter
    private static final long SCAN_PERIOD2 = 1000; //5 seconds
    private static List<BluetoothDevice> mDevices2 = new ArrayList<BluetoothDevice>();//discovered devices in range
    private BluetoothDevice mDevice2; //external BLE device (Grove BLE module)
    private static BluetoothGatt mBluetoothGatt2; //provides the GATT functionality for communication
    public static String DEVICE_NAME2 = "C";
   // private static final String DEVICE_NAME = "AORUS_X5";
    private static final String GROVE_SERVICE2 = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_TX2 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_RX2 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public boolean BLe_stus2 =false ,connect_stus2 =false;


//    private static final String DEVICE_NAME = "CC2650 SensorTag";
//    private static final String GROVE_SERVICE = "f000aa80-0451-4000-b000-0000000000000";
//    private static final String CHARACTERISTIC_TX = "f000aa82-0451-4000-b000-00000000000";
//    private static final String CHARACTERISTIC_RX = "f000aa81-0451-4000-b000-00000000000";

    public static BluetoothGattService mBluetoothGattService2; //service on mBlueoothGatt
    private Activity activity2;

   public String rssi_string3;
    public int rssi_int3;
    BluetoothDevice device2;
    public  boolean C_disable =false;
    public bluetooth_42(Activity activity1, Activity activity) {


    }

//    public bluetooth_40(Integer[] a, int[] b, ImageView[] imageView, Integer[] imageView1, ImageView imageView2) {
//        this.a = a;
//        this.b = b;
//        this.imageView = imageView;
//        this.imageView1 = imageView1;
//        this.imageView2 = imageView2;
//    }

    public bluetooth_42(Activity activity) {
        this.activity2 = activity;
        Log.e("money","one1");
        final BluetoothManager mBluetoothManager = (BluetoothManager) activity.getSystemService(activity.BLUETOOTH_SERVICE);
        mBluetoothAdapter2 = mBluetoothManager.getAdapter();
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
        mTimer2 = new Timer();
        scanLeDevice();
        mTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                findGroveBLE();
                //Log.e("money","two");
            }
        }, SCAN_PERIOD2);


    }
//----------------rssi

    public static void sendMessage1(String _msg) {
        if (mBluetoothGattService2 == null)
            return;
       // Log.i("SS", "22");

        statusUpdate("Finding Characteristic...");

        BluetoothGattCharacteristic gattCharacteristic1 =
                mBluetoothGattService2.getCharacteristic(UUID.fromString(CHARACTERISTIC_TX2));

        if (gattCharacteristic1 == null) {
            statusUpdate("Couldn't find TX characteristic: " + CHARACTERISTIC_TX2);
            return;
        }

        statusUpdate("Found TX characteristic: " + CHARACTERISTIC_TX2);

        statusUpdate("Sending message 'Hello Grove BLE'");

        String msg = _msg;

        byte b = 0x00;
        byte[] temp = msg.getBytes();
        byte[] tx = new byte[temp.length + 1];
        tx[0] = b;

        for (int i = 0; i < temp.length; i++)
            tx[i + 1] = temp[i];

        gattCharacteristic1.setValue(tx);
        mBluetoothGatt2.writeCharacteristic(gattCharacteristic1);

    }
   public static void  sendMessage_byte(byte[] bbytes) {
       Log.i("jim","ble_sentin");
       if (mBluetoothGattService2 == null)
           return;
       BluetoothGattCharacteristic gattCharacteristic =
               mBluetoothGattService2.getCharacteristic(UUID.fromString(CHARACTERISTIC_TX2));

       if (gattCharacteristic == null) {
           statusUpdate("Couldn't find TX characteristic: " + CHARACTERISTIC_TX2);
           return;
       }
       byte b1 = 0x00;
       byte[] temp1 = bbytes;
       byte[] tx1 = new byte[temp1.length + 1];
       for (int i = 0; i < temp1.length; i++)
           tx1[i + 1] = temp1[i];
       gattCharacteristic.setValue(tx1);
       mBluetoothGatt2.writeCharacteristic(gattCharacteristic);
   }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter2.startLeScan(mLeScanCallback);

                try {
                    Log.e("money","scan");
                    Thread.sleep(SCAN_PERIOD2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter2.stopLeScan(mLeScanCallback);
            }
        }.start();

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (device != null) {
                if (mDevices2.indexOf(device) == -1)//to avoid duplicate entries
                {
                    if (DEVICE_NAME2.equals(device.getName())) {
                        mDevice2 = device;//we found our device!
                        Log.i(" money ", "Added " + device.getName() + ": " + device.getAddress());
                    }
                    mDevices2.add(device);
                    statusUpdate("Found device " + device.getName());
                }
            }
        }
    };

    private void findGroveBLE() {
        if (mDevices2 == null || mDevices2.size() == 0) {
            Log.i("money","no dvicw");
            statusUpdate("No BLE devices found");
            return;
        } else if (mDevice2 == null) {
            Log.i("money","dvice null");
            statusUpdate("Unable to find Grove BLE");
            return;
        } else {
            Log.i("money","find");
            statusUpdate("Found Grove BLE V1");
            statusUpdate("Address: " + mDevice2.getAddress());
            connectDevice();
        }
    }

    private boolean connectDevice() {
         device2 = mBluetoothAdapter2.getRemoteDevice(mDevice2.getAddress());
        if (device2 == null) {
            Log.i("money1","device_null");
            statusUpdate("Unable to connect");
            return false;
        }
        // directly connect to the device
        statusUpdate("Connecting11 ...");
        mBluetoothGatt2 = device2.connectGatt(activity2, false, mGattCallback);
        BLe_stus2 =true;
        //Read_Rssi1();
        return true;
    }
    public void Ble_Disconnect2(){

        mBluetoothGatt2.disconnect();
        mBluetoothGatt2.disconnect();



       // mBluetoothGatt1 =null;
        Log.i("jim","disconnect_b");
    }
    public void Ble_Reconnect2(){
        mBluetoothGatt2 = device2.connectGatt(activity2, false, mGattCallback);


    }
public void Read_Rssi2(){
    mBluetoothGatt2.readRemoteRssi();//rssi
}
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("money","Connected");
                statusUpdate("Connected");
                statusUpdate("Searching for services");
                mBluetoothGatt2.discoverServices();
                C_disable =false;


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                statusUpdate("Device disconnected");
                Log.i("money","disconnected");
                C_disable =true;
                mBluetoothGatt2.close();
                rssi_int3 =-60;
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices1 = mBluetoothGatt2.getServices();

                for (BluetoothGattService gattService1 : gattServices1) {
                    statusUpdate("Service discovered: " + gattService1.getUuid());
                    if (GROVE_SERVICE2.equals(gattService1.getUuid().toString())) {
                        Log.i("moneyxx","communication Service");
                        mBluetoothGattService2 = gattService1;
                        statusUpdate("Found communication Service");
                        sendMessage1("");
                    }
                }
                BluetoothGattCharacteristic GattCharacteristic_RX1 = mBluetoothGattService2.getCharacteristic(UUID.fromString(CHARACTERISTIC_RX2));
                mBluetoothGatt2.setCharacteristicNotification(GattCharacteristic_RX1, true);
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
            rssi_string3 =""+ rssi;
            rssi_int3 =rssi;
            Log.i("jim","CCCC:"+rssi);
        }
    };





}



