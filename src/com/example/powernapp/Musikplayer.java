package com.example.powernapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Musikplayer extends Activity {
	
	ActionBar actionBar;
	ListView lv_songs;
	MediaPlayer mediaPlayer;
	SeekBar sb_volume;
	AudioManager audioManager;
	List<Song> mySongs = new ArrayList<Song>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musikplayer);
		getActionBar().setTitle("Musikplayer");
		
		mediaPlayer = new MediaPlayer();
		lv_songs = (ListView) findViewById(R.id.lv_songs);
		
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
		
		/* adding the powernApp-songs to the list */	
		mySongs.add(new Song("powernApp", "Song 1", "230", true));
		mySongs.add(new Song("powernApp", "Song 2", "560", true));
		mySongs.add(new Song("powernApp", "Song 3", "340", true));
		
		/* get all the music files from the phone */
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		String[] projection = {
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DURATION
		};
		
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
		while(cursor.moveToNext())  {
			mySongs.add(new Song(cursor.getString(0), cursor.getString(1), cursor.getString(2), false));
		}
		
		ArrayAdapter<Song> adapter = new MyListAdapter();		
		lv_songs.setAdapter(adapter);
		
		lv_songs.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int which,
					long arg3) {
				
				/* if the song is a powernApp-song, then play it directly from the raw folder */
				if(mySongs.get(which).getPowernAppSong())  {
					playSongFromRaw(mySongs.get(which).getName());
				}	
				
				else  {
					
				}
			}
	    });
	}
	
	private class MyListAdapter extends ArrayAdapter<Song>  {
		public MyListAdapter()  {
			super(getApplicationContext(), R.layout.songitem_view, mySongs);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if(itemView == null)  {
				itemView = getLayoutInflater().inflate(R.layout.songitem_view, parent, false);
			}
			
			Song currentSong = mySongs.get(position);
			
			TextView tv_name = (TextView) itemView.findViewById(R.id.item_name);
			tv_name.setText(currentSong.getName());
			
			TextView tv_artist = (TextView) itemView.findViewById(R.id.item_artist);
			tv_artist.setText(currentSong.getArtist());
			
			TextView tv_duration = (TextView) itemView.findViewById(R.id.item_duration);
			String duration = currentSong.getDuration();
			duration = duration.substring(0, 3);		
			int minutes = Integer.valueOf(duration);
			int seconds = minutes % 60;
			minutes /= 60;
			duration = minutes + ":" + seconds;		
			tv_duration.setText(duration);
			
			return itemView;
		}
	}

	private void initSeekBar()  {
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
		
		if(index[1].equals("1"))
			afd = this.getResources().openRawResourceFd(R.raw.powernapp1);
		
		else if(index[1].equals("2"))
			afd = this.getResources().openRawResourceFd(R.raw.powernapp2);
			
		else if(index[1].equals("3"))
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

class Song {
	
	private String artist;
	private String name;
	private String duration;
	private boolean powernAppSong;
	
	public Song(String artist, String name, String duration, boolean powernAppSong)  {
		super();
		this.artist = artist;
		this.name = name;
		this.duration = duration;
		this.powernAppSong = powernAppSong;
	}
	
	public String getArtist()  {
		return artist;
	}
	
	public String getName()  {
		return name;
	}
	
	public String getDuration()  {
		return duration;
	}
	
	public boolean getPowernAppSong()  {
		return powernAppSong;
	}
}
