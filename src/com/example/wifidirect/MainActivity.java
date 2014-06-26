package com.example.wifidirect;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

import android.R.integer;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;
import android.net.wifi.WifiManager;
import android.app.ListActivity;
import android.os.AsyncTask;

public class MainActivity extends Activity implements WifiP2pManager.ConnectionInfoListener{

	protected static final String TAG = null;
	private final Context context=this;
	private final IntentFilter mIntentfilter=new IntentFilter();
	
	private Channel mChannel;
	private WifiP2pManager mManager;
	private BroadcastReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//indicate change in wifi state
		mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentfilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this,getMainLooper(),null);
		mReceiver=new MyReceiver(mManager,mChannel,this);
		registerReceiver(mReceiver, mIntentfilter);
		
		Button btn=(Button) findViewById(R.id.button);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				show();
			}
		});
		
		Button btn1=(Button) findViewById(R.id.button1);
		Button btn2=(Button) findViewById(R.id.button2);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Client client=new Client(address,1);
			}
		});
		
		
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Client client2=new Client(address,1);
			}
		});
		
	}
	
	
	private List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
	
	private PeerListListener peerListListener=new PeerListListener() {
		
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peerList) {
			// TODO Auto-generated method stub
			peers.clear();
			peers.addAll(peerList.getDeviceList());
			((ArrayAdapterItem) listViewItems.getAdapter()).notifyDataSetChanged();
            
			//add list view			
			if (peers.size() == 0) {
              Toast.makeText(getApplicationContext(), "no peers available",Toast.LENGTH_SHORT).show();
            }
		}
	};
	int final_pos;

	
	public void conn(int pos)
	{
		final_pos=pos;
		connect();
	}
	
	AlertDialog alertDialogDevices;
	ArrayAdapterItem adapter;
	ListView listViewItems;
	public void show()
	{
		//AlertDialog alertDialogDevices;
		adapter=new ArrayAdapterItem(this,R.layout.listv,peers);
		listViewItems=new ListView(this);
		listViewItems.setAdapter(adapter);
		listViewItems.setOnItemClickListener(new ListOnClick(MainActivity.this));
		alertDialogDevices=new AlertDialog.Builder(MainActivity.this).setView(listViewItems).setTitle("Devices").show(); 
	}
	
	 
	public class MyReceiver extends BroadcastReceiver{
		
		private WifiP2pManager mManager;
		private Channel mChannel;
		private MainActivity activity;
		
		
		
		public MyReceiver(WifiP2pManager manager,Channel channel,MainActivity activity)
		{
			super();
			this.mManager=manager;
			this.mChannel=channel;
			this.activity=activity;
			
		}
				
		
	@Override
	public void onReceive(Context context,Intent intent){
		WifiManager wifi;
		wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		String action=intent.getAction();
		if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
			
			
			int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
			if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				//wifi is enabled
				wifi.setWifiEnabled(true);
				
				discoverPeers();
			}
			else
			{
				//wifi is disabled
				wifi.setWifiEnabled(false);
			}
			return;
						
		}
		else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
		{
			if(mManager!=null){
			mManager.requestPeers(mChannel, peerListListener);
			}
		}
		else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
		{
			//connection state changed
			
			if(mManager==null)
			   return;
			
			NetworkInfo networkInfo=(NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected()){
			mManager.requestConnectionInfo(mChannel,(ConnectionInfoListener) activity);
			}
			}
	}
		
	}
	
	public void discoverPeers()
	{
		
		mManager.discoverPeers(mChannel,new WifiP2pManager.ActionListener()
		{
			@Override
			public void onSuccess()
			{
				//code remaining
				
			}
			
			@Override
			public void onFailure(int reasonCode)
			{
				//code remaining
				
			}
			
		});
		
	}
	/**/
	
	WifiP2pDevice device;
	
	
	
	public void connect()
	{
	
	   device=(WifiP2pDevice) peers.get(final_pos);
	   WifiP2pConfig config=new WifiP2pConfig();
	   config.deviceAddress=device.deviceAddress;
	   config.groupOwnerIntent=0;
	   config.wps.setup=WpsInfo.PBC;
	   
	   mManager.connect(mChannel, config, new ActionListener()
	   {
		   @Override
		   public void onSuccess()
		   {
			// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			   Toast.makeText(MainActivity.this,"Connection Successful with"+ "  "+device.deviceName+"   "+device.deviceAddress,Toast.LENGTH_SHORT).show();   
		   }
		   
		   @Override
		   public void onFailure(int reason)
		   {
			   Toast.makeText(MainActivity.this, "Connect failed. Retry.",Toast.LENGTH_SHORT).show(); 
		   }		   
	   	
	   });
	   
	}
	
	private WifiP2pInfo info;
	String address;
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info)
	{
		Toast.makeText(MainActivity.this,"connection info available",Toast.LENGTH_SHORT).show();
		this.info=info;
		this.address=info.groupOwnerAddress.getHostAddress();
		if(this.info.groupFormed && this.info.isGroupOwner)
		{
			/*this is server side..send the file
			new FileServerAsyncTask(MainActivity.this);
			server side listen to the client*/
			Toast.makeText(MainActivity.this,address,Toast.LENGTH_SHORT).show();
			
			Server server=new Server();
		}
		else if(info.groupFormed)
		{
			Toast.makeText(MainActivity.this,"This is client side",Toast.LENGTH_SHORT).show();
			
			Button btn1=(Button) findViewById(R.id.button1);
			Button btn2=(Button) findViewById(R.id.button2);
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
		}
	}
	
	 
	@Override
	public void onResume()
	{
		
		super.onResume();
		registerReceiver(mReceiver, mIntentfilter);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
