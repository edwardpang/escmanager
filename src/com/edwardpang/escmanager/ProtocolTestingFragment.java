package com.edwardpang.escmanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProtocolTestingFragment extends Fragment {
	private static final String	TAG = "ProtocolTestingFragment";

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
		
		return v;
	}	
}
