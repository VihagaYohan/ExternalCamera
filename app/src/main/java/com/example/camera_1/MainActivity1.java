package com.example.camera_1;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accessing USB manager
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        Log.d("title","device list goes here");
        Log.d("device list", String.valueOf(manager.getDeviceList()));

        // get device connected devices list via USB
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        // request for permission
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        // initializing an iterator
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            manager.requestPermission(device, permissionIntent);
        }

        // initiate array list
        List<String> valuesList = new ArrayList<>();

        // add items to array list
        for(UsbDevice usbDevice : deviceList.values()) {
            // Log.d("camera device","value - " + usbDevice.getDeviceId());
            valuesList.add(usbDevice.getDeviceName());

        }

       /* HashMap<String, String> capitalCities = new HashMap<>();
        capitalCities.put("England", "London");
        capitalCities.put("Germany", "Berlin");
        capitalCities.put("Norway", "Oslo");
        capitalCities.put("USA", "Washington DC");

        List<String> valuesList = new ArrayList<>(capitalCities.values());*/

        // create adapapter for list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valuesList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        TextView txtCount = findViewById(R.id.textCount);
        /*  set count for detected USB devices
            txtCount.setText("count - " + valuesList.size());
        */

        txtCount.setText(hasCamera());
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                        }
                    }
                    else {
                        Log.d("Permission Tag", "permission denied for device " + device);
                    }
                }
            }
        }
    };

    // show alert
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alert title")
                .setMessage("This is an alert message")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // check whether device has external camera, front camera or world-facing camera available
    private String hasCamera() {
           if(getApplicationContext().getPackageManager().hasSystemFeature(
                   PackageManager.FEATURE_CAMERA_EXTERNAL)) {
               return "Has external camera";
           } else if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
               return "Has front-facing camera";
           } else if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
               return "Has back camera";
           } else {
               return "Does not have camera feature";
           }
    }
}