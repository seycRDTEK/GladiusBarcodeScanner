package com.eyc.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GladiusScanReceiver extends BroadcastReceiver {
//
	private String TAG = "BarcodeScanner";
	private GladiusBarcodeScanner callingListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "received scan start");
		callingListener.startScanning();
		
	}

	public void setCallingListener(GladiusBarcodeScanner gbs){
		this.callingListener = gbs;
	}
	
	public void destroy(){
		this.callingListener = null;
	}
}