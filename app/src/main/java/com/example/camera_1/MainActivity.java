package com.example.camera_1;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager manager;
    private List<String> usbDevicesList = new ArrayList<>();
    private UsbDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDevices();
        // device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);

        // check for deviceList length
        if(usbDevicesList.size() == 0) {
            Toast.makeText(this, "Usb device list is empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

            registerReceiver(usbReceiver, filter);

            manager.requestPermission(device,permissionIntent);
        }
    }

    // get connected devices using intent
    private void getDevicesIntent() {
        device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
    }

    private void getDevices(){
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if(deviceList.size() == 0) {
            Toast.makeText(this, "There are no devices connected. Hash map is empty", Toast.LENGTH_SHORT).show();
        } else {
            for(UsbDevice usbDevice : deviceList.values()) {
                usbDevicesList.add(usbDevice.getDeviceName());
                device = usbDevice;
            }
            initializeList();
        }
    }

    // get connected devices
    /* private void getDevices() {
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // get external USB devices connected to android device
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        // iterate through deviceList, and items to usbDeviceList array list
        for(UsbDevice usbDevice : deviceList.values()) {
            usbDevicesList.add(usbDevice.getDeviceName());
        }

        // initializeList();
    }*/

    // initialize data for list view
    private void initializeList(){
        // create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usbDevicesList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // set list count
        TextView txtCount = findViewById(R.id.textCount);
        txtCount.setText("count - " + usbDevicesList.size());
    }

    // setting up broadcast receiver
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    // UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    // device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    UsbDevice device1 = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,true)) {
                        if(device1 != null) {
                            // call method to set up device communication
                            Toast.makeText(context,"device id - " + device.getDeviceId(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
   Toast.makeText(context, "permission denied for device " + device, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    // check whether device has camera
    private CameraOptions hasCamera() {
        if(getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_EXTERNAL
        )){
            return CameraOptions.EXTERNAL;
        } else if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT
        )) {
            return CameraOptions.FRONT;
        } else if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA
        )) {
            return CameraOptions.BACK;
        } else {
            return CameraOptions.NO_HARDWARE;
        }
    }
}

enum CameraOptions {
    EXTERNAL,
    FRONT,
    BACK,
    NO_HARDWARE
}
