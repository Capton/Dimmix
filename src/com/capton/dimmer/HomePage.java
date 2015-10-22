package com.capton.dimmer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

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
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HomePage extends Activity implements OnClickListener {
    final static int NOTIFICATION_ID = 123456;
    //private UUID  MY_UUID = UUID. "550e8400-e29b-41d4-a716-446655440000";

	float discrete = 0;
	float start = 0;
	float end = 100;
	float start_pos = 0;
	int start_position = 0;

    private NotificationManager notificationManager;
	private ProgressDialog pDialog;
	private ToggleButton btn0, btn1, btn2, btn3, btn4, btn5;
	private Button hideBtn, quitBtn, schedBtn;
	private ToggleButton connectBtn, bTBtn;
	private ProgressBar progBar;
	private SeekBar seekBar;
	private Dialog schedDialog;
	//public Bluetooth blueTooth;
	public BluetoothAdapter btAdapter;
	public BluetoothDevice btSensor ;
	public ArrayList<BluetoothDevice> foundBtDevicesList = null;
	public ArrayList<BluetoothDevice> btBondedDeviceList = null;
	public Boolean btStatus = false;
	public String msg = "";
	String $deviceName = "";
	String $sensorAddress = "";
	final String btSensorUrl = "20:15:02:26:05:09";
	final String tecnoUrl = "TECNO T347";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
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
		}
		else {
			btStatus = btAdapter.isEnabled();
			bTBtn.setChecked(btStatus);
		}
        notificationManager.cancel(NOTIFICATION_ID);

		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mBroadcastReceiver1, filter);

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
			if(switchBtStatus()){
				btAdapter.enable();
			}
			else{
				btAdapter.disable();
				connectBtn.setChecked(false);
			}
		} else if (id == connectBtn.getId()) {
			if (btStatus == true){
				//connectBtn.setChecked(true);
				if(sensorBonded()){
					//connect to the sensor
					ToastMaster(btSensor.getName() + " (" + btSensor.getAddress()+ ")" + " bonded");
					ConnectThread connThread = new ConnectThread(btSensor);
					connThread.run();
				}
			}
			else{
				ToastMaster("Switch bt on");
				connectBtn.setChecked(false);
			}

			//BluetoothDevice device = btAdapter.getRemoteDevice("");
			//BluetoothSocket tmp = null;
			//BluetoothSocket mmSocket = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			/*try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				Method m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				tmp = (BluetoothSocket) m.invoke(device, 1);
			} catch (IOException e) {
				Log.e("tag", "create() failed", e);
			}
			mmSocket = tmp;*/
		} else if (id == quitBtn.getId()) {
			this.finish();
		} else if (id == hideBtn.getId()) {
            toNotificationBar();
		} else if (id == schedBtn.getId()) {
			//this.startActivity(new Intent(this, Preferences.class));
            this.startActivity(new Intent(this, BtTest.class));
		}

	}

    public void toNotificationBar(){
        Intent resultIntent = new Intent(getApplicationContext(), HomePage.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification(R.drawable.bt2, " ", System.currentTimeMillis());
        notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), "", resultPendingIntent);
        notification.defaults = Notification.DEFAULT_ALL;
        notificationManager.notify(NOTIFICATION_ID, notification);
        this.finish();
    }


    public void sendMsg(String $msg){
		ToastMaster($msg);

    }

	private boolean sensorBonded(){
		boolean status = false;
		Set<BluetoothDevice> pairedDevice = btAdapter.getBondedDevices();
		if (pairedDevice.size() > 0) {
			ToastMaster(Integer.toString(pairedDevice.size()) + " Found bonded device(s)");
			for (BluetoothDevice device : pairedDevice) {
				if(btSensorUrl.equals(device.getAddress())) {
					btSensor = device;
					status = true;
				}

			}
		}
		return status;
	}

	public boolean switchBtStatus() {
		if (btAdapter.isEnabled()) {
			btStatus = false;
			msg = "Disabling Bluetooth...";
			new AttemptLogin().execute("bt");

		} else {
			btStatus = true;
			msg = "Enabling Bluetooth...";
			new AttemptLogin().execute("bt");
		}
		return btStatus;
	}

	public boolean createBond(BluetoothDevice btDevice) throws Exception {
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        Intent intent = new Intent(ACTION_PAIRING_REQUEST);
        String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
        intent.putExtra(EXTRA_DEVICE, btDevice);
        String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        return false;
	}

	private void startSearching() {
		Log.i("Log", "in the start searching method");
		ToastMaster("searching for available devices");
		IntentFilter intentFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		HomePage.this.registerReceiver(myReceiver, intentFilter);
		btAdapter.startDiscovery();
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Message msg = Message.obtain();
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				$deviceName = "";
                $sensorAddress = "";
				$deviceName = device.getName();
                $sensorAddress = device.getAddress();

                ToastMaster($deviceName+ " found ");
				try {
                    ConnectThread connThread = new ConnectThread(device);
                    //connThread.run();
					if (connThread.createBond(device)){
						ToastMaster($deviceName + " bonded");
					}
					else ToastMaster($deviceName + " not bonded");

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
			}
		}
	};

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
			/*
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
				ToastMaster(tmp.toString() + "socket created");
            } catch (IOException e) {
			}
			*/

			try{
				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				tmp = (BluetoothSocket) m.invoke(device, 1);
			} catch(NoSuchMethodException e){

			} catch (IllegalAccessException e){

			} catch (InvocationTargetException e){

			}

            mmSocket = tmp;
        }

        public void run() {

            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
				ToastMaster("connection successful");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
				ToastMaster("connection exception msg (" + connectException.getMessage()+ " )", Toast.LENGTH_LONG);
                try {
					//ToastMaster("couldn't connect");
					mmSocket.close();
					ToastMaster( "socket closed");
                } catch (IOException closeException) {
					ToastMaster("couldn't close Socket");
				}
                return;


            }

            // Do work to manage the connection (in a separate thread)
          //  manageConnectedSocket(mmSocket);
        }

		public boolean createBond(BluetoothDevice btDevice) throws Exception {
			Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
			Method createBondMethod = class1.getMethod("createBond");
			Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
			return returnValue.booleanValue();
		}

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {

            try {
                mmSocket.close();
            } catch (IOException e) { }

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

	private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch(state) {
					case BluetoothAdapter.STATE_OFF:
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						break;
					case BluetoothAdapter.STATE_ON:
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						break;
				}

			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mBroadcastReceiver1);
	}
}
