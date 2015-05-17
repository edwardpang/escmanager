package com.edwardpang.escmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ProtocolTestingFragment extends Fragment {
	private static final String	TAG = "ProtocolTestingFragment";
	private Spinner spinnerProtocolTestingEpCmd;

	OnProtocolTestingFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnProtocolTestingFragmentListener {
        public void onFragmentEventHandler(String str);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnProtocolTestingFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProtocolTestingFragmentListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_protocol_testing, container, false);
		
		spinnerProtocolTestingEpCmd = (Spinner) v.findViewById(R.id.spinnerProtocolTestingEpCmd);
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.ep_cmd_hello));
		list.add(getString(R.string.ep_cmd_fwup_start));
		list.add(getString(R.string.ep_cmd_get_hw_ver));
		list.add(getString(R.string.ep_cmd_get_sw_ver));
		list.add(getString(R.string.ep_cmd_get_param));
		list.add(getString(R.string.ep_cmd_set_param));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerProtocolTestingEpCmd.setAdapter(dataAdapter);
		
		final Button btnProtocolTestingSend = (Button) v.findViewById(R.id.btnProtocolTestingSend);
		btnProtocolTestingSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFragmentEventHandler (String.valueOf(spinnerProtocolTestingEpCmd.getSelectedItem()));
			}
		});
		return v;
	}	
}
