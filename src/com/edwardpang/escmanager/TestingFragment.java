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
				Log.i (TAG, "APP: AT");
				mCallback.onFragmentEventHandler ("AT");
			}
		});
		return v;
	}
}
