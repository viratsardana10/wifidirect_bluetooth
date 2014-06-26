package com.example.wifidirect;

import java.net.ConnectException;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ListOnClick implements OnItemClickListener {
	
	MainActivity mainActivity;
	
	public ListOnClick(MainActivity mainActivity)
	{
		this.mainActivity=mainActivity;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Context context=view.getContext();
		TextView textViewItem = ((TextView) view.findViewById(R.id.textViewItem));
		
		mainActivity.conn((Integer) textViewItem.getTag());
		
		((MainActivity) context).alertDialogDevices.cancel();

	

	}

}
