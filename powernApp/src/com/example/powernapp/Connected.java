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
import android.widget.Toast;

public class Connected extends Activity {
	
	/* Sockets */
	BluetoothSocket bt_socket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("powernApp");
		setContentView(R.layout.activity_connected);
		
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
		else
			Toast.makeText(getApplicationContext(), "powernApp-Device", Toast.LENGTH_SHORT).show();
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
