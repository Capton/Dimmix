package com.capton.dimmer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BtTest extends Activity {

    final static int REQUEST_ENABLE_BT = 1;
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            switch (state) {

                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context,
                            "BTStateChangedBroadcastReceiver: STATE_OFF",
                            Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context,
                            "BTStateChangedBroadcastReceiver: STATE_ON",
                            Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(context,
                            "BTStateChangedBroadcastReceiver: STATE_TURNING_OFF",
                            Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(context,
                            "BTStateChangedBroadcastReceiver: STATE_TURNING_ON",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    TextView textInfo;
    Button buttonEnableBT;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textInfo = (TextView) findViewById(R.id.info);
        buttonEnableBT = (Button) findViewById(R.id.enablebt);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            textInfo.setText("BlueTooth not supported in this device");
            buttonEnableBT.setEnabled(false);
        } else {
            if (bluetoothAdapter.isEnabled()) {
                textInfo.setText("BlueTooth enabled");
            } else {
                buttonEnableBT.setEnabled(true);
                textInfo.setText("BlueTooth disabled, click button to turn on BlueTooth.");
            }

            //register BroadcastReceiver
            registerReceiver(myReceiver,
                    new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        }

        buttonEnableBT.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(BtTest.this, "BlueTooth Turned On", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BtTest.this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }


        if (bluetoothAdapter.isEnabled()) {
            buttonEnableBT.setText("Bluetooth enabled");
            textInfo.setText("BlueTooth enabled");
        } else {
            buttonEnableBT.setEnabled(true);
            textInfo.setText("BlueTooth disabled, click button to turn on BlueTooth.");
        }

    }


}