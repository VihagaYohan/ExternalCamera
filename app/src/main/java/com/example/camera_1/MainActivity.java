package com.example.camera_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        Log.d("title","device list goes here");
        Log.d("device list", String.valueOf(manager.getDeviceList()));
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        List<String> valuesList = new ArrayList<>();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valuesList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        TextView txtCount = findViewById(R.id.textCount);
        /*  set count for detected USB devices
            txtCount.setText("count - " + valuesList.size());
        */

        txtCount.setText(hasCamera());
    }

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