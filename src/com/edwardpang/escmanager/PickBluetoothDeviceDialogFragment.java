package com.edwardpang.escmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class PickBluetoothDeviceDialogFragment extends DialogFragment {
	private static final String	TAG = "PickBluetoothDeviceDialog";
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_title_pick_bluetooth_device)
               .setPositiveButton(R.string.dialog_button_scan_device, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Log.d (TAG, "Scan BT Device is clicked");
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
