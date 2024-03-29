package com.edwardpang.escmanager;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements 
	MonitorFragment.OnMonitorFragmentListener,
	OtherSettingFragment.OnOtherSettingFragmentListener,
	CommandTestingFragment.OnCommandTestingFragmentListener,
	ProtocolTestingFragment.OnProtocolTestingFragmentListener,
	FirmwareUpdateFragment.OnFirmwareUpdateFragmentListener{

	private static final String	TAG = "MainActivity";
	private static final boolean D = false;
	private static final int	PRIVATE_CONST_REQUEST_ENABLE_BT = 0x0BEEF001;
	private static final int	PRIVATE_CONST_SELECT_BLUETOOTH_DEVICE = 0x0BEEF002;
	private static final int	PRIVATE_CONST_CREATE_ESC_PASSWORD_DIALOG = 0x0BEEF003;
	private static final int	PRIVATE_CONST_CREATE_ESC_NAME_DIALOG = 0x0BEEF004;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	
	BluetoothAdapter		mBtAdapter;
	Set<BluetoothDevice>	mBtAdapterBondedDevices;
	
	Menu					mMenu;
	boolean					mChatServiceBusy = false;
	String					mLastSendMessage = "";
	String					mOldPassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		setContentView(mViewPager);
	
		final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_monitor),
                MonitorFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_general_setting),
                GeneralSettingFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_motor_timing_setting),
                ConfigFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_other_setting),
                OtherSettingFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_command_testing),
                CommandTestingFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_protocol_testing),
                ProtocolTestingFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.title_section_firmware_update),
        		FirmwareUpdateFragment.class, null);

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }

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
		else if (id == R.id.action_settings_about) {
			Intent dialogIntent = new Intent (this, AboutDialogActivity.class);
        	startActivity (dialogIntent);
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
		else if (requestCode == PRIVATE_CONST_CREATE_ESC_NAME_DIALOG) {
			if (resultCode == RESULT_OK) {
				String strNewName = data.getStringExtra("NewName");
				onFragmentEventHandler (getString(R.string.at_cmd_set_name) + strNewName);
			}
			else
				Log.i (TAG, "Name Dialog is cancelled");
		}
		else if (requestCode == PRIVATE_CONST_CREATE_ESC_PASSWORD_DIALOG) {
			if (resultCode == RESULT_OK) {
				String strNewPassword = data.getStringExtra("NewPassword");
				onFragmentEventHandler (getString(R.string.at_cmd_set_pin) + strNewPassword);
			}
			else
				Log.i (TAG, "Password Dialog is cancelled");
		}
	}
	
    public static class TabsAdapter extends FragmentPagerAdapter
    	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		
		static final class TabInfo {
		    private final Class<?> clss;
		    private final Bundle args;
		
		    TabInfo(Class<?> _class, Bundle _args) {
		        clss = _class;
		        args = _args;
		    }
		}
		
		public TabsAdapter(Activity activity, ViewPager pager) {
		    super(activity.getFragmentManager());
		    mContext = activity;
		    mActionBar = activity.getActionBar();
		    mViewPager = pager;
		    mViewPager.setAdapter(this);
		    mViewPager.setOnPageChangeListener(this);
		}
		
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		    TabInfo info = new TabInfo(clss, args);
		    tab.setTag(info);
		    tab.setTabListener(this);
		    mTabs.add(info);
		    mActionBar.addTab(tab);
		    notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
		    return mTabs.size();
		}
		
		@Override
		public Fragment getItem(int position) {
		    TabInfo info = mTabs.get(position);
		    return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}
		
		@Override
		public void onPageSelected(int position) {
		    mActionBar.setSelectedNavigationItem(position);
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
		
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
		    Object tag = tab.getTag();
		    for (int i=0; i<mTabs.size(); i++) {
		        if (mTabs.get(i) == tag) {
		            mViewPager.setCurrentItem(i);
		        }
		    }
		}
		
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

    public void onFragmentEventHandler(String str) {
    	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
    		Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    	}
    	else {
	    	mChatServiceBusy = true;
	    	Log.d (TAG, "onFragmentEventHandler: " + str);
	    	mLastSendMessage = str;
	    	sendMessage (str);
    	}
    }

    public int getBluetoothChatServiceState () {
    	return mChatService.getState();
    }
    
    public boolean isBluetoothChatServiceBusy () {
    	return mChatServiceBusy;
    }

    public void createInputNameDialog () {
    	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
    		Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    	}
    	else {
			Intent dialogIntent = new Intent (this, NameInputDialogActivity.class);
	    	startActivityForResult (dialogIntent, PRIVATE_CONST_CREATE_ESC_NAME_DIALOG);
    	}
    }
    
    public void createInputPasswordDialog () {
    	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
    		Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
    	}
    	else {
			Intent i = new Intent (this, PasswordInputDialogActivity.class);
			i.putExtra("OldPassword", mOldPassword);
	    	startActivityForResult (i, PRIVATE_CONST_CREATE_ESC_PASSWORD_DIALOG);
    	}
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
    
    private void receiveMessage(String message) {
        // Check that there's actually something to send
        if (message.length() > 0) {
        	if (message.contains("OK+TEMP:")) {
        		MonitorFragment f = (MonitorFragment) getFragmentManager().findFragmentByTag ("android:switcher:"+R.id.pager+":0");
        		if(f != null) {
        	         if(f.getView() != null) {
        	        	 TextView tv = (TextView) f.getView().findViewById(R.id.tvTabMonitorRowAmbientTempValue);
        	        	 tv.setText(message.split(":")[1]);
        	         }
        		}
        	}
        	else if (message.contains("OK+NAME:")) {
        		OtherSettingFragment f = (OtherSettingFragment) getFragmentManager().findFragmentByTag ("android:switcher:"+R.id.pager+":3");
        		if(f != null) {
        	         if(f.getView() != null) {
        	        	 TextView tv = (TextView) f.getView().findViewById(R.id.tvTabOtherSettingRowEscNameValue);
        	        	 tv.setText(message.split(":")[1]);
        	         }
        		}
        	}
        	else if (message.contains("OK+PIN:")) {
        		mOldPassword = message.split(":")[1];
        	}
        	else if (message.contains("OK+FWUP+START")) {
        		FirmwareUpdateFragment f = (FirmwareUpdateFragment) getFragmentManager().findFragmentByTag ("android:switcher:"+R.id.pager+":6");
        		if(f != null) {
        			//TextView tv = (TextView) f.getView().findViewById(R.id.tvFirmwareUpdateStatus);
        			//tv.setText(tv.getText() + "OK\n");
        			f.setFwupState(FirmwareUpdateStateEnum.FWUP_STATE_RECV_START_ACK);
        		}
        	}
        	else if (message.contains("OK+FWUP+BLK:")) {
        		FirmwareUpdateFragment f = (FirmwareUpdateFragment) getFragmentManager().findFragmentByTag ("android:switcher:"+R.id.pager+":6");
        		if(f != null) {
        			int n = Integer.parseInt (message.split(":")[1]);
        			f.setAckBlockNum(n);
        			f.setFwupState(FirmwareUpdateStateEnum.FWUP_STATE_RECV_FILE_CONTENT_ACK);
        			TextView tv = (TextView) f.getView().findViewById(R.id.tvFirmwareUpdateStatus);
        			tv.append("BLOCK " + n + " is sent\n");
        		}
        	}
        	else if (message.contains(getString(R.string.at_cmd_set_response))) {
        		Log.i (TAG, "Tx: " + mLastSendMessage + " Rx: " + message);
        		if (mLastSendMessage.contains(getString(R.string.at_cmd_set_name))) {
	        		OtherSettingFragment f = (OtherSettingFragment) getFragmentManager().findFragmentByTag ("android:switcher:"+R.id.pager+":3");
	        		if(f != null) {
	        	         if(f.getView() != null) {
	        	        	 TextView tv = (TextView) f.getView().findViewById(R.id.tvTabOtherSettingRowEscNameValue);
	        	        	 tv.setText(message.split(":")[1]);
	        	         }
	        		}
        		}
        		else if (mLastSendMessage.contains(getString(R.string.at_cmd_set_pin))) {
        			mOldPassword = message.split(":")[1];
        		}
        	}
        	mChatServiceBusy = false;
        }
    }
    
    @SuppressLint("HandlerLeak") private final Handler mHandler = new Handler() {
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
                Log.d (TAG, "Me:  " + writeMessage);
                if (D) Toast.makeText(getApplicationContext(), "Me:  " + writeMessage, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                Log.d (TAG, mConnectedDeviceName + ":  " + readMessage);
            	if(D) Toast.makeText(getApplicationContext(), mConnectedDeviceName+":  " + readMessage, Toast.LENGTH_SHORT).show();
                receiveMessage (readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
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
