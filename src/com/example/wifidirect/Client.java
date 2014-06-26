package com.example.wifidirect;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
	private static final int server_port=8988;
	private String address;
	private int i;
	private Socket socket;
	public Client(String address,int i)
	{
		this.address=address;
	    this.i=i;
	    new Thread(new ClientThread()).start();
	}
	
	class ClientThread implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				InetAddress serverAddr=InetAddress.getByName(address);
				try {
					socket=new Socket(serverAddr,server_port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				out.println(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
}