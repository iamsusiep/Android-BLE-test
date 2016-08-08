package com.example.administrator.mybluetoothletest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean scanning = false;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mBluetoothGatt;


    Button button;
    ListView listView;
    BleList bleList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},1);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Toast.makeText(this, "블루투스를 켜주세요", Toast.LENGTH_SHORT).show();
            finish();
        }


        bleList = new BleList();
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(bleList);




        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!scanning){
                    mBluetoothAdapter.startLeScan(leScanCallback);
                }
                else{
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                    bleList.clear();
                    bleList.notifyDataSetChanged();
                }
                scanning = !scanning;

            }
        });
    }

    private class BleList extends BaseAdapter{
        private ArrayList<BluetoothDevice> devices;
        private ArrayList<Integer> RSSIs;
        private LayoutInflater inflater;


        public BleList(){
            super();
            devices = new ArrayList<BluetoothDevice>();
            RSSIs = new ArrayList<Integer>();
            inflater = ((Activity) MainActivity.this).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device,int rssi){
            if(!devices.contains(device)){
                devices.add(device);
                RSSIs.add(rssi);
            }
            else{
                RSSIs.set(devices.indexOf(device),rssi);
            }
        }

        public void clear(){
            devices.clear();
            RSSIs.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(android.R.layout.two_line_list_item,null);
                viewHolder.deviceName = (TextView) convertView.findViewById(android.R.id.text1);
                viewHolder.deviceRssi = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String deviceName = devices.get(position).getName();
            int rssi = RSSIs.get(position);

            viewHolder.deviceName.setText(deviceName != null && deviceName.length() > 0 ?deviceName:"알 수 없는 장치");
            viewHolder.deviceRssi.setText(String.valueOf(rssi));

            return convertView;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceRssi;
    }

    // 스켄 이후 장치 발견 이벤트
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("scan",device.getName() + " RSSI :" + rssi + " Record " + scanRecord);
            bleList.addDevice(device,rssi);
            bleList.notifyDataSetChanged();
        }
    };

}
