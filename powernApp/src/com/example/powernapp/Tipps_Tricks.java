package com.example.powernapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Tipps_Tricks extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Tipps und Tricks");
		setContentView(R.layout.activity_tipps__tricks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tipps__tricks, menu);
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
	    	if(!ConnectedStatus.isConnected)	{
	    		Intent intent_home = new Intent(Tipps_Tricks.this,MainActivity.class);
	    		startActivity(intent_home);
	    	}
	    	else	{
	    		Intent intent_connected = new Intent(Tipps_Tricks.this,Connected.class);
	    		startActivity(intent_connected);
	    	}	
	        return true;
	    case R.id.hilfen:
	        Intent intent_help = new Intent(Tipps_Tricks.this,Hilfen.class);
	        startActivity(intent_help);	        
	        return true;	    
	    case R.id.about_us:
	    	Intent intent_about = new Intent(Tipps_Tricks.this,About_Us.class);
	        startActivity(intent_about);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
