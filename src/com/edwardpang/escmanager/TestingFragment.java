package com.edwardpang.escmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TestingFragment extends Fragment {
	private static final String	TAG = "TestingFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_testing, container, false);

		final Button btnAtCmdTest = (Button) v.findViewById(R.id.btnAtCmdTest);
		btnAtCmdTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i (TAG, "APP: AT");
			}
		});
		return v;
	}
}
