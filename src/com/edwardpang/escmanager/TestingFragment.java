package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TestingFragment extends Fragment {
	private static final String	TAG = "TestingFragment";

	OnTestingFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnTestingFragmentListener {
        public void onFragmentEventHandler(String str);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTestingFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_testing, container, false);
		
		final Button btnAtCmdTest = (Button) v.findViewById(R.id.btnAtCmdTest);
		btnAtCmdTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT");
			}
		});
		
		final Button btnAtCmdGetBaud = (Button) v.findViewById(R.id.btnAtCmdGetBaud);
		btnAtCmdGetBaud.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+BAUD?");
			}
		});

		final Button btnAtCmdGetParityBit = (Button) v.findViewById(R.id.btnAtCmdGetParityBit);
		btnAtCmdGetParityBit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+CHK?");
			}
		});
		
		final Button btnAtCmdGetStopBit = (Button) v.findViewById(R.id.btnAtCmdGetStopBit);
		btnAtCmdGetStopBit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+STOP?");
			}
		});

		final Button btnAtCmdGetUart = (Button) v.findViewById(R.id.btnAtCmdGetUart);
		btnAtCmdGetUart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+UART?");
			}
		});
		
		final Button btnAtCmdModuleCheck = (Button) v.findViewById(R.id.btnAtCmdModuleCheck);
		btnAtCmdModuleCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+SECH?");
			}
		});

		final Button btnAtCmdAppCheck = (Button) v.findViewById(R.id.btnAtCmdAppCheck);
		btnAtCmdAppCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+APCH?");
			}
		});	
		
		final Button btnAtCmdGetTemp = (Button) v.findViewById(R.id.btnAtCmdGetTemp);
		btnAtCmdGetTemp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+TEMP?");
			}
		});

		final Button btnAtCmdGetName = (Button) v.findViewById(R.id.btnAtCmdGetName);
		btnAtCmdGetName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+NAME?");
			}
		});
		
		final Button btnAtCmdFactoryDefault = (Button) v.findViewById(R.id.btnAtCmdFactoryDefault);
		btnAtCmdFactoryDefault.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+DEFAULT");
			}
		});

		final Button btnAtCmdGetSwVersion = (Button) v.findViewById(R.id.btnAtCmdGetSwVersion);
		btnAtCmdGetSwVersion.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler ("AT+VERSION?");
			}
		});		
		return v;
	}	
}
