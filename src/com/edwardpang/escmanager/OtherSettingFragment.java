package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
				
				InputFilter[] filters = new InputFilter[1];
				filters[0] = new InputFilter.LengthFilter(PRIVATE_CONST_ESC_NAME_LENGTH_MAX);
				
				final EditText et = new EditText(getActivity());
				et.setLayoutParams(lp);
				et.setFilters(filters);

				AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
				ad.setCancelable(false);
				ad.setTitle (R.string.dialog_esc_name_title);
				ad.setMessage (R.string.dialog_esc_name_message);
				ad.setView(et);
				ad.setPositiveButton (R.string.dialog_esc_name_btn_modify, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							String str = et.getText().toString();
							if (str.length() <= PRIVATE_CONST_ESC_NAME_LENGTH_MAX && 
								str.length() >= PRIVATE_CONST_ESC_NAME_LENGTH_MIN) {
								Log.i (TAG, getString(R.string.dialog_esc_name_title) + " to " + str);
								mCallback.onFragmentEventHandler(getString(R.string.at_cmd_set_name) + str);
							}
							else {
								Log.i (TAG, "Invalid input length " + str + " (" + str.length() + "bytes)");
								Toast.makeText(getActivity(), str + " is too long, pleaes try again", Toast.LENGTH_LONG).show();
							}								
						}
					}
				);
				ad.setNegativeButton(R.string.dialog_esc_name_btn_cancel, 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								Log.i (TAG, getString(R.string.dialog_esc_name_title) + " is cancelled");
							}
						}
					);
				ad.show();	
					
			}
		});

		final Button btnTabOtherSettingRowEscPasswordAction = (Button) v.findViewById(R.id.btnTabOtherSettingRowEscPasswordAction);
		btnTabOtherSettingRowEscPasswordAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+PIN?");
			}
		});
		
		Thread background = new Thread() {
			public void run() {
				try {
					while (mCallback.getBluetoothChatServiceState ( ) != BluetoothChatService.STATE_CONNECTED);
					while (mCallback.isBluetoothChatServiceBusy ( ));
					mCallback.onFragmentEventHandler ("AT+NAME?");
		        	sleep(500);
		        	//mCallback.onFragmentEventHandler ("AT+PIN?");
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
