package com.edwardpang.escmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectBluetoothDeviceDialogActivity extends Activity {
	private static final String	TAG = "PickBluetoothDeviceDialog";
	
	BluetoothAdapter		mBtAdapter;

	ListView				mListViewBondedDevice;
	Set<BluetoothDevice>	mBtAdpaterBondedDevices;
	List<String> 			mBtAdpaterBondedDevicesStrList = new ArrayList<String>();
	ArrayAdapter<String>	mBtAdpaterBondedDevicesStrListAdapter;

	ListView				mListViewUnbondedDevice;
	Set<BluetoothDevice>	mBtAdpaterUnbondedDevices;
	List<String> 			mBtAdpaterUnbondedDevicesStrList = new ArrayList<String>();
	ArrayAdapter<String>	mBtAdpaterUnbondedDevicesStrListAdapter;
	
	ProgressDialog			mProgressDialog;
	
	/*********************************************************************
	 *	Private Function Section
	 */
	private String prepareBondedDeviceString (String name, String address) {
		return name + "\nAddress " + address;
	}

	private String[] splitBondedDeviceString (String str) {
		return str.split("\nAddress ");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_bluetooth_device_dialog);
		
	   	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    	mBtAdpaterBondedDevices = mBtAdapter.getBondedDevices();
		if (mBtAdpaterBondedDevices.size() > 0) {
			Log.d (TAG, "Paired Device Number is " + mBtAdpaterBondedDevices.size());
			for (BluetoothDevice device : mBtAdpaterBondedDevices) {
				mBtAdpaterBondedDevicesStrList.add(prepareBondedDeviceString (device.getName(), device.getAddress()));
				Log.d (TAG, "Bonded device " + device.getName() + " is found");
			}
		}
		else {
			String noDevices = getResources().getText(R.string.dialog_text_no_paired_device).toString();
			mBtAdpaterBondedDevicesStrList.add(noDevices);
		}
		
		mListViewBondedDevice = (ListView) this.findViewById (R.id.lvPairedDevicesList);
		mBtAdpaterBondedDevicesStrListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBtAdpaterBondedDevicesStrList);
		mListViewBondedDevice.setAdapter(mBtAdpaterBondedDevicesStrListAdapter);
		mListViewBondedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mBtAdapter.getState() != BluetoothAdapter.STATE_ON) {
					return;
				}

				if (mBtAdapter.isDiscovering())
					mBtAdapter.cancelDiscovery();
				
				String str = mBtAdpaterBondedDevicesStrList.get(arg2);
				if (str == null || str.equals(""))
					return;
				
				String[] values = splitBondedDeviceString (str);
				String address = values[1];
				
				Log.i (TAG, address + " is chosen");
				Intent resultData = new Intent ( );
				resultData.putExtra("name", values[0]);
				resultData.putExtra("address", values[1]);
				resultData.putExtra("paried", "true");
				setResult (Activity.RESULT_OK, resultData);
				SelectBluetoothDeviceDialogActivity.this.finish ( );
			} 
		});
		mBtAdpaterBondedDevicesStrListAdapter.notifyDataSetChanged();
		
		mListViewUnbondedDevice = (ListView) this.findViewById (R.id.lvUnpairedDevicesList);
		mBtAdpaterUnbondedDevicesStrListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBtAdpaterUnbondedDevicesStrList);
		mListViewUnbondedDevice.setAdapter(mBtAdpaterUnbondedDevicesStrListAdapter);
		mListViewUnbondedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mBtAdapter.getState() != BluetoothAdapter.STATE_ON) {
					return;
				}

				if (mBtAdapter.isDiscovering())
					mBtAdapter.cancelDiscovery();
				
				String str = mBtAdpaterUnbondedDevicesStrList.get(arg2);
				if (str == null || str.equals(""))
					return;
				
				String[] values = splitBondedDeviceString (str);
				String address = values[1];
				
				Log.i (TAG, address + " is chosen");
				Intent resultData = new Intent ( );
				resultData.putExtra("name", values[0]);
				resultData.putExtra("address", values[1]);
				resultData.putExtra("paried", "false");
				setResult (Activity.RESULT_OK, resultData);
				SelectBluetoothDeviceDialogActivity.this.finish ( );
			} 
		});
		mBtAdpaterUnbondedDevicesStrListAdapter.notifyDataSetChanged();	
		
		final Button button = (Button) findViewById(R.id.btnScanBluetoothDevice);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
	            if (!mBtAdapter.isDiscovering()) {
	            	mBtAdapter.startDiscovery();
		        	mProgressDialog = ProgressDialog.show(
		        			SelectBluetoothDeviceDialogActivity.this, 
		        			"Scanning Bluetooth Devices", 
		        			"Please wait...");
	            }
            }
        });

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
	}
	
	@Override
	public void onDestroy ( ) {
		super.onDestroy();
		
        if (mBtAdapter != null) {
        	if (mBtAdapter.isDiscovering())
        		mBtAdapter.cancelDiscovery();
        }
		this.unregisterReceiver(mReceiver);		
	}
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
	            	Log.d (TAG, "Unbonded device " + device.getName() + " is found");
	            	// Add the name and address to an array adapter to show in a ListView
		            String str = prepareBondedDeviceString (device.getName(), device.getAddress());
		            if (mBtAdpaterUnbondedDevicesStrList.indexOf(str) == -1) {
		            	mBtAdpaterUnbondedDevicesStrList.add(str);
		            	mBtAdpaterUnbondedDevicesStrListAdapter.notifyDataSetChanged ( );
		            }
	            }
	            else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
	            	Log.d (TAG, "Bonded device " + device.getName() + " is found");
	            	// Add the name and address to an array adapter to show in a ListView
		            String str = prepareBondedDeviceString (device.getName(), device.getAddress());
		            if (mBtAdpaterBondedDevicesStrList.indexOf(str) == -1) {
		            	mBtAdpaterBondedDevicesStrList.add(str);
		            	mBtAdpaterBondedDevicesStrListAdapter.notifyDataSetChanged ( );
		            }
	            }
	            else if (device.getBondState() == BluetoothDevice.BOND_BONDING)
	            	Log.d (TAG, "Bonding device " + device.getName() + " is found");
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	            Log.d (TAG, "Bluetooth Discovery is started");
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	            Log.d (TAG, "Bluetooth Discovery is finished");
	        	mProgressDialog.dismiss ();
	        }
	    }
	};
}
