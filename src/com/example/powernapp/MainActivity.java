package com.example.powernapp;

import java.io.IOException;
import java.lang.reflect.Method;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {

	/* UI Stuff*/
	VideoView videoView;
	ImageButton btn_search;
	ImageView iv_arrow;
	TextView tv_klickmich;
	/* Intents und Adapter */
	Intent enableBtIntent;
	BluetoothAdapter bt_adapter;
	ArrayAdapter<String> arr_name;
	ArrayAdapter<String> arr_mac;
	/* Variablen und Konstanten */
	final int btActivate_result = 0;
	boolean buttonImage = true;
	boolean searching = false;
	/* Sockets und Handler */
	BluetoothSocket bt_socket;
	Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);				
		mHandler = new Handler();
		
		bt_adapter = BluetoothAdapter.getDefaultAdapter();
		btn_search = (ImageButton) findViewById(R.id.btn_search);
		tv_klickmich = (TextView) findViewById(R.id.tv_klickmich);
		iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
		videoView = (VideoView) findViewById(R.id.videoView);
		
		/* nur wenn das Activity zum ersten Mal aufgerufen wird, soll das Video gestartet werden */
		if(Global.main_count==0)	{
			Uri uri = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.makeseverynap3);
			videoView.setVideoURI(uri);
			videoView.setZOrderOnTop(true);
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()  {

				@Override
				public void onCompletion(MediaPlayer arg0)  {
					/* Animations */
					videoView.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.INVISIBLE);					
					videoView.setVisibility(View.INVISIBLE);					
					tv_klickmich.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
					iv_arrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
					tv_klickmich.setVisibility(View.VISIBLE);
					iv_arrow.setVisibility(View.VISIBLE);
				}});
			
			videoView.start();
			Global.main_count++;
		}
		
		else	{
			/* Animations */
			videoView.setVisibility(View.INVISIBLE);
			btn_search.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			tv_klickmich.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			iv_arrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
			tv_klickmich.setVisibility(View.VISIBLE);
			iv_arrow.setVisibility(View.VISIBLE);
		}
		BTActivate();
	}
	
	@Override
	protected void onResume()	{
		overridePendingTransition(0,0);
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    /* Handle item selection */
	    switch (item.getItemId()) {
	    case R.id.musikplayer:
	        Intent intent_help = new Intent(MainActivity.this,Musikplayer.class);
	        startActivity(intent_help);     
	        return true;
	    case R.id.tipps:
	    	Intent intent_tipps = new Intent(MainActivity.this,Tipps_Tricks.class);
	        startActivity(intent_tipps);	
	        return true;
	    case R.id.about_us:
	    	Intent intent_about = new Intent(MainActivity.this,About_Us.class);
	        startActivity(intent_about);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private final void BTActivate()
    {		
    	if (bt_adapter == null) {
    		/* Gerät hat KEIN Bluetooth, Alert wird angezeigt */
    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    		builder.setTitle("Tut uns leid");
    		builder.setMessage("Ihr Gerät unterstützt leider kein Bluetooth!");
    		builder.setPositiveButton("Schade",new DialogInterface.OnClickListener() {     		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
        			/* Applikation wird geschlossen */
    	            finish();
    	            System.exit(0);
    			}
    		});
    		AlertDialog dialog = builder.create();    		
    		dialog.show();
    	}
        
    	btn_search.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				/* Bluetooth wird, wenn ausgeschalten, durch den User eingeschalten */
				if (!bt_adapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);				    
				    startActivityForResult(enableBtIntent, btActivate_result);				    
				}
				else	{			
					BTSearchForDevices();
				}
			}
    	});
    }
	
	private final void BTSearchForDevices()
	{
		arr_name = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
		arr_mac = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
		
		if (bt_adapter.isDiscovering())	{ bt_adapter.cancelDiscovery(); }
		searching = true;
		bt_adapter.startDiscovery();
		new Thread(new Runnable() { 
	        @Override
	        public void run() {
	        	while (searching) {
	        		try {
	        			/* Buttonhintergrund soll alle 1,3s geändert werden */
	        			Thread.sleep(1300);
	        			mHandler.post(new Runnable() {
	        				@Override
	        				public void run() {
	                        	
	        					if(buttonImage)	{
	        							((ImageButton) btn_search).setImageResource(R.drawable.logo2);
	        							buttonImage = false;
	        					}
	        					else	{
	        						((ImageButton) btn_search).setImageResource(R.drawable.logo);
	        						buttonImage = true;
	        					}
	        				}
	        			});
	        		} catch (Exception e) {
	        			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	        		}
	        	}
	        }
	    }).start();
		btn_search.setEnabled(false);
		
		/* IntentFilter erzeugen und den BroadcastReceiver im System registrieren */
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}
	
	private final void BTConnect(String mac_address)	{
		if (bt_adapter.isDiscovering())	{ bt_adapter.cancelDiscovery(); }
		BluetoothDevice remote_device = bt_adapter.getRemoteDevice(mac_address);
		try {
			bt_socket = createBluetoothSocket(remote_device);
	    } 
		catch (IOException e) {
    		return;
	    }
		try {
            /* Connect the device through the socket */
        	bt_socket.connect();
        } catch (IOException e) {
            /* Unable to connect; close the socket and get out */
            Toast.makeText(getApplicationContext(), "Verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            try {
				bt_socket.close();
			} catch (IOException e1) { }
            return;
        }
		Global.isConnected = true;
		Global.bt_socket = bt_socket;
		Intent intent_connected = new Intent(MainActivity.this, Connected.class);
		intent_connected.putExtra("caller", "main");
		startActivity(intent_connected); 
	}
	
	/* Send and reveiving methode for bluetooth-connections *
	private void SendData(BluetoothSocket s)	{
		
		OutputStream tmpOut = null;

		try {
            tmpOut = s.getOutputStream();
        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
		
		try {
			tmpOut.write(1);
        } catch (IOException e) { Log.e("",e.getLocalizedMessage()); }
	}
	
	private void RecieveData(BluetoothSocket s)	{
		InputStream mmInStream = null;
		byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                String str = new String(buffer,"UTF-8");
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                break;
            }
        }
	}
	*/
	
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {	
		try {
	    	BluetoothDevice tmp_device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());
	        final Method m = tmp_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
	        return (BluetoothSocket)m.invoke(tmp_device, Integer.valueOf(1));
	    } 
	    catch (Exception e) {
		    Toast t =Toast.makeText(getApplicationContext(), "Socket konnte nicht erstellt werden", Toast.LENGTH_LONG);
		    t.setGravity(Gravity.CENTER, 0, 0);
		    t.show();
		    return bt_socket = null;
	    }	    
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();
	        /* wenn durch die Suche ein Gerät gefunden wurde */
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            /* das Bluetooth-Gerät aus dem Intent holen */
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            /* Hinzufügen des Namens und der Adresse in ein Array */
	            arr_name.add(device.getName());
	            arr_mac.add(device.getAddress());
	        }	
	        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))	{
	        	btn_search.setEnabled(true);
	        	searching = false;
	        	((ImageButton) btn_search).setImageResource(R.drawable.logo);	        	
	        	
	        	if(arr_name.getCount()>0)	{
	        		
	        		AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
	        		builder.setTitle("Wähle ein Gerät aus");
	        		builder.setAdapter(arr_name, new DialogInterface.OnClickListener() {     		
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				if(bt_adapter.isDiscovering())
	                    		bt_adapter.cancelDiscovery();
	                    	btn_search.setEnabled(true);
	                        String device_data = arr_mac.getItem(which);               
	                        BTConnect(device_data);
	        			}
	        		});
	        		AlertDialog dialog = builder.create();
	        		dialog.show();
	        	}
	        	else	{
	        		Toast t = Toast.makeText(getApplicationContext(), "Keine Geräte gefunden", Toast.LENGTH_SHORT);
	        		t.setGravity(Gravity.CENTER, 0, 0);
	        		t.show();
	        	}
	        }
	    }
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    /* Check which request we're responding to */
		switch(requestCode)
		{
			case btActivate_result:
			{
				if (resultCode == RESULT_OK) {
		            BTSearchForDevices();
		        }
				break;
			}
		}		
	}
}