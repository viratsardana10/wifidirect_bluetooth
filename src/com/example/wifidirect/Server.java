package com.example.wifidirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.entity.InputStreamEntity;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.widget.Toast;

public class Server{
	ServerSocket serverSocket=null;
	Thread socketServerThread;
	private static final int server_port=8988;
	public Server(){
    Thread socketServerThread=new Thread(new SocketServerThread()); 
    socketServerThread.start();     
	}
 
	 class SocketServerThread implements Runnable
	{
		@Override
		public void run()
		{
			Socket socket=null;
			try {
				serverSocket=new ServerSocket(server_port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(!Thread.currentThread().isInterrupted())
			{
				try {
					socket=serverSocket.accept();
					CommunicationThread commThread=new CommunicationThread(socket);
					new Thread(commThread).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	 
	 class CommunicationThread implements Runnable
	 {
       private Socket clientSocket;
       private BufferedReader input;
		public CommunicationThread(Socket clientSocket) {
			this.clientSocket=clientSocket;
			try {
				this.input=new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// TODO Auto-generated constructor stub
	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!Thread.currentThread().isInterrupted())
			{
				try {
					String read=input.readLine();
					//int f=Integer.parseInt(read);
					
					
						BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
						mBluetoothAdapter.enable();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		 
	 }
	
}


