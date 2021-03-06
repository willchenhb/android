package com.paad.wifi4us;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SendFragment extends Fragment{
	private FragmentManager fragmentManager;
	private Fragment send_id_start_share_button;
	private Fragment send_id_start_share_text;
	private Fragment send_id_stop_share_button;
	
	private WifiManager wifiManager;
	public int WIFI_AP_STATE_DISABLING;  
    public int WIFI_AP_STATE_DISABLED;  
    public int WIFI_AP_STATE_ENABLING;  
    public int WIFI_AP_STATE_ENABLED;  
    public int WIFI_AP_STATE_FAILED;
    
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		try{
			WIFI_AP_STATE_DISABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLING").get(wifiManager);
			WIFI_AP_STATE_DISABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLED").get(wifiManager);
			WIFI_AP_STATE_ENABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLING").get(wifiManager);
			WIFI_AP_STATE_ENABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager);
			WIFI_AP_STATE_FAILED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_FAILED").get(wifiManager);
		}catch(Exception e){
        	e.printStackTrace();
        }
		View view_res = inflater.inflate(R.layout.fragment_send, container, false);
		return view_res;
	}
	
	
	public void onStart(){
		super.onStart();
		int state = getWifiApState();
		if(state == WIFI_AP_STATE_ENABLING || state == WIFI_AP_STATE_ENABLED){
			UIStopSend();
		}else{
			UIStartSend();
		}
	}
	
	private void UIStartSend(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_start_share_button = fragmentManager.findFragmentByTag("send_id_start_share_button");
		if(send_id_start_share_button == null){
			send_id_start_share_button = new SendStartShareButton();
			 transaction.replace(R.id.send_container, send_id_start_share_button, "send_id_start_share_button");		
		}
		send_id_start_share_text = fragmentManager.findFragmentByTag("send_id_start_share_text");
		if(send_id_start_share_text == null){
			send_id_start_share_text = new SendStartShareText();
			transaction.replace(R.id.send_stateinfo_container, send_id_start_share_text, "send_id_start_share_text");
		}
		
		transaction.commitAllowingStateLoss();
	}
	
	private void UIStopSend(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_stop_share_button = new SendStopShareButton();
		transaction.replace(R.id.send_container, send_id_stop_share_button, "send_id_stop_share_button");
		transaction.commitAllowingStateLoss();
	}
	
	private int getWifiApState(){
        try {  
            Method method = wifiManager.getClass().getMethod("getWifiApState");  
            int i = (Integer) method.invoke(wifiManager);  
            return i;  
        } catch (Exception e) {  
            return WIFI_AP_STATE_FAILED;  
        }  
	}
}
