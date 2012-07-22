package messages;

import java.util.ArrayList;
import java.util.Hashtable;

import client.Client;


public class ClientMessageManager {
	ArrayList<String> clients = new ArrayList<String>();
	Hashtable<String, Client> clientHash = new Hashtable<String, Client>();
	
	public ClientMessageManager()	{}
	
	public void sendMessage(final String message, final int clientId)	{
		final String clientIp = clients.get(new Integer(clientId));
		clientHash.get(clientIp).receiveMessage(message);
	}
	
	public void sendMessage(final String message, final String clientIp)	{
		clientHash.get(clientIp).receiveMessage(message);
	}
	
	public void sendMessageToNode(final String message, final String node)	{
		
	}
	
	public int addClient(final String clientIp)	{
		int returnInt = clients.size();
		clients.add(clientIp);
//		clientHash.put(clientIp, new Client(this));
		return returnInt;
	}
	
	public String getClientIp(final int clientId)	{
		return clients.get(new Integer(clientId));
	}
}
