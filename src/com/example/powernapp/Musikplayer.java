package com.example.powernapp;

import java.io.IOException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Musikplayer extends Activity {
	
	ActionBar actionBar;
	ListView lv_songs;
	MediaPlayer mediaPlayer;
	ArrayAdapter<String> songList;
	SeekBar sb_volume;
	AudioManager audioManager;
	Button btn_playpause, btn_stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musikplayer);
		getActionBar().setTitle("Musikplayer");
		
		mediaPlayer = new MediaPlayer();
		lv_songs = (ListView) findViewById(R.id.lv_songs);
		songList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
		btn_playpause = (Button) findViewById(R.id.btn_playpause);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		initSeekBar();
		
		/* Thread for updating the seekBar if the side-keys were pressed */
		new Thread(new Runnable() { 
	        @Override
	        public void run() {
	        	while (true) {
	        		try {
	                    sb_volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)); 
						Thread.sleep(100);						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
	        }
	    }).start();
		
		/* adding the songs to the list */
		songList.add("powernApp - Song 1");
		songList.add("powernApp - Song 2");
		songList.add("powernApp - Song 3");
		
		lv_songs.setAdapter(songList);
		
		lv_songs.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int which,
					long arg3) {
				playSongFromRaw(songList.getItem(which));
			}
	    });
	}
	
	private void initSeekBar()
    {
        try
        {
        	sb_volume = (SeekBar)findViewById(R.id.sb_volume);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            
            sb_volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            sb_volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            
            sb_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				public void onStopTrackingTouch(SeekBar seekBar) {
					/* Do stuff in here */
				}
				
				public void onStartTrackingTouch(SeekBar seekBar) {
					/* Do stuff in here */
				}
				
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);					
				}
			});
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	private void playSongFromRaw(String file)  {

		String[] index = file.split(" ");
		AssetFileDescriptor afd = null;
		
		if(index[index.length-1].equals("1"))
			afd = this.getResources().openRawResourceFd(R.raw.powernapp1);
		
		else if(index[index.length-1].equals("2"))
			afd = this.getResources().openRawResourceFd(R.raw.powernapp2);
			
		else if(index[index.length-1].equals("3"))
			afd = this.getResources().openRawResourceFd(R.raw.powernapp3);
		
	    mediaPlayer.reset();
	    try {
	    	mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    mediaPlayer.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.musikplayer, menu);
		return true;
	}
	
	@Override
	protected void onResume()	{
		overridePendingTransition(0,0);
		super.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /* Handle item selection */
	    switch (item.getItemId()) {
	    case R.id.main:
	    	if(!Global.isConnected)	{
	    		Intent intent_home = new Intent(Musikplayer.this,MainActivity.class);
	    		startActivity(intent_home);
	    	}
	    	else	{
	    		Intent intent_connected = new Intent(Musikplayer.this,Connected.class);
	    		intent_connected.putExtra("caller", "help");
	    		startActivity(intent_connected);
	    	}
	        return true;
	    case R.id.tipps:
	    	Intent intent_tipps = new Intent(Musikplayer.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);	
	        return true;
	    case R.id.about_us:
	    	Intent intent_about = new Intent(Musikplayer.this,About_Us.class);
	        startActivity(intent_about);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
