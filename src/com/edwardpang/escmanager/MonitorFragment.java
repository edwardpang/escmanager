package com.edwardpang.escmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MonitorFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab_monitor, container, false);
		
		TextView tv = (TextView) v.findViewById(R.id.tvTabMonitorRowAmbientTempValue);
		MainActivity act = (MainActivity) getActivity();
		tv.setText(act.getAmbientTemp());
		
		return v;
	}
}