package com.apps.bluetoothlowenergy_basics;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kavitha on 6/22/2017.
 */

public class DeviceListAdapter  extends BaseAdapter {

    Context context;
    List<BluetoothDevice> bluetoothDeviceList;
    LayoutInflater layoutInflater;


    public  DeviceListAdapter(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        bluetoothDeviceList= new ArrayList<>();
    }


    public void  addDevice(BluetoothDevice bluetoothDevice){
        if (!bluetoothDeviceList.contains(bluetoothDevice)){
            bluetoothDeviceList.add(bluetoothDevice);
        }
    }

    @Override
    public int getCount() {
        return bluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        BluetoothDevice eachPairedDevices = bluetoothDeviceList.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.scanned_items, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.macAddress = (TextView) convertView.findViewById(R.id.macAddress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.deviceName.setText(eachPairedDevices.getName());
        viewHolder.macAddress.setText(eachPairedDevices.getAddress());

        return convertView;
    }


    private class ViewHolder {
        TextView deviceName, macAddress;
    }
}