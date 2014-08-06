package com.edwardpang.escmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameInputDialogActivity extends Activity {
	private static final String	TAG = "NameInputDialogActivity";

	EditText	etEscName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name_input_dialog);

		etEscName = (EditText) this.findViewById (R.id.etEscName);
		
		final Button btnEscNameModify = (Button) findViewById(R.id.btnEscNameModify);
		btnEscNameModify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String name = etEscName.getText().toString();
            	if (name.length() > 0) {
	            	Log.i (TAG, "Input Name :" + name);
	
	        		Intent resultData = new Intent ( );
	        		resultData.putExtra("NewName", name);
					setResult (Activity.RESULT_OK, resultData);
					NameInputDialogActivity.this.finish ( );
            	}
            	else {
            		Toast.makeText(getApplicationContext(), "Name is empty", Toast.LENGTH_LONG).show();
            	}
            }
        });
        
		final Button btnEscNameCancel = (Button) findViewById(R.id.btnEscNameCancel);
		btnEscNameCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent resultData = new Intent ( );
        		resultData.putExtra("NewName", "");
				setResult (Activity.RESULT_CANCELED, resultData);
				NameInputDialogActivity.this.finish ( );
            }
        });
	}
}
