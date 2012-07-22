package Master;

import java.util.ArrayList;
import java.util.Hashtable;

import client.Client;

import nodes.Node;

public class MessageManager {
	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<String> clients = new ArrayList<String>();
	Hashtable<String, Client> clientHash = new Hashtable<String, Client>();
	
	public static void main(String args[])	{
		new MessageManager();
	}
	
	public MessageManager()	{
		addNode();
		addClient(0);
		
		allClientsRequestLock("testLock");
		allClientsReleaseLock("testLock");
		
		addClient(0);
		
		allClientsRequestLock("testLock");
		allClientsReleaseLock("testLock");
		
		addClient(0);
		
		allClientsRequestLock("testLock");
		allClientsReleaseLock("testLock");
		
		addNode();
		
		allClientsRequestLock("testLock");
		allClientsReleaseLock("testLock");
		
		addNode();
		addNode();
		addNode();
		
		addClient(1);
		addClient(2);
		addClient(3);
		
		allClientsRequestLock("testLock");
		allClientsReleaseLock("testLock");
		
		allClientsRequestLock("testLock1");
		allClientsRequestLock("testLock2");
		allClientsRequestLock("testLock3");
		allClientsRequestLock("testLock4");
		allClientsRequestLock("testLock5");
		
		allClientsReleaseLock("testLock1");
		allClientsReleaseLock("testLock2");
		allClientsReleaseLock("testLock3");
		allClientsReleaseLock("testLock4");
		allClientsReleaseLock("testLock5");
		
		Client client1 = clientHash.get(clients.get(0));
		Client client2 = clientHash.get(clients.get(1));
		Client client3 = clientHash.get(clients.get(2));
		Client client4 = clientHash.get(clients.get(2));
		Client client5 = clientHash.get(clients.get(2));
		Client client6 = clientHash.get(clients.get(2));
		
		client1.requestLock("testLock1");
		client2.requestLock("testLock2");
		client3.requestLock("testLock1");
		client3.requestLock("testLock2");
		client3.requestLock("testLock3");
		client4.requestLock("testLock1");
		client4.requestLock("testLock4");
		client5.requestLock("testLock5");
		client6.requestLock("testLock2");
		client6.requestLock("testLock3");
		client6.requestLock("testLock6");
	}
	
	public void sendMessage(final String message, final int nodeId)	{
		nodes.get(new Integer(nodeId)).receiveMessage(message);
	}
	
	public void sendMessage(final String message, final String clientIp)	{
		clientHash.get(clientIp).receiveMessage(message);
	}
	
	private void allClientsRequestLock(final String lock)	{
		for (String ip: clients)	{
			Client client = clientHash.get(ip);
			client.requestLock(lock);
		}
	}
	
	private void allClientsReleaseLock(final String lock)	{
		for (String ip: clients)	{
			Client client = clientHash.get(ip);
			if (client.hasLock(lock))
				client.releaseLock(lock);
		}
	}
	
	private void addClient(final int nodeId)	{
		final String ip = new Integer(clients.size()).toString();
		Client client = new Client(ip, nodeId, this);
		clients.add(ip);
		clientHash.put(ip, client);
	}
	
	private void addNode()	{
		for (Node node: nodes)	{
			node.addNode(nodes.size());
		}
		
		nodes.add(new Node(nodes.size(), this));
	}
}
