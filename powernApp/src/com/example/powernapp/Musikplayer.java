package com.example.powernapp;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class Musikplayer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Musikplayer");
		setContentView(R.layout.activity_musikplayer);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab tab1 = actionBar.newTab().setText("Eigene Musik");
		ActionBar.Tab tab2 = actionBar.newTab().setText("Appmusik");
		
		tab1.setTabListener(new TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();				
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub			
			}
		});
		
		tab2.setTabListener(new TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();			
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub			
			}
		});
		
		actionBar.addTab(tab1);
		actionBar.addTab(tab2);
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
