package com.example.powernapp;

import java.io.IOException;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
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
    		builder.setMessage("Das von Ihnen gewählte Gerät ist kein powernApp-Device");
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
					OutputStream tmpOut = null;

					try {
			            tmpOut = bt_socket.getOutputStream();
			        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
					
					try {
						tmpOut.write(1);
			        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
				}
			});
			
			//RecieveData(bt_socket);
		}
		
        return view;
    }
	
	private boolean IspowernAppDevice(BluetoothSocket bt_socket)	{
		/* wait for the powernApp-package to arrive */
		/* set a timeout (2s) */
		return true;
	}
}
