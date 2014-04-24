package com.example.powernapp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class FragmentMain extends Fragment implements RecognitionListener {
	
	/* UI Stuff*/
	VideoView videoView;
	ImageButton btn_search;
	ImageView iv_arrow;
	TextView tv_klickmich;
	/* NavigationDrawer */	 
	NavigationDrawerFragment drawer;
	/* Intents und Adapter */
	Intent speechIntent;
	BluetoothAdapter bt_adapter;
	ArrayAdapter<String> arr_name;
	ArrayAdapter<String> arr_mac;
	/* Variablen und Konstanten */
	boolean buttonImage = true;
	boolean searching = false;
	/* Sockets und Handler */
	BluetoothSocket bt_socket;
	Handler mHandler;
	
	/* SpeechRecognizer */
	SpeechRecognizer speechRecognizer;
	/* speech commands */
	static final String[] searchButtonCommand = { "search", "go", "start", "searching", "button" };
	static final String[] switchToMusicCommand = { "switch to music player", "music", "switch to music" };
	static final String[] switchToTipsCommand = { "switch to tips", "switch to tricks", "switch to tips and tricks", "tips", "tricks" };
	static final String[] switchToAboutUsCommand = { "switch to about us", "about us", "about", "switch to about" };
	
	static final int BLUETOOTH_ACTIVATE_CODE = 0;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		setHasOptionsMenu(true);
        
		bt_adapter = BluetoothAdapter.getDefaultAdapter();	
		btn_search = (ImageButton) view.findViewById(R.id.btn_search);
		tv_klickmich = (TextView) view.findViewById(R.id.tv_klickmich);
		iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
		videoView = (VideoView) view.findViewById(R.id.videoView);
		
		drawer = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		
		if (bt_adapter == null) {
    		/* Gerät hat KEIN Bluetooth, Alert wird angezeigt */
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle("Tut uns leid");
    		builder.setMessage("Ihr Gerät unterstützt kein Bluetooth!");
    		builder.setCancelable(false);
    		builder.setPositiveButton("Schade",new DialogInterface.OnClickListener() {     		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
        			/* Applikation wird geschlossen */
    				android.os.Process.killProcess(android.os.Process.myPid());    				
    			}
    		});
    		AlertDialog dialog = builder.create();    		
    		dialog.show();
    	}
		
		btn_search.setOnClickListener(new View.OnClickListener()  {			
			public void onClick(View v)  {
				BluetoothStart();
			}			
    	});

		StartAnimations();
		
		// Inflate the layout for this fragment
        return view;
    }
	
	private void BluetoothStart() {
		/* Bluetooth wird, wenn ausgeschalten, durch den User eingeschalten */
		if (!bt_adapter.isEnabled())  {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);				    
		    startActivityForResult(enableBtIntent, BLUETOOTH_ACTIVATE_CODE);				    
		}
		else  {
			BTSearchForDevices();
		}
	}
	
	private void StartAnimations()  {
		/* nur wenn das Activity zum ersten Mal aufgerufen wird, soll das Video gestartet werden */
		if(Global.mainCount == 0)	{
			new Thread(new Runnable()  {  
		        @Override
		        public void run()  {
		        	Uri uri = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME +"/" + R.raw.makeseverynap);
					videoView.setVideoURI(uri);
					videoView.setZOrderOnTop(true);					
					videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()  {
						@Override
						public void onCompletion(MediaPlayer arg0)  {
							/* Animations */
							videoView.setVisibility(View.INVISIBLE);					
							tv_klickmich.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
							iv_arrow.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
							tv_klickmich.setVisibility(View.VISIBLE);
							iv_arrow.setVisibility(View.VISIBLE);
						}});			
					videoView.start();
					Global.mainCount++;
		        }
		    }).start();
		}
		
		else  {
			/* Animations */
			videoView.setVisibility(View.INVISIBLE);
			btn_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			tv_klickmich.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			iv_arrow.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			tv_klickmich.setVisibility(View.VISIBLE);
			iv_arrow.setVisibility(View.VISIBLE);
		}
	}
	
	private void BTSearchForDevices()  {
		arr_name = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, 0);
		arr_mac = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, 0);

		if (bt_adapter.isDiscovering())	{ bt_adapter.cancelDiscovery(); }
		searching = true;
		bt_adapter.startDiscovery();
			
		new Thread(new Runnable() { 
	        @Override
	        public void run() {
	        	while (searching) {
	        		try {
	        			/* Buttonhintergrund soll alle 1,3s geändert werden */
	        			Thread.sleep(1300);
	        			if(buttonImage)	{	     
	        				getActivity().runOnUiThread(new Runnable() {							
								@Override
								public void run() {
									btn_search.setImageResource(R.drawable.logo2);								
								}
							});	        			
	        				buttonImage = false;
	        			}
	        			else  {
	        				getActivity().runOnUiThread(new Runnable() {							
								@Override
								public void run() {
									btn_search.setImageResource(R.drawable.logo);									
								}
							});
	        				buttonImage = true;
	        			}
	        		} catch (final Exception e) {
	        			getActivity().runOnUiThread(new Runnable() {							
							@Override
							public void run() {
								Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();									
							}
						});
	        			return;
	        		}
	        	}
	        }
	    }).start();

		btn_search.setEnabled(false);

		/* IntentFilter erzeugen und den BroadcastReceiver im System registrieren */
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	private final void BTConnect(String mac_address)  {
		if (bt_adapter.isDiscovering())	{ bt_adapter.cancelDiscovery(); }
		
		BluetoothDevice remote_device = bt_adapter.getRemoteDevice(mac_address);
		bt_socket = createBluetoothSocket(remote_device);
		try {
            /* Connect the device through the socket */
        	bt_socket.connect();
        } catch (IOException e) {
            /* Unable to connect; close the socket and get out */
            Toast.makeText(getActivity(), "Verbindung fehlgeschlagen oder kein powernApp-Device", Toast.LENGTH_LONG).show();
            try {
				bt_socket.close();
			} catch (IOException e1) { }
            return;
        }
		Global.isConnected = true;
		Global.bt_socket = bt_socket;
		Global.macAddress = mac_address;
		
		getFragmentManager().beginTransaction().replace(R.id.container, new FragmentConnected()).commit();
	}

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) {	
		try {
	    	BluetoothDevice tmp_device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());
	        final Method m = tmp_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
	        return (BluetoothSocket)m.invoke(tmp_device, Integer.valueOf(1));
	    } 
	    catch (Exception e) {
		    Toast t =Toast.makeText(getActivity(), "Socket konnte nicht erstellt werden", Toast.LENGTH_LONG);
		    t.setGravity(Gravity.CENTER, 0, 0);
		    t.show();
		    return bt_socket = null;
	    }	    
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();
	        /* wenn durch die Suche ein Gerät gefunden wurde */
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            /* das Bluetooth-Gerät aus dem Intent holen */
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if (device != null) {
	                /* Check if the device has name */
	                if ((device.getName() != null) && (device.getName().length() > 0)) {
	                	/* Check if device is already in list */
	                	if (arr_mac.getPosition(device.getAddress()) == -1)  {
	                		arr_name.add(device.getName());
	                		arr_mac.add(device.getAddress());
	                	}
	                }
	            }
	        }	
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))	{
	        	btn_search.setEnabled(true);
	        	searching = false;
	        	((ImageButton) btn_search).setImageResource(R.drawable.logo);	        	

	        	if(arr_name.getCount() > 0)	{

	        		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        		builder.setTitle("Wähle ein Gerät aus");
	        		builder.setAdapter(arr_name, new DialogInterface.OnClickListener() {     		
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				if(bt_adapter.isDiscovering())
	                    		bt_adapter.cancelDiscovery();
	                    	btn_search.setEnabled(true);
	                        String device_data = arr_mac.getItem(which);
	                        
	                        BTConnect(device_data);
	        			}
	        		});
	        		AlertDialog dialog = builder.create();
	        		dialog.show();
	        	}
	        	else	{
	        		Toast t = Toast.makeText(getActivity(), "Keine Geräte gefunden", Toast.LENGTH_SHORT);
	        		t.setGravity(Gravity.CENTER, 0, 0);
	        		t.show();
	        	}
	        }
	    }
	};
	
	private void VoiceRecognition()  {
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
		speechRecognizer.setRecognitionListener(this);
	    
	    speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    /* even though the app is mostly German, the SpeechRecognizer gives more results when set the English */
	    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
	    speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.PACKAGE_NAME);
	    
	    speechRecognizer.startListening(speechIntent);
	}
	
	private void CompareResults(ArrayList<String> matches)  {
		/* check if searchButtonCommand */
		for (int u = 0; u < matches.size(); u++)  {
			for (int i = 0; i < searchButtonCommand.length; i++)  {		
				if (searchButtonCommand[i].equals(matches.get(u)))  {
					BluetoothStart();
					return;
				}
			}
		}
		
		/* SWITCH commands */
		
		/* switch to musicplayer */
		for (int i = 0; i < matches.size(); i++)  {
			for(int u = 0; u < switchToMusicCommand.length; u++)  {
				if (switchToMusicCommand[u].equals(matches.get(i)))  {					
					drawer.selectItem(1);
					return;
				}
			}
		}	
		
		/* switch to tips and tricks */
		for (int i = 0; i < matches.size(); i++)  {
			for(int u = 0; u < switchToTipsCommand.length; u++)  {
				if (switchToTipsCommand[u].equals(matches.get(i)))  {
					drawer.selectItem(2);
					return;
				}
			}
		}	
		
		/* switch to about us */
		for (int i = 0; i < matches.size(); i++)  {
			for(int u = 0; u < switchToAboutUsCommand.length; u++)  {
				if (switchToAboutUsCommand[u].equals(matches.get(i)))  {
					drawer.selectItem(3);
					return;
				}
			}
		}	
		
		Toast.makeText(getActivity(), "Befehl wurde nicht erkannt", Toast.LENGTH_SHORT).show();
		
		/* Alert Dialog to show the failed output
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Gefundene Wörter");
		builder.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, matches), null);
		AlertDialog dialog = builder.create();
		dialog.show(); */
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.action_voice);
		menu.removeItem(R.id.action_settings);
		
		inflater.inflate(R.menu.voice, menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {     
        if (item.getItemId() == R.id.action_voice)  {
        	VoiceRecognition();
            return true;        
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onDestroy()  {
		if(speechRecognizer != null)
			speechRecognizer.destroy();
		super.onDestroy();
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    /* Check which request we're responding to */
		switch(requestCode)
		{
			case BLUETOOTH_ACTIVATE_CODE:
			{
				if (resultCode == Activity.RESULT_OK)
		            BTSearchForDevices();
				break;
			}        
		}		
	}

	@Override
	public void onReadyForSpeech(Bundle params) { }

	@Override
	public void onBeginningOfSpeech() { }

	@Override
	public void onRmsChanged(float rmsdB) { }

	@Override
	public void onBufferReceived(byte[] buffer) { }

	@Override
	public void onEndOfSpeech() { }

	@Override
	public void onError(int error) {
		Toast.makeText(getActivity(), "Befehl wurde nicht erkannt", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResults(Bundle data) {
		ArrayList<String> matches = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		CompareResults(matches);
	}

	@Override
	public void onPartialResults(Bundle partialResults) { }

	@Override
	public void onEvent(int eventType, Bundle params) { }
}
