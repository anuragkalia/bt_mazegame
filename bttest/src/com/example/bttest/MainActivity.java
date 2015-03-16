package com.example.bttest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.view.Menu;
//import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends ActionBarActivity {

	private static final String logPrefix = "MainActivity";
	private ArrayAdapter<String> mArrayAdapter;//

	// Create a BroadcastReceiver
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(logPrefix, "in onReceive()");

			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				TextView bt_curr_state = (TextView) findViewById(R.id.BT_curr_state);

				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					bt_curr_state.setText("Bluetooth off");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					bt_curr_state.setText("Turning Bluetooth off...");
					break;
				case BluetoothAdapter.STATE_ON:
					bt_curr_state.setText("Bluetooth on");
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					bt_curr_state.setText("Turning Bluetooth on...");
					break;
				}
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.d(logPrefix, "in onReceive() for ACTION_FOUND");
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				mArrayAdapter
						.add(device.getName() + "\n" + device.getAddress());

				ListView all_dev_list = (ListView) findViewById(R.id.list);
				all_dev_list.setAdapter(mArrayAdapter);
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				Log.d(logPrefix, "in onReceive() for ACTION_DISCOVERY_STARTED");
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				Log.d(logPrefix, "in onReceive() for ACTION_DISCOVERY_FINISHED");
			} else if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
						BluetoothAdapter.ERROR);

				Log.d(logPrefix, "...ACTION_SCANMODE_CHANGED");
				switch (state) {
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
					Log.d(logPrefix, "SCAN_MODE_CONNECTABLE");
					break;
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
					Log.d(logPrefix, "SCAN_MODE_CONNECTABLE_DISCOVERABLE");
					break;
				case BluetoothAdapter.SCAN_MODE_NONE:
					Log.d(logPrefix, "SCAN_MODE_NONE");
					break;
				}
			}
		}
	};

	// */

	void get_bonded_devices() {
		BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.d(logPrefix, "in get_bonded_devices()");
		mArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1);
		Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices

			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView

				mArrayAdapter
						.add(device.getName() + "\n" + device.getAddress());
			}

			ListView all_dev_list = (ListView) findViewById(R.id.list);
			all_dev_list.setAdapter(mArrayAdapter);
		}

		if (mBTAdapter.isDiscovering()) {
			Log.d(logPrefix, "isDiscovering() is true");
			mBTAdapter.cancelDiscovery();
		}
		if (!mBTAdapter.startDiscovery()) {
			Log.w(logPrefix, "startDiscovery() returned 0");
		} else {
			Log.d(logPrefix, "startDsicovery() working");
		}
		// */
	}

	private static final int REQUEST_ENABLE_BT = 100;
	private static final int REQUEST_BT_DISC_ON = 200;
	private boolean bt_is_on = false;

	void mainfunction() {
		Log.d(logPrefix, "in mainfunction()");

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		TextView bt_notfound = (TextView) findViewById(R.id.BT_notfound);
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth

			bt_notfound.setText("Bluetooth NOT found in phone :'(");
		} else {
			bt_notfound.setText("Bluetooth found in phone ^_^");

			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				TextView bt_enabled = (TextView) findViewById(R.id.BT_enabled);
				bt_enabled.setText("Bluetooth already enabled");

				bt_is_on = true;
			}
		}
	}

	static boolean button_find_dev_off = true;
	static boolean button_disc_off = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(logPrefix, "in onCreate()");

		setContentView(R.layout.activity_main);

		final Button button_find = (Button) findViewById(R.id.find_BT_dev);
		button_find.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Perform action on click

				Button button = (Button) findViewById(R.id.find_BT_dev);
				if (button_find_dev_off == true) {
					button.setText("Find Devices");

					if (bt_is_on == true) {
						get_bonded_devices();
					}

				} else {
					button.setText("Do Not Find Devices");

					BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();
					if (BtAdapter.isDiscovering()) {
						BtAdapter.cancelDiscovery();
					}
					ListView listview = (ListView) findViewById(R.id.list);
					listview.setAdapter(null);
				}
				button_find_dev_off = !button_find_dev_off;
			}
		});

		final Button button_disc = (Button) findViewById(R.id.bt_disc_on);
		button_disc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Perform action on click
				Button button = (Button) findViewById(R.id.bt_disc_on);
				if (button_disc_off == true) {
					button.setText("Discoverable Off");

					Intent discoverableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(
							BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					startActivityForResult(discoverableIntent, REQUEST_BT_DISC_ON);
				} else {
					button.setText("Discoverable On");

				}
				button_find_dev_off = !button_find_dev_off;
			}
		});

		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		registerReceiver(mReceiver, filter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			TextView bt_enabled = (TextView) findViewById(R.id.BT_enabled);
			Log.d(logPrefix, "in onActivityResult( REQUEST_ENABLE_BT )");
			switch (resultCode) {
			case RESULT_OK:
				bt_enabled.setText("Bluetooth enable request is accepted");
				bt_is_on = true;
				break;
			case RESULT_CANCELED:
				bt_enabled.setText("Bluetooth enable request is denied");
				bt_is_on = false;
				break;
			}
		case REQUEST_BT_DISC_ON:
			if(resultCode == RESULT_CANCELED)
			{
				Log.e(logPrefix, "onActivityResult(REQUEST_BT_DISC_ON) returns RESULT_CANCELED");
			}
			else
			{
				Log.d(logPrefix, "Discoverability turned on for " + resultCode + " seconds");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d(logPrefix, "in onDestroy()");

		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(logPrefix, "in onPause()");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(logPrefix, "in onResume()");

		mainfunction();
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d(logPrefix, "in onStart()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.d(logPrefix, "in onRestart()");
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.d(logPrefix, "in onStop()");
	}
	

	private class GetConnectedBtSocketTask extends AsyncTask<input, Void , BluetoothSocket> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected BluetoothSocket doInBackground(input... urls) {
	        
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(BluetoothSocket result) {
	        mImageView.setImageBitmap(result);
	    }
	}
}
