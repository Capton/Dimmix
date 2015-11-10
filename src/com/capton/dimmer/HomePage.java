package com.capton.dimmer;

import android.app.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class HomePage extends Activity implements OnClickListener {
    final static int NOTIFICATION_ID = 123456;
    //private UUID  MY_UUID = UUID. "550e8400-e29b-41d4-a716-446655440000";
    final String btSensorUrl = "20:15:02:26:05:09";
    final String tecnoUrl = "TECNO T347";
    public BluetoothAdapter btAdapter;
    public BluetoothDevice btSensor;
    public ArrayList<BluetoothDevice> foundBtDevicesList = null;
    public ArrayList<BluetoothDevice> btBondedDeviceList = null;
    public Boolean btStatus = false;
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //ToastMaster("Bluetooth turned off");
                        btStateChanged(false);
                        closeConn();
                        btStatus = false;
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //ToastMaster("Bluetooth turning off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //ToastMaster("Bluetooth turned On");
                        btStateChanged(true);
                        btStatus = true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //ToastMaster("Bluetooth turning On");
                        break;
                }

            }

        }
    };
    public String msg = "";
    float discrete = 0;
    float start = 0;
    float end = 100;
    float start_pos = 0;
    int start_position = 0;
    boolean sensorFound = false;
    boolean connStatus = false;
    private NotificationManager notificationManager;
    private ProgressDialog pDialog;
    private ToggleButton btn0, btn1, btn2, btn3, btn4, btn5;
    private Button hideBtn, quitBtn, schedBtn;
    private ToggleButton connectBtn, bTBtn;
    private ProgressBar progBar;
    private SeekBar seekBar;
    private Dialog schedDialog;
    private BluetoothSocket mmSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ConnectedThread mConnectedThread;
    private Handler bluetoothInHandler;
    final int handlerState = 0;
    private boolean autoConn = false;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                if (device.getAddress().equals(btSensorUrl)) {
                    ToastMaster(device.getName() + " found ");
                    btSensor = device;
                    btAdapter.cancelDiscovery();
                    sensorFound = true;
                    try {
                        if (sensorBonded()) {
                            ToastMaster(btSensor.getName() + " bonded");
                            ConnectThread connThread = new ConnectThread(btSensor);
                            connThread.run();
                        }
                        else {
                            ToastMaster(btSensor.getName() + " not bonded");
                            //create bond here
                            if (createBond(btSensor)) {
                                ConnectThread connThread = new ConnectThread(btSensor);
                                connThread.run();
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        btn0 = (ToggleButton) findViewById(R.id.btn0);
        btn1 = (ToggleButton) findViewById(R.id.btn1);
        btn2 = (ToggleButton) findViewById(R.id.btn2);
        btn3 = (ToggleButton) findViewById(R.id.btn3);
        btn4 = (ToggleButton) findViewById(R.id.btn4);
        btn5 = (ToggleButton) findViewById(R.id.btn5);
        schedBtn = (Button) findViewById(R.id.schedbtn);
        hideBtn = (Button) findViewById(R.id.hide);
        quitBtn = (Button) findViewById(R.id.quit);
        connectBtn = (ToggleButton) findViewById(R.id.connect);
        bTBtn = (ToggleButton) findViewById(R.id.btstatus);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        schedDialog = new Dialog(HomePage.this);
        schedDialog.setTitle("Dimmer Event Scheduler");

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        schedBtn.setOnClickListener(this);
        hideBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        connectBtn.setOnClickListener(this);
        bTBtn.setOnClickListener(this);
        progBar.setOnClickListener(this);
        start = 0; // you need to give starting value of SeekBar
        end = 5; // you need to give end value of SeekBar
        start_pos = 0; // you need to give starting position value of SeekBar

        start_position = (int) (((start_pos - start) / (end - start)) * 100);
        discrete = start_pos;
        seekBar.setProgress(start_position);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                // To convert it as discrete value
                float temp = progress;
                float dis = end - start;
                discrete = (start + ((temp / 100) * dis));
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        foundBtDevicesList = new ArrayList<BluetoothDevice>();
        btBondedDeviceList = new ArrayList<BluetoothDevice>();

        if (btAdapter == null) {
        } else {
            if (autoConn) {
                //btAdapter.enable();
                if (!btAdapter.isEnabled())
                    switchBtStatus();
                createConn(btAdapter.isEnabled());
            }
            btStatus = btAdapter.isEnabled();
            bTBtn.setChecked(btStatus);
            connectBtn.setChecked(connStatus);
        }
        notificationManager.cancel(NOTIFICATION_ID);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter);

        bluetoothInHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    //ToastMaster(msg.toString());
                }
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                this.startActivity(new Intent(getApplicationContext(),
                        Preferences.class));
                break;
            case R.id.quit:
                this.finish();
                break;
            case R.id.about:
                this.startActivity(new Intent(getApplicationContext(), About.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btn0.getId()) {
            seekBar.setProgress(0);
            btn0.setChecked(true);
            btn1.setChecked(false);
            btn2.setChecked(false);
            btn3.setChecked(false);
            btn4.setChecked(false);
            btn5.setChecked(false);
            sendMsg("0");
        } else if (id == btn1.getId()) {
            // btn1.setBackgroundColor(Color.BLUE);
            seekBar.setProgress(1);
            btn0.setChecked(false);
            btn1.setChecked(true);
            btn2.setChecked(false);
            btn3.setChecked(false);
            btn4.setChecked(false);
            btn5.setChecked(false);
            sendMsg("1");

        } else if (id == btn2.getId()) {
            seekBar.setProgress(2);
            btn0.setChecked(false);
            btn1.setChecked(true);
            btn2.setChecked(true);
            btn3.setChecked(false);
            btn4.setChecked(false);
            btn5.setChecked(false);
            sendMsg("2");
        } else if (id == btn3.getId()) {
            seekBar.setProgress(3);
            btn0.setChecked(false);
            btn1.setChecked(true);
            btn2.setChecked(true);
            btn3.setChecked(true);
            btn4.setChecked(false);
            btn5.setChecked(false);
            sendMsg("3");
        } else if (id == btn4.getId()) {
            seekBar.setProgress(4);
            btn0.setChecked(false);
            btn1.setChecked(true);
            btn2.setChecked(true);
            btn3.setChecked(true);
            btn4.setChecked(true);
            btn5.setChecked(false);
            sendMsg("4");
        } else if (id == btn5.getId()) {
            seekBar.setProgress(5);
            btn0.setChecked(false);
            btn1.setChecked(true);
            btn2.setChecked(true);
            btn3.setChecked(true);
            btn4.setChecked(true);
            btn5.setChecked(true);
            sendMsg("5");
        } else if (id == bTBtn.getId()) {
            switchBtStatus();
        } else if (id == connectBtn.getId()) {
            if (connectBtn.isChecked()) {
                connectBtn.setChecked(false);
                if (btStatus == true) {
                    if (connStatus == false) {
                        if (sensorBonded()) {
                            //connect to the sensor
                            if (btSensor.getBondState() == BluetoothDevice.BOND_BONDED) {
                                ToastMaster(btSensor.getName() + " is bonded");
                                if (!connStatus) {
                                    createConn(true);
                                } else ToastMaster("connenction already exists");
                            } else {
                                ToastMaster(btSensor.getName() + " not bonded");
                            }
                        } else {
                            startSearching();
                            if (sensorFound) {
                                try {
                                    createConn(createBond(btSensor));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        ToastMaster("connection already exists");
                    }
                } else {
                    ToastMaster("Switch Bluetooth On First");
                    connectBtn.setChecked(false);
                }
            } else {
                closeConn();
                ToastMaster("closing connection");
            }


        } else if (id == quitBtn.getId()) {
            this.finish();
        } else if (id == hideBtn.getId()) {
            toNotificationBar();
        } else if (id == schedBtn.getId()) {
            //this.startActivity(new Intent(this, Preferences.class));
            this.startActivity(new Intent(this, BtTest.class));
        }

    }

    public void toNotificationBar() {
        Intent resultIntent = new Intent(getApplicationContext(), HomePage.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification(R.drawable.bt2, " ", System.currentTimeMillis());
        notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), "", resultPendingIntent);
        notification.defaults = Notification.DEFAULT_ALL;
        notificationManager.notify(NOTIFICATION_ID, notification);
        this.finish();
    }

    public void sendMsg(String $msg) {
        ToastMaster($msg);
        if(connStatus)
            mConnectedThread.write($msg);
    }

    private boolean sensorBonded() {
        boolean status = false;
        Set<BluetoothDevice> pairedDevice = btAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                if (device.getAddress().equals(btSensorUrl)) {
                    btSensor = device;
                    status = true;
                } else btSensor = null;

            }
        }
        return status;
    }

    public boolean switchBtStatus() {
        if (btAdapter.isEnabled()) {
            msg = "Disabling Bluetooth...";
            new AttemptLogin().execute("bt");
            closeConn();
            btAdapter.disable();
            btStatus = false;

        } else {
            msg = "Enabling Bluetooth...";
            new AttemptLogin().execute("bt");
            btAdapter.enable();
            btStatus = true;
        }
        return btStatus;
    }

    public boolean createBond(BluetoothDevice btDevice) throws Exception {
        try {
            Log.d("", "Start Pairing...");
            Method m = btDevice.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(btDevice, (Object[]) null);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        return sensorBonded();
    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        ToastMaster("searching for available devices");
        IntentFilter intentFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        HomePage.this.registerReceiver(myReceiver, intentFilter);
        if(btAdapter.isDiscovering()) {
            ToastMaster("canceling discovery");
            btAdapter.cancelDiscovery();
        }
        btAdapter.startDiscovery();
    }

    void ToastMaster(String textToDisplay, int length) {
        Toast myMessage = Toast.makeText(getApplicationContext(),
                textToDisplay,
                length);
        myMessage.setGravity(Gravity.CENTER, 0, 0);
        myMessage.show();
    }

    void ToastMaster(String textToDisplay) {
        Toast myMessage = Toast.makeText(getApplicationContext(),
                textToDisplay,
                Toast.LENGTH_SHORT);
        myMessage.setGravity(Gravity.CENTER, 0, 0);
        myMessage.show();
    }

    private void btStateChanged(boolean status) {
        if (status) {
            bTBtn.setChecked(true);
        } else {
            bTBtn.setChecked(false);
            connectBtn.setChecked(false);
        }
    }

    private void createConn(boolean status) {
        if (status) {
            ConnectThread connThread = new ConnectThread(btSensor);
            connThread.run();
        } else {
            //ToastMaster("Connection Lost");
        }
    }

    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            btSensor = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice

            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) m.invoke(device, 1);
            } catch (NoSuchMethodException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }

            mmSocket = tmp;
        }

        public void run() {

            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                if (mmSocket.isConnected())
                    	ToastMaster("Connection already established");
                else{
                    mmSocket.connect();
                    connectBtn.setChecked(true);
                    ToastMaster("connection successful");
                    connStatus = true;
                }

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                connectBtn.setChecked(false);
                ToastMaster("connection exception msg (" + connectException.getMessage().toString() + " )", Toast.LENGTH_LONG);
                try {
                    //ToastMaster("couldn't connect");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mmSocket.close();
                    ToastMaster("socket closed");
                } catch (IOException closeException) {
                    ToastMaster("couldn't close Socket");
                }
            }

            // Do work to manage the connection (in a separate thread)
            //  manageConnectedSocket(mmSocket);
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            closeConn();
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomePage.this);
            pDialog.setMessage(msg);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                // ToastMaster(file_url);
            }

        }
    }

    public void closeConn() {
        //adding the Thread.sleep(1000) before reseting the conection releases the bluetooth.
        if(btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();
        if (connStatus) {
            try {
                Thread.sleep(1000);
                mmSocket.close();
                connStatus = false;
                mmSocket = null;
            } catch (IOException e) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        unregisterReceiver(mBroadcastReceiver1);
    }

    public void onStop() {
        super.onStop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        closeConn();

    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothInHandler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}