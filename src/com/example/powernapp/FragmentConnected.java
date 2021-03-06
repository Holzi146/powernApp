package com.example.powernapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class FragmentConnected extends Fragment {
	/* Sockets */
	BluetoothSocket bt_socket;
	/* UI Stuff */
	Button btn_close;
	Button btn_start;
	CheckBox cb_time;
	TimePicker tp_time;
	
	InputStream mInputStream;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_connected, container, false);
		
		bt_socket = Global.bt_socket;
		
		if(!IspowernAppDevice(bt_socket))	{
			/* cancel connection and return to MainActivity */
			try {
				bt_socket.close();
			} catch (IOException e) {
				Toast.makeText(getActivity(), "Socket konnte nicht geschlossen werden!", Toast.LENGTH_SHORT).show();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle("Upps!");
    		builder.setMessage("Das von Ihnen gew�hlte Ger�t ist kein powernApp-Device");
    		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {     		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				Global.isConnected = false;
					getFragmentManager().beginTransaction().replace(R.id.container, new FragmentMain()).commit();
    			}
    		});
    		AlertDialog dialog = builder.create();  		
    		dialog.show();
		}
		/* Device is a powernApp-Device */
		else	{			
			btn_start = (Button) view.findViewById(R.id.btn_start);
			btn_close = (Button) view.findViewById(R.id.btn_close);
			cb_time = (CheckBox) view.findViewById(R.id.cb_time);
			tp_time = (TimePicker) view.findViewById(R.id.tp_time);
			tp_time.setIs24HourView(true);
			tp_time.setEnabled(false);
			
			/* Animations */			
			btn_start.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			btn_close.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			cb_time.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			tp_time.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			
			/* CheckBox Selection-Changer */
			cb_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		        @Override
		        public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
		        	if(cb_time.isChecked())
		        		tp_time.setEnabled(true);
		        	else
		        		tp_time.setEnabled(false);
		        }
		    });
			
			
			/*On-Click-Listener */
			btn_close.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					try {
						bt_socket.close();
					} catch (IOException e) {
						//Toast.makeText(getActivity(), "Socket konnte nicht geschlossen werden!", Toast.LENGTH_SHORT).show();
					}
					Global.isConnected = false;
    				/* back to main */
					getFragmentManager().beginTransaction().replace(R.id.container, new FragmentMain()).commit();
				}
			});
			
			btn_start.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					
					Toast.makeText(getActivity(), "powernApp gestartet", Toast.LENGTH_SHORT).show();
					
					btn_start.setEnabled(false);
					cb_time.setEnabled(false);
					tp_time.setEnabled(false);
					
					new Handler().postDelayed(new Runnable() {
		    	        @Override
		    	        public void run() {
		    	            WakeUp(bt_socket);                
		    	        }
		    	    }, 5000);
					
					/*
					OutputStream tmpOut = null;

					try {
			            tmpOut = bt_socket.getOutputStream();
			        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
					
					try {
						/* start powernApp *
						tmpOut.write(3);
			        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }*/
				}
			});
			
			//RecieveData(bt_socket);
		}
		
        return view;
    }
	
	private void RecieveData(BluetoothSocket bt_socket) {
		
		//WakeUp(bt_socket);
	}

	private void WakeUp(final BluetoothSocket bt_socket)  {
		/* AlertDialog with current ringtone */
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		final Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
		r.play();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Wecker");
		builder.setMessage("powernApp ist beendet!");
		builder.setCancelable(false);
		builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {     		
			@Override
			public void onClick(DialogInterface dialog, int which) {
    			/* ringtone is being stopped */
				r.stop();
				
				/* cancel socket connection */
				try {
					bt_socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* go back to main */
				Global.isConnected = false;
				getFragmentManager().beginTransaction().replace(R.id.container, new FragmentMain()).commit();
			}
		});
		AlertDialog dialog = builder.create();    		
		dialog.show();
	}
	
	private boolean IspowernAppDevice(BluetoothSocket bt_socket)	{
		/* send a one for the powernApp identification *
		OutputStream tmpOut = null;
		try {
            tmpOut = bt_socket.getOutputStream();
        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
		
		try {
			/* powernApp identification *
			tmpOut.write(3);
        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
		/* wait for the powernApp-package to arrive *
		
		try {
			mInputStream = bt_socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(new Runnable()
	    {
	        public void run()
	        {                
	           while(!Thread.currentThread().isInterrupted())
	           {
	                try 
	                {
	                    int bytesAvailable = mInputStream.available();                        
	                    if(bytesAvailable > 0)
	                    {
	                        byte[] packetBytes = new byte[bytesAvailable];
	                        mInputStream.read(packetBytes);

	                        final String data = new String(packetBytes, "US-ASCII");
	                            	
	                        Handler handler = null;
	                        handler.post(new Runnable()
	                        {
	                            public void run()
	                            {
	                                Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
	                            }
	                        });
	                    }
	                } 
	                catch (IOException ex) 
	                {
	                	Toast.makeText(getActivity(), "Error receiving data", Toast.LENGTH_SHORT).show();
	                }
	           }
	        }
	    }).start();*/

		return true;
	}
}
