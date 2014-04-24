package com.example.powernapp;

import android.bluetooth.*;
import android.widget.ImageButton;

public class Global {
	public static boolean isConnected = false;
	public static BluetoothSocket bt_socket;
	public static int mainCount = 0;
	public static String macAddress;
	public static int currentSong = -1;
	public static ImageButton btn_currentSong;
}
