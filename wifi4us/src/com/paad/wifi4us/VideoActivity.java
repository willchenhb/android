package com.paad.wifi4us;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

public class VideoActivity extends Activity {
	
	private Button button_sound;
	private Button button_interest;
	private TextView counttime;
	private MyVideoView video;
	private MediaController controller;
	private Activity currentActivity;
	private String adid;
	private String adword;
	
    //Receive Service 	
    private ReceiveService receiveService;
	private boolean haveBondService;
	private ServiceConnection sc = new ServiceConnection() {
        @Override  
        public void onServiceDisconnected(ComponentName arg0) {  
        	haveBondService = false;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	receiveService = ((ReceiveService.MyBinder)binder).getService();
        	haveBondService = true;
        }  
    };  

	public void onStart(){
		super.onStart();
		Intent intent = new Intent(this, ReceiveService.class);  
        //bind service to get ready for all the clickable element
		bindService(intent, sc, Context.BIND_AUTO_CREATE); 
	}
	
	public void onStop(){
		super.onStop();
		unbindService(sc);
	}
	
	
    public void onBackPressed() {  
    	if(!haveBondService){
    		return;
    	}
    	super.onBackPressed();
    	receiveService.WifiDisconnect();
    }  
    
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_video);
        currentActivity = this;
        adid = getIntent().getStringExtra("adid");
        adword = getIntent().getStringExtra("adword");

        
        
        button_sound = (Button)findViewById(R.id.receive_button_ad_sound_button);
        button_sound.getBackground().setAlpha(100);
        button_sound.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
	            Toast.makeText(VideoActivity.this, "sound click", Toast.LENGTH_SHORT).show();
			}
        });
        
        button_interest = (Button)findViewById(R.id.receive_button_ad_interest_button);
        button_interest.setText(adword);
        button_interest.getBackground().setAlpha(100);
        button_interest.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
	            Toast.makeText(VideoActivity.this, "interest click", Toast.LENGTH_SHORT).show();
			}
        });
        
        
        counttime = (TextView)findViewById(R.id.receive_text_ad_timecount);  
        MyCount mc = new MyCount(30000, 1000);  
        mc.start();  
    
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        video = (MyVideoView)findViewById(R.id.videoview);
        video.SetCurrentState(this,new DisplayMetrics());
        controller = new MediaController(this);
        controller.setVisibility(View.INVISIBLE); 
        video.setMediaController(controller);
        video.setVideoPath(Environment.getExternalStorageDirectory().toString() + "/wifi4us/ad_" + adid + ".3gp");
        video.start();
        
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
        	public void onCompletion(MediaPlayer mp){
        		Editor sharedata = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE).edit(); 
        		sharedata.putBoolean("FINISH_VIDEO", true);
        		sharedata.putBoolean("STATE_RECEIVE", true);
        		sharedata.commit();
        		currentActivity.finish();
        	}
        });
    }
    
    class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        @Override     
        public void onFinish() {     
        	counttime.setText("���");        
        }     
        @Override     
        public void onTick(long millisUntilFinished) {     
        	counttime.setText(Long.toString(millisUntilFinished / 1000));     
        }    
    }     
}
