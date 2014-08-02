package com.edwardpang.escmanager;

import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String	TAG = "MainActivity";
	private static final boolean D = true;
	private static final int	PRIVATE_CONST_REQUEST_ENABLE_BT = 0x0BEEF001;
	private static final int	PRIVATE_CONST_SELECT_BLUETOOTH_DEVICE = 0x0BEEF002;
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	BluetoothAdapter		mBtAdapter;
	Set<BluetoothDevice>	mBtAdapterBondedDevices;
	
	Menu					mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
        
		if (!mBtAdapter.isEnabled()) {
	        Log.d(TAG, "Bluetooth Adaptor is disabled");
        	Intent intentBtEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(intentBtEnable, PRIVATE_CONST_REQUEST_ENABLE_BT);
		}
		else {
			Intent dialogIntent = new Intent (this, SelectBluetoothDeviceDialogActivity.class);
        	startActivityForResult (dialogIntent, PRIVATE_CONST_SELECT_BLUETOOTH_DEVICE);
		}
        
	}

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, PRIVATE_CONST_REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.action_settings_select_device) {
			Intent dialogIntent = new Intent (this, SelectBluetoothDeviceDialogActivity.class);
        	startActivityForResult (dialogIntent, PRIVATE_CONST_SELECT_BLUETOOTH_DEVICE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PRIVATE_CONST_REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
			}
			else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Bluetooth is NOT enabled, leaving application...", Toast.LENGTH_LONG).show();
				MainActivity.this.finish();
			}
		}
		else if (requestCode == PRIVATE_CONST_SELECT_BLUETOOTH_DEVICE) {
			if (resultCode == RESULT_OK) {
				String name = data.getStringExtra("name");
				String address = data.getStringExtra("address");
				
				Toast.makeText(this, "Device name " + name + " address " + address + " is selected", Toast.LENGTH_SHORT).show();
				BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
				mChatService.connect(device, false);
			}
		}
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_monitor).toUpperCase(l);
			case 1:
				return getString(R.string.title_section_general_setting).toUpperCase(l);
			case 2:
				return getString(R.string.title_section_motor_timing_setting).toUpperCase(l);
			case 3:
				return getString(R.string.title_section_other_setting).toUpperCase(l);
			case 4:
				return getString(R.string.title_section_testing).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                	mMenu.findItem (R.id.action_connection_status).setIcon(R.drawable.ic_action_bluetooth_connected);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	mMenu.findItem (R.id.action_connection_status).setIcon(R.drawable.ic_action_bluetooth_searching);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	mMenu.findItem (R.id.action_connection_status).setIcon(R.drawable.ic_action_bluetooth);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                //mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                //Toast.makeText(getApplicationContext(), "Connected to "
                //               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
}
