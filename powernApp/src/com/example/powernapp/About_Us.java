package com.example.powernapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class About_Us extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Über uns");   
		setContentView(R.layout.activity_about__us);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about__us, menu);
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
	    	MainActivity obj = (MainActivity)getApplicationContext();
	    	if(obj.getIsConnected())	{
	    		Intent intent_connected = new Intent(About_Us.this,Connected.class);
		        startActivity(intent_connected);
	    	}
	    	else	{
	    		Intent intent_home = new Intent(About_Us.this,MainActivity.class);
	    		startActivity(intent_home);
	    	}
	        return true;
	    case R.id.hilfen:
	        Intent intent_help = new Intent(About_Us.this,Hilfen.class);
	        startActivity(intent_help);	        
	        return true;
	    case R.id.tipps:
	    	Intent intent_tipps = new Intent(About_Us.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);	
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
