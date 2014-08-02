package com.edwardpang.escmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tab, container, false);
		TextView tv = (TextView) v.findViewById(R.id.msg);
		tv.setText(R.string.title_section_monitor);
		return v;
	}
}
