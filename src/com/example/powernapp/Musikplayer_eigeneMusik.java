package com.example.powernapp;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Musikplayer_eigeneMusik extends Activity {

	MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musikplayer_eigene_musik);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.musikplayer_eigene_musik, menu);
		return true;
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

}
