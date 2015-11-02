package com.capton.dimmer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Bluetooth extends Activity implements OnClickListener {
    BluetoothAdapter btAdapter;
    ArrayList<BluetoothDevice> foundBtDevicesList = null;
    Boolean btStatus = false;
    String $deviceName = "";
    private ToggleButton btn;
    private Button startBtn, connectbtn;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT)
                        .show();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                $deviceName = "";
                $deviceName = device.getName();
                try {
                    if (createBond(device)) {
                        Toast.makeText(getApplicationContext(), $deviceName + " is bonded", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), $deviceName + " bonding failed.", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (foundBtDevicesList.size() < 1) {
                    foundBtDevicesList.add(device);
                } else {
                    boolean flag = true;
                    for (int i = 0; i < foundBtDevicesList.size(); i++) {
                        if (device.getAddress().equals(
                                foundBtDevicesList.get(i).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        foundBtDevicesList.add(device);
                    }
                }
                Toast.makeText(getApplicationContext(), $deviceName, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn = (ToggleButton) findViewById(R.id.btstatus);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        foundBtDevicesList = new ArrayList<BluetoothDevice>();
        connectbtn.setOnClickListener(this);
        btn.setOnClickListener(this);
        startBtn.setOnClickListener(this);

        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG)
                    .show();
        } else {
            btStatus = btAdapter.isEnabled();
            btn.setChecked(btStatus);
            Toast.makeText(getApplicationContext(), "btAdapter enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn.getId())
            switchBtStatus();
        else if (v.getId() == startBtn.getId())
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        else if (v.getId() == connectbtn.getId()) {
            startSearching();
            for (BluetoothDevice device : foundBtDevicesList) {
                Toast.makeText(getApplicationContext(), device.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void switchBtStatus() {
        // if (btn.isChecked()) {
        if (btAdapter.isEnabled()) {
            btAdapter.disable();
            Toast.makeText(getApplicationContext(), "Disabling Bluetooth...",
                    Toast.LENGTH_LONG).show();
        } else {
            btAdapter.enable();
            Toast.makeText(getApplicationContext(), " Enabling Bluetooth...",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean createBond(BluetoothDevice btDevice) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        Bluetooth.this.registerReceiver(myReceiver, intentFilter);
        btAdapter.startDiscovery();
    }
}
