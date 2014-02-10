package com.example.powernapp;

import java.io.File;
import java.io.IOException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Hilfen extends Activity {
	
	MediaPlayer mp;
	Button btn_play, btn_pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Einschlafhilfen");
		setContentView(R.layout.activity_hilfen);
		
		LoadPlayer();
		
		btn_play = (Button)findViewById(R.id.btn_play);
		btn_pause = (Button)findViewById(R.id.btn_pause);
		
		btn_play.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Play();
			}
    	});
		
		btn_pause.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Pause();
			}
    	});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hilfen, menu);
		return true;
	}
	
	@Override
	protected void onResume()	{
		overridePendingTransition(0,0);
		super.onResume();
	}
	
	public void LoadPlayer(){ 
	    mp = new MediaPlayer();
	 
	    try {
	        mp.setDataSource("/storage/sdcard0/Music/Snow.mp3");
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    try {
	        mp.prepare();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void Play()	{
		mp.start();   
	}
	
	public void Pause()	{
		mp.pause();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /* Handle item selection */
	    switch (item.getItemId()) {
	    case R.id.main:
	    	if(!ConnectedStatus.isConnected)	{
	    		Intent intent_home = new Intent(Hilfen.this,MainActivity.class);
	    		startActivity(intent_home);
	    	}
	    	else	{
	    		Intent intent_connected = new Intent(Hilfen.this,Connected.class);
	    		startActivity(intent_connected);
	    	}
	        return true;
	    case R.id.tipps:
	    	Intent intent_tipps = new Intent(Hilfen.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);	
	        return true;
	    case R.id.about_us:
	    	Intent intent_about = new Intent(Hilfen.this,About_Us.class);
	        startActivity(intent_about);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
