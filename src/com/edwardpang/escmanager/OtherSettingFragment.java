package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OtherSettingFragment extends Fragment {
	private static final String	TAG = "OtherSettingFragment";

	private static final int	PRIVATE_CONST_ESC_NAME_LENGTH_MIN = 1;
	private static final int	PRIVATE_CONST_ESC_NAME_LENGTH_MAX = 12;
	
	OnOtherSettingFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnOtherSettingFragmentListener {
        public void onFragmentEventHandler(String str);
        public int getBluetoothChatServiceState ( );
        public boolean isBluetoothChatServiceBusy ( );
        public void createInputPasswordDialog ( );
        public void createInputNameDialog ( );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnOtherSettingFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOtherSettingFragmentListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_other_setting, container, false);

		final Button btnAtCmdTest = (Button) v.findViewById(R.id.btnTabOtherSettingRowEscFwVersionAction);
		btnAtCmdTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT");
			}
		});
		
		final Button btnTabOtherSettingRowEscNameAction = (Button) v.findViewById(R.id.btnTabOtherSettingRowEscNameAction);
		btnTabOtherSettingRowEscNameAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.createInputNameDialog ( );
			}
		});

		final Button btnTabOtherSettingRowEscPasswordAction = (Button) v.findViewById(R.id.btnTabOtherSettingRowEscPasswordAction);
		btnTabOtherSettingRowEscPasswordAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.createInputPasswordDialog ( );
			}
		});
		
		Thread background = new Thread() {
			public void run() {
				try {
					while (mCallback.getBluetoothChatServiceState ( ) != BluetoothChatService.STATE_CONNECTED);
					while (mCallback.isBluetoothChatServiceBusy ( ));
					mCallback.onFragmentEventHandler (getString(R.string.at_cmd_get_name));
		        	sleep(200);
		        	mCallback.onFragmentEventHandler (getString(R.string.at_cmd_get_pin));
		        	//sleep(500);
				} catch (Exception e) {
				}
			}
		};
		// start thread
		background.start();
		
		return v;
	}

}
