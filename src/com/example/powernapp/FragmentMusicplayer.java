package com.example.powernapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FragmentMusicplayer extends Fragment  {
	ListView lv_songs;
	MediaPlayer mediaPlayer;
	SeekBar sb_volume;
	AudioManager audioManager;
	List<Song> mySongs = new ArrayList<Song>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_musicplayer, container, false);
		
		mediaPlayer = new MediaPlayer();
		sb_volume = (SeekBar)view.findViewById(R.id.sb_volume);
		lv_songs = (ListView) view.findViewById(R.id.lv_songs);
		
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
		mySongs.add(new Song(0, "powernApp", "Song 1", "390", null, true));
		mySongs.add(new Song(1, "powernApp", "Song 2", "560", null, true));
		mySongs.add(new Song(2, "powernApp", "Song 3", "340", null, true));
		
		/* get all other music files from the phone */
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		String[] projection = {
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DURATION,
		        MediaStore.Audio.Media.DATA
		};
		
		int index = 3;
		Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
		while(cursor.moveToNext())  {
			mySongs.add(new Song(index, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), false));
			index++;
		}
		
		ArrayAdapter<Song> adapter = new MyListAdapter();		
		lv_songs.setAdapter(adapter);
		
        return view;
    }
	
	private class MyListAdapter extends ArrayAdapter<Song>  {
		public MyListAdapter()  {
			super(getActivity(), R.layout.songitem_view, mySongs);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final Song currentSong = mySongs.get(position);
			
			if(convertView == null)  {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.songitem_view, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.tv_name = (TextView) convertView.findViewById(R.id.item_name);
				viewHolder.tv_artist = (TextView) convertView.findViewById(R.id.item_artist);
				viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.item_duration);
				viewHolder.btn_playpause = (ImageButton) convertView.findViewById(R.id.item_button);
				
				viewHolder.btn_playpause.setOnClickListener(new View.OnClickListener() {			
					public void onClick(View v) {
						
						/* Song is going to be played */
						
						if(currentSong.clickCount % 2 == 0)  {
							/* if another song is playing --> reset the imageresource and increase clickCount */
							if(mediaPlayer.isPlaying())  {
								Global.btn_currentSong.setImageResource(R.drawable.play);
								Song previousSong = mySongs.get(Global.currentSong);
								previousSong.clickCount++;
							}
							
							/* same track as before */
							if(position==Global.currentSong)  {
								mediaPlayer.start();
							}
							
							else  {
								int position = Integer.valueOf((String)v.getTag());
								/* check if song is a powernApp-song -> if so, then play it directly from the raw folder */
								if(currentSong.isPowernAppSong())  {
									playSongFromRaw(mySongs.get(position).getName());
								}
								else  {
									/* play song from MediaStore */
									playSongFromMediaStore(currentSong.getPath());
								}
							}
							
							viewHolder.btn_playpause.setImageResource(R.drawable.pause);
						    Global.btn_currentSong = viewHolder.btn_playpause;
						    Global.currentSong = position;
						}
									
						/* Song is going to be paused */
						
						else  {
							mediaPlayer.pause();
							viewHolder.btn_playpause.setImageResource(R.drawable.play);
						}
						currentSong.clickCount++;
					}
		    	});
				
				convertView.setTag(viewHolder);
			}
			
			
			final ViewHolder holder = (ViewHolder) convertView.getTag();
			
			/* if the song name is too long, then crop it */
			String songName = currentSong.getName();
			if(songName.length() > 21)  {
				songName = songName.substring(0, 21) + "...";
			}
			holder.tv_name.setText(songName);
			
			holder.tv_artist.setText(currentSong.getArtist());
			
			String duration = currentSong.getDuration();
			duration = duration.substring(0, 3);		
			int minutes = Integer.valueOf(duration);
			int seconds = minutes % 60;
			minutes /= 60;
			if(String.valueOf(seconds).length()==1)  {
				duration = minutes + ":0" + seconds;
			}
			else  {
				duration = minutes + ":" + seconds;
			}
			holder.tv_duration.setText(duration);
			
			if(currentSong.getID()==position && mediaPlayer.isPlaying())  {
				holder.btn_playpause.setImageResource(R.drawable.pause);
			}
			else  {
				holder.btn_playpause.setImageResource(R.drawable.play);
			}		
			holder.btn_playpause.setTag(String.valueOf(position));
			
			return convertView;
		}
	}
	
	private void initSeekBar()  {
        try
        {
        	audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            
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
	
	private void playSongFromMediaStore(String path)  {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(path);
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
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
		    @Override
		    public void onPrepared(MediaPlayer mediaPlayer) {
		        mediaPlayer.start();
		    }
		});
	}
	
	private void playSongFromRaw(String file)  {

		String[] index = file.split(" ");
		AssetFileDescriptor afd = null;
		
		if(index[1].equals("1"))  {
			afd = this.getResources().openRawResourceFd(R.raw.powernapp1);
		}

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
	    try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    mediaPlayer.start();
	}
	
	static class ViewHolder  {
		TextView tv_name;
		TextView tv_artist;
		TextView tv_duration;
		ImageButton btn_playpause;
	}
}

class Song {
	
	private int ID;
	private String artist;
	private String name;
	private String duration;
	private String path;
	private boolean powernAppSong;
	public int clickCount = 0;
	
	public Song(int ID, String artist, String name, String duration, String path, boolean powernAppSong)  {
		super();
		this.artist = artist;
		this.name = name;
		this.duration = duration;
		this.path = path;
		this.powernAppSong = powernAppSong;
	}
	
	public int getID()  {
		return ID;
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
	
	public String getPath()  {
		return path;
	}
	
	public boolean isPowernAppSong()  {
		return powernAppSong;
	}
}
