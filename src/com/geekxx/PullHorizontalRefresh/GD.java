package com.geekxx.PullHorizontalRefresh;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.Toast;

public class GD extends AndroidTestCase{
	
	public static final boolean isDebugMode = true;
	
	public static final String TAG = "CM";
	
	public static  void i(Object msg){
		if (isDebugMode) {
			Log.i(TAG, msg.toString());
		}
	}
	public  static void w(Object msg){
		if (isDebugMode) {
			Log.w(TAG, msg.toString());
		}
	}
	public  static void e(Object msg){
		if (isDebugMode) {
			Log.e(TAG, msg.toString());
		}
	}
	public static  void d(Object msg){
		if (isDebugMode) {
			Log.d(TAG, msg.toString());
		}
	}
	public static  void v(Object msg){
		if (isDebugMode) {
			Log.v(TAG, msg.toString());
		}
	}
	
	public static  void toast(Context c,Object msg){
		if (isDebugMode) {
			Toast.makeText(c, msg.toString(), 1).show();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
