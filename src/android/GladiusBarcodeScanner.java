package com.eyc.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.IntentFilter;
import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues;
import com.hsm.barcode.SymbologyConfig;

public class GladiusBarcodeScanner extends CordovaPlugin {
	
	private static String TAG = "BarcodeScanner";
	private static final String START_LISTENER = "startBarcodeListener";
	private static final String STOP_LISTENER = "stopBarcodeListener";
	private static final String START_SCAN = "startScanning";
    private static final int decodeTimeout = 2000; 


	private static CallbackContext scanReceiveCb;
	private static Decoder scanDecoder = null;
    
    private static Activity mActivity = null;
    
//	private DecodeResult decodeResult = null;
	//private GladiusScanReceiver scanReceiver = null;
	
    public static final int barcode=0x7f040000;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    if (START_LISTENER.equals(action)) {
	    	if (scanDecoder != null){ 
	    		return true; //listener already called, do nothing
	    	}
	    	scanDecoder = new Decoder();
			if (scanDecoder == null) {
				return false;
			}
			Log.d(TAG, "execute : "+action);
			scanDecoder.connectToDecoder();
	        scanReceiveCb = callbackContext;
			
			
			//for check bit	EAN13	
	        
			SymbologyConfig conf = new SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13);              
			scanDecoder.getSymbologyConfig(conf,false);
			conf.Mask = DecoderConfigValues.SymbologyFlags.SYM_MASK_FLAGS;
			conf.Flags = DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT | DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_ENABLE;
	        scanDecoder.setSymbologyConfig(conf);
	        scanDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_EAN13);  
			//scanDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_ALL);
			
	        /*
		    //for enable QR code decoder	    
			SymbologyConfig symconf = new SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_QR);
			scanDecoder.getSymbologyConfig(symconf,false);   	    
			symconf.Mask = DecoderConfigValues.SymbologyFlags.SYM_MASK_FLAGS;
			symconf.Flags = DecoderConfigValues.SymbologyFlags.SYMBOLOGY_ENABLE;
			scanDecoder.setSymbologyConfig(symconf);
			scanDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_QR);
			*/
	        
			/*if (this.scanReceiver == null) {
	    		Log.d(TAG, "Instantiate Scan Receiver");
				this.scanReceiver = new GladiusScanReceiver();
				this.scanReceiver.setCallingListener(this);
				IntentFilter ifMiki = new IntentFilter();
				ifMiki.addAction("com");
				//ifMiki.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
				Intent tmp = this.cordova.getActivity().registerReceiver(this.scanReceiver,ifMiki);
	    		Log.d(TAG, "Scan Receiver created successfully : "+tmp);
			}*/

			//mActivity = this.cordova.getActivity();
	        return true;
	    }
	    else if (START_SCAN.equals(action)){ //possibility to call the scan directly
	    	Log.d(TAG, "execute : "+action);
	    	return startScanning(callbackContext);
	    }
	    else if (STOP_LISTENER.equals(action)) {
	    	Log.d(TAG, "execute : "+action);
	    	destroy();
	    	return true;
	    }
	    return false;  // Returning false results in a "MethodNotFound" error.
	}

	
	public static boolean startScanning(Activity callingActivity){
        mActivity = callingActivity;
		return startScanning(scanReceiveCb);
	}

    private static boolean startScanning(CallbackContext scanReceivedCb) {

		Log.d(TAG, "Start scanning");
		if (scanDecoder == null) {
			Log.d(TAG, "scanDecoder is null");
			return false;
		}
		

//		decodeResult = new DecodeResult();
//		scanDecoder.waitForDecodeTwo(decodeTimeout, decodeResult);
		
		scanDecoder.waitForDecode(decodeTimeout);
		
		int length = scanDecoder.getLength();
		if(length > 0) {
    		Log.d(TAG, "decode success");
    		
    		byte [] str_arry = scanDecoder.getBarcodeByteData();
    		if (str_arry == null) {
    			return false; 
    		}
    		
    		String decodedata = EncodingUtils.getString(str_arry, "UTF8");	

         	PluginResult result = new PluginResult(PluginResult.Status.OK,decodedata);
			result.setKeepCallback(true);
			scanReceivedCb.sendPluginResult(result);
			
			//send barcode result.
			Intent sendIntent = BarcodeScanner.getBroadCastIntent(PluginResult.Status.OK, decodedata, true);
			LocalBroadcastManager.getInstance(mActivity.getApplicationContext()).sendBroadcast(sendIntent);
			return true;
    	}
    	else {
    		return false;
    	}	
    	
    }  
    
    
     
     @Override
     public void onDestroy() {
    	 Log.d(TAG, "onDestroy");
    	 destroy();
    	 super.onDestroy();
     }
	
     private void destroy(){
 		if (scanDecoder != null) {
 			scanDecoder.disconnectFromDecoder();
		}
 		scanDecoder = null;
        //this.cordova.getActivity().unregisterReceiver(scanReceiver);
     }
}
