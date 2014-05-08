package com.example.powernapp;

import java.util.ArrayList;
import java.util.List;
import com.example.powernapp.NavigationDrawerFragment.NavigationDrawerCallbacks;
import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {
	
	public static String PACKAGE_NAME;
	
	List<String> fragmentList = new ArrayList<String>();

    public static NavigationDrawerFragment mNavigationDrawerFragment;
    private String mTitle;
    
    boolean doubleBackToExitPressedOnce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          
        PACKAGE_NAME = getPackageName();
        
        /* ---------- Navigation Drawer actions --------------- */
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);                	        
        mDrawerLayout.setFocusableInTouchMode(false);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout); 
        
        mTitle = "powernApp";
    }

    @Override
    public void onBackPressed()  {    	
    	if (fragmentList.size() - 1 == 0)  {
    		if (doubleBackToExitPressedOnce)
    			android.os.Process.killProcess(android.os.Process.myPid());
    		
    	    doubleBackToExitPressedOnce = true;
    	    Toast.makeText(getApplicationContext(), "Press back again to leave", Toast.LENGTH_SHORT).show();

    	    new Handler().postDelayed(new Runnable() {
    	        @Override
    	        public void run() {
    	            doubleBackToExitPressedOnce = false;                       
    	        }
    	    }, 2000);
    	}
    	
    	else  {
    		String fragment = fragmentList.get(fragmentList.size() - 2);    				
        	fragmentList.remove(fragmentList.size() - 1);
        	
    		if (fragment == "main")  {
    			mNavigationDrawerFragment.selectItem(0);
    		}   		
    		else if (fragment == "musicplayer")  {
    			mNavigationDrawerFragment.selectItem(1);
    		}   		
    		else if (fragment == "tips")  {
    			mNavigationDrawerFragment.selectItem(2);
    		}   		
    		else if (fragment == "aboutus")  {
    			mNavigationDrawerFragment.selectItem(3);
    		}   		
        	fragmentList.remove(fragmentList.size() - 1);
    	}
    }
    
    /* ----------- CODE BELOW THIS LINE IS FOR THE NAVIGATION DRAWER -------------- */

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
    	switch (position) {
        	case 0:
        		if (!Global.isConnected)  {
        			getFragmentManager().beginTransaction().replace(R.id.container, new FragmentMain()).commit();      
        			fragmentList.add("main");
        		}
        		else
        			getFragmentManager().beginTransaction().replace(R.id.container, new FragmentConnected()).commit();
        		break;
        	case 1:
        		getFragmentManager().beginTransaction().replace(R.id.container, new FragmentMusicplayer()).commit();  
        		fragmentList.add("musicplayer");
        		break;
        	case 2:
        		getFragmentManager().beginTransaction().replace(R.id.container, new FragmentTips()).commit();
        		fragmentList.add("tips");
        		break;
        	case 3:
        		getFragmentManager().beginTransaction().replace(R.id.container, new FragmentAboutus()).commit();
        		fragmentList.add("aboutus");
        		break;
    	}
    	onSectionAttached(position);
    }

    /* change the title according to the selected fragment */
    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = "powernApp";
                break;
            case 1:
                mTitle = "Musikplayer";            
                break;
            case 2:
                mTitle = "Tipps und Tricks";
                break;
            case 3:
            	mTitle = "Über uns";
            	break;
        }
        restoreActionBar();
    }
    
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
