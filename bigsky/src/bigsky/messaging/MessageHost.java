package bigsky.messaging;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

import bigsky.BlueTextRequest;
import bigsky.BlueTextResponse;
import bigsky.Contact;
import bigsky.TaskBar;
import bigsky.TextMessage;
import bigsky.gui.Conversation;
import bigsky.gui.LoadScreen;
import bigsky.Global;

class ClientConn implements Runnable {
	
	
	Thread t;
	Socket client = null;

	ClientConn(Socket client) {
		this.client = client;
		t = new Thread(this);
		t.start();
	}

	public void run() {
		Contact user = new Contact("Jonathan", "Mielke", "6185204620", "");
		ObjectInputStream br = null;
		try {
			br = new ObjectInputStream(client.getInputStream());
			while (br != null) {
				Object streamObject = br.readObject();
				if(streamObject instanceof Contact)
				{
					Contact ct = (Contact) streamObject;
					TaskBar.incomingContactQueue.add(ct);
					synchronized(TaskBar.textManager){
						TaskBar.textManager.notify();
					}
				}
				else if(streamObject instanceof TextMessage)
				{
					TextMessage txtMessage = (TextMessage) streamObject;
					txtMessage.setReceiver(user);
					System.out.println("Client: " + txtMessage.getContent());					
					TaskBar.myTextArray.add(txtMessage);					
					synchronized(TaskBar.textManager){
						TaskBar.textManager.notify();
					}
				}
				else if(streamObject instanceof BlueTextResponse)
				{
					BlueTextResponse response = (BlueTextResponse) streamObject;
					Global.phoneTextHistory = response.getChatHistory();
					Global.blueTextRqContact = response.getOriginalRequest().getContact();
					System.out.println("Got " + Global.phoneTextHistory.size() + " messages from " + response.getOriginalRequest().getContact().getPhoneNumber());
					synchronized(TaskBar.textManager){
						TaskBar.textManager.notify();
					}
				}
				else{
					System.out.println("ERROR: Unknown object sent through stream");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + " inside ClientConn.run()");
		}
	}
}

public class MessageHost extends Thread{
	
	public ClientConn conn = null;
	private ObjectOutputStream ps2 = null;
	private ServerSocket socket = null;

	
	public void run(){
		
		LoadScreen load = new LoadScreen();
		try{
			
			socket = new ServerSocket(1300);
			load.setVisible(true);
			Socket client = socket.accept();
			conn = new ClientConn(client);
			load.dispose();
			TaskBar.convo = new Conversation();
			TaskBar.convo.getFrmBluetext().setVisible(true);
			ps2 = new ObjectOutputStream(client.getOutputStream());
			
		} catch(Exception e){
			System.out.println("Caught exception while setting up MessageHost" + e.getMessage());
		}
		finally{
			if(socket != null)
			{
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public void closeHost(){
		
		if(ps2 != null){
			try {
				ps2.close();
			} catch (IOException e) {}
		}
		if(socket !=null){
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	public synchronized void sendObject(Object o)
	{
		try{
			ps2.writeObject(o);
			ps2.flush();
		} catch(Exception e){
			System.out.println("Got exception in MessageHost.sendObject(): " + e.getMessage());
		}
	}
}