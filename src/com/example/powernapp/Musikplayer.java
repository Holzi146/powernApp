package com.example.powernapp;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Musikplayer extends Activity {
	
	ActionBar actionBar;
	Tab tab1, tab2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setTitle("Musikplayer");
		setContentView(R.layout.activity_musikplayer);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
