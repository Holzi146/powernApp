package com.example.powernapp;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class Connected extends Activity {
	
	/* Sockets */
	BluetoothSocket bt_socket;
	/* UI Stuff */
	Button btn_close;
	Button btn_start;
	CheckBox cb_time;
	TimePicker tp_time;
	/* this variable is used to find out from which this activity has been called */ 
	String caller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("powernApp");
		setContentView(R.layout.activity_connected);
		caller = getIntent().getStringExtra("caller");
		
		bt_socket = Global.bt_socket;
		
		if(!IsBluetoothDevice(bt_socket))	{
			/* cancel connection and return to MainActivity */
			try {
				bt_socket.close();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Socket konnte nicht geschlossen werden!", Toast.LENGTH_SHORT).show();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(Connected.this);
    		builder.setTitle("Upps!");
    		builder.setMessage("Das von Ihnen gewählte Gerät ist kein powernApp-Device");
    		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {     		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				Global.isConnected = false;
    				Intent intent_home = new Intent(Connected.this,MainActivity.class);
    	    		startActivity(intent_home);
    			}
    		});
    		AlertDialog dialog = builder.create();  		
    		dialog.show();
		}
		/* Device is a powernApp-Device */
		else	{			
			btn_start = (Button) findViewById(R.id.btn_start);
			btn_close = (Button) findViewById(R.id.btn_close);
			cb_time = (CheckBox) findViewById(R.id.cb_time);
			tp_time = (TimePicker) findViewById(R.id.tp_time);
			tp_time.setIs24HourView(true);
			tp_time.setEnabled(false);
			
			/* Animations */			
			btn_start.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			btn_close.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			cb_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			tp_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			
			/* CheckBox Selection-Changer */
			cb_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		        @Override
		        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
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
						Toast.makeText(getApplicationContext(), "Socket konnte nicht geschlossen werden!", Toast.LENGTH_SHORT).show();
					}
					Global.isConnected = false;
    				Intent intent_home = new Intent(Connected.this,MainActivity.class);
    	    		startActivity(intent_home);
				}
			});
			
			btn_start.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					if(cb_time.isChecked())	{
						Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
					}
					else	{
						Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	@Override
	protected void onResume()	{
		overridePendingTransition(0,0);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connected, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
	    if(caller.equals("main"))	{
	    	Intent intent_help = new Intent(Connected.this,Hilfen.class);
	        startActivity(intent_help);
	    }
	    if(caller.equals("about"))	{
	    	Intent intent_about = new Intent(Connected.this,About_Us.class);
	        startActivity(intent_about);
	    }
	    if(caller.equals("help"))	{
	    	Intent intent_help = new Intent(Connected.this,Hilfen.class);
	        startActivity(intent_help);
	    }
	    if(caller.equals("tipps"))	{
	    	Intent intent_tipps = new Intent(Connected.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);
	    }
	}
	
	private boolean IsBluetoothDevice(BluetoothSocket bt_socket)	{
		/* wait for the powernApp-package to arrive */
		/* set a timeout (2s) */
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /* Handle item selection */
	    switch (item.getItemId()) {
	    case R.id.hilfen:
	        Intent intent_help = new Intent(Connected.this,Hilfen.class);
	        startActivity(intent_help);	        
	        return true;
	    case R.id.tipps:
	    	Intent intent_tipps = new Intent(Connected.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);	
	        return true;
	    case R.id.about_us:
	    	Intent intent_about = new Intent(Connected.this,About_Us.class);
	        startActivity(intent_about);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
