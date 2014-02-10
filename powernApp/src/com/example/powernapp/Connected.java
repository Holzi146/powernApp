package com.example.powernapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Connected extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("powernApp");
		setContentView(R.layout.activity_connected);
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
