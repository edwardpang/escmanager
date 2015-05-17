package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirmwareUpdateFragment extends Fragment{
	private static final String	TAG = "FirmwareUpdateFragment";
	private FirmwareUpdateStateEnum fwupState;

	OnFirmwareUpdateFragmentListener mCallback;

	public void setFwupState (FirmwareUpdateStateEnum state) {
		Log.i (TAG, "setFwupState (" + state.toString() + ")");
		fwupState = state;
	}
	
    // Container Activity must implement this interface
    public interface OnFirmwareUpdateFragmentListener {
        public void onFragmentEventHandler(String str);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFirmwareUpdateFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFirmwareUpdateFragmentListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_firmware_update, container, false);

		Thread fwupThread = new Thread() {
			public void run() {
				try {
					Log.i (TAG, "fwupThread begin");
					setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_INIT);
					while (fwupState != FirmwareUpdateStateEnum.FWUP_STATE_COMPLETED){ 
						switch (fwupState) {
							case FWUP_STATE_INIT:
								// Only button press can leave this state
								break;
		
							case FWUP_STATE_SEND_START:
								mCallback.onFragmentEventHandler (getString(R.string.ep_cmd_fwup_start));
								setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_WAIT_START_ACK);
								break;
								
							case FWUP_STATE_WAIT_START_ACK:
								break;
		
							case FWUP_STATE_RECV_START_ACK:
								setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_STARTED);
								break;
								
							case FWUP_STATE_STARTED:
								setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_COMPLETED_WITHOUT_ERROR);
								break;
								
							case FWUP_STATE_SEND_FILE_CONTENT:
								break;
								
							case FWUP_STATE_COMPLETED_WITH_ERROR:
							case FWUP_STATE_COMPLETED_WITHOUT_ERROR:
								setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_COMPLETED);
								break;
								
							case FWUP_STATE_COMPLETED:
								break;
								
							default:
								break;
						}
					}
					Log.i (TAG, "fwupThread end");
				} catch (Exception e) {
				}
			}
		};
		
		final Button btnSelectFile = (Button) v.findViewById(R.id.btnSelectFile);
		btnSelectFile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mCallback.onFragmentEventHandler (String.valueOf(spinnerProtocolTestingEpCmd.getSelectedItem()));
			}
		});
		
		final Button btnStart = (Button) v.findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setFwupState (FirmwareUpdateStateEnum.FWUP_STATE_SEND_START);
			}
		});
		
		fwupThread.start();		

		return v;
	}	
}
