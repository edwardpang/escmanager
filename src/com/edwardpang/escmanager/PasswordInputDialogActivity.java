package com.edwardpang.escmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordInputDialogActivity extends Activity {
	private static final String	TAG = "PasswordInputDialogActivity";

	EditText	etEscPasswordOld, etEscPasswordNew1, etEscPasswordNew2;
	String		mOldPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_input_dialog);
		
		Bundle b = getIntent().getExtras();
		String pwd = b.getString("OldPassword");
		mOldPassword = pwd;

		Log.d(TAG, "onCreate mOldPassword:" + mOldPassword);

		etEscPasswordOld = (EditText) this.findViewById (R.id.etEscPasswordOld);
		etEscPasswordNew1 = (EditText) this.findViewById (R.id.etEscPasswordNew1);
		etEscPasswordNew2 = (EditText) this.findViewById (R.id.etEscPasswordNew2);
		
		final Button btnEscPasswordModify = (Button) findViewById(R.id.btnEscPasswordModify);
		btnEscPasswordModify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String old = etEscPasswordOld.getText().toString();
            	String new1 = etEscPasswordNew1.getText().toString();
            	String new2 = etEscPasswordNew2.getText().toString();
            	
            	Log.i (TAG, "Input Password Old:" + old + 
            			" New1:" + new1 + " New2:" + new2);

            	if (!mOldPassword.equals(old)) {
            		Toast.makeText(getApplicationContext(), "Incorrect OLD password", Toast.LENGTH_LONG).show();
            	}
            	else if (new1.length() == 0){
            		Toast.makeText(getApplicationContext(), "Password1 is empty", Toast.LENGTH_LONG).show();
            	}
            	else if (new2.length() == 0){
            		Toast.makeText(getApplicationContext(), "Password2 is empty", Toast.LENGTH_LONG).show();
            	}
            	else if (!new1.equals(new2)) {
            		Toast.makeText(getApplicationContext(), "New Password mismatched", Toast.LENGTH_LONG).show();
            	}
            	else {
                		Intent resultData = new Intent ( );
                		resultData.putExtra("NewPassword", new1);
        				setResult (Activity.RESULT_OK, resultData);
        				PasswordInputDialogActivity.this.finish ( );
            	}
            }
        });
        
		final Button btnEscPasswordCancel = (Button) findViewById(R.id.btnEscPasswordCancel);
		btnEscPasswordCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent resultData = new Intent ( );
        		resultData.putExtra("NewPassword", "");
				setResult (Activity.RESULT_CANCELED, resultData);
				PasswordInputDialogActivity.this.finish ( );
            }
        });
	}
}
