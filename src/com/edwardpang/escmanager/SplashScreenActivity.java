package com.edwardpang.escmanager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
	private static final String	TAG = "SplashScreenActivity";
	private static final int	PRIVATE_CONST_REQUEST_ENABLE_BT = 0x0BEEF001;
	
	BluetoothAdapter	mBtAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
			SplashScreenActivity.this.finish();
		}
        Log.d(TAG, "Bluetooth Adaptor is available");
		
		if (!mBtAdapter.isEnabled ( )) {
	        Log.d(TAG, "Bluetooth Adaptor is disabled");
        	Intent intentBtEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(intentBtEnable, PRIVATE_CONST_REQUEST_ENABLE_BT);
        }
		else {
			Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
	        Log.d(TAG, "Bluetooth Adaptor is enabled");
		}
		
        /****** Create Thread that will sleep for 5 seconds *************/        
       Thread background = new Thread() {
           public void run() {
                
               try {
                   sleep(3000);
            	   while (!mBtAdapter.isEnabled());
                    
                   // After 5 seconds redirect to another intent
                   Intent i=new Intent(getBaseContext(),MainActivity.class);
                   startActivity(i);
                    
                   //Remove activity
                   finish();
                    
               } catch (Exception e) {
                
               }
           }
       };
       // start thread
       background.start();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PRIVATE_CONST_REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
			}
			else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Bluetooth is NOT enabled, leaving application...", Toast.LENGTH_LONG).show();
				SplashScreenActivity.this.finish();
			}
		}
	}
}
