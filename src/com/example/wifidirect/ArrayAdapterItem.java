package com.example.wifidirect;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

public class ArrayAdapterItem extends ArrayAdapter<WifiP2pDevice>{
	
	Context mContext;
	int layoutResourceId;
	private List<WifiP2pDevice> peers;
	
	public ArrayAdapterItem(Context mContext,int layoutResourceId,List<WifiP2pDevice> peers)
	{
		 super(mContext, layoutResourceId,peers);
		 
		 this.layoutResourceId=layoutResourceId;
		 this.mContext=mContext;
		 this.peers=peers;
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		if(convertView==null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		//String peerf;
		//peerf=peers.get(position).toString();
		WifiP2pDevice device=peers.get(position);
		
		TextView textViewItem=(TextView) convertView.findViewById(R.id.textViewItem);
		textViewItem.setText(device.deviceName);
		textViewItem.setTag(position);
		return convertView;				
	
	

	}
	}
