package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MonitorFragment extends Fragment {
	private static final String	TAG = "MonitorFragment";

	OnMonitorFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnMonitorFragmentListener {
        public void onFragmentEventHandler(String str);
        public int getBluetoothChatServiceState ( );
        public boolean isBluetoothChatServiceBusy ( );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMonitorFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMonitorFragmentListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_monitor, container, false);

		Thread background = new Thread() {
			public void run() {
				try {
					while (mCallback.getBluetoothChatServiceState ( ) != BluetoothChatService.STATE_CONNECTED);
					while (mCallback.isBluetoothChatServiceBusy ( ));
		        	mCallback.onFragmentEventHandler ("AT+TEMP?");
				} catch (Exception e) {
				}
			}
		};
		// start thread
		background.start();
		return v;
	}
}