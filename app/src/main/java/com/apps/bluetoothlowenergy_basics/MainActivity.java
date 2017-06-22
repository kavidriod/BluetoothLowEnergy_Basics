package com.apps.bluetoothlowenergy_basics;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT =1 ;
    private static final int PERMISSION_REQUEST_CODE = 2;
    ToggleButton prelollipopToggleButton,postlollipopToggleButton;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    private AbsListView listView;
    DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (AbsListView) findViewById(android.R.id.list);

        prelollipopToggleButton = (ToggleButton) findViewById(R.id.prelollipopToggleButton);
        postlollipopToggleButton = (ToggleButton) findViewById(R.id.postlollipopToggleButton);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        deviceListAdapter = new DeviceListAdapter(getApplicationContext());

        //Check Build Version is < 21 or not
        if (Build.VERSION.SDK_INT < 21){
            showToast("Prelollipop,Click on Pre-Lollipop Scan Button");
        }else {
            showToast("Lollipop or Higher,Click on Lollipop & After Scan Button");
        }


        prelollipopToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (!bluetoothAdapter.isEnabled()){
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }else {
                    if (Build.VERSION.SDK_INT >= 23){
                        //Grant Permission in runtime
                        if (requestPermissionAtRumTime()){
                            scanPreLollipop(isChecked);
                        }

                    }else {
                        //User already granted permission before Installation
                    }

                }
            }
        });

        postlollipopToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!bluetoothAdapter.isEnabled()){
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }else {
                    scanPostLollipop(isChecked);
                    if (Build.VERSION.SDK_INT >= 23){
                        //Grant Permission in runtime
                        if (requestPermissionAtRumTime()){
                            scanPreLollipop(isChecked);
                        }

                    }else {
                        //User already granted permission before Installation
                    }

                }
            }
        });

    }

    private void scanPostLollipop(boolean isChecked) {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (isChecked){
            List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
            ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bluetoothLeScanner.startScan(scanFilters,scanSettings,scanCallback);
        }else {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private void scanPreLollipop(boolean isChecked) {
        if (isChecked){
            bluetoothAdapter.startLeScan(leScanCallback);
        }else {
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private boolean requestPermissionAtRumTime(){
        String[] permissionsToRequest = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };


        List<String> listPermissionsNeeded = new ArrayList<String>();
        int result;
        for (String s:permissionsToRequest){
            result = ContextCompat.checkSelfPermission(this,s);
            if (result != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(s);
            }
        }


        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSION_REQUEST_CODE);
            return false;
        }

        return  true;
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addToAdapter(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            addToAdapter(device);
        }
    };

    private void addToAdapter(BluetoothDevice device) {
        deviceListAdapter.addDevice(device);
        listView.setAdapter(deviceListAdapter);
        deviceListAdapter.notifyDataSetChanged();
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == Activity.RESULT_CANCELED){
                //Bluetooth is not Enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     switch (requestCode){
         case  PERMISSION_REQUEST_CODE:

             if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                 if (Build.VERSION.SDK_INT < 21){
                     scanPreLollipop(true);
                 }else{
                     scanPostLollipop(true);
                 }
             }else {
                 requestPermissionAtRumTime();
             }
             break;
         default:
             break;
     }
    }
}
