package client;

import java.util.Hashtable;

import nodes.MessageParser;

import constants.Messages;
import Master.MessageManager;
import messages.MessageBuilder;

public class Client {
	private MessageBuilder messageBuilder = new MessageBuilder();
	private MessageParser messageParser = new MessageParser();
	private MessageManager messageManager;
	private Hashtable<String, Boolean> obtainedLocks = new Hashtable<String, Boolean>();
	private String ip;
	private int nodeId;
	
	public Client(final String ip, final int nodeId, MessageManager messageManager)	{
		this.ip = ip;
		this.nodeId = nodeId;
		this.messageManager = messageManager;
		
		final String message = messageBuilder.constructMessage(ip, Messages.clientConnectionRequest, "");
		messageManager.sendMessage(message, nodeId);
	}
	
	public void receiveMessage(final String message)	{
		final String messageType = messageParser.getMessageType(message);
		if (messageType.equals(Messages.clientGrantLock))	{
			takeLock(messageParser.getMessageContent(message));
		}
		else if (messageType.equals(Messages.clientLockReleased))	{
			System.out.println("Client with IP " + ip + " succesfully released the lock named \""
					+ messageParser.getMessageContent(message) + "\"");
		}
		else if (messageType.equals(Messages.clientRefuseLock))	{
			System.out.println("Client with IP " + ip + " was refused the lock named \"" 
					+ messageParser.getMessageContent(message) + "\"");
		}
	}
	
	private void takeLock(final String lock)	{
		System.out.println("Client with IP " + ip + " obtained lock named \"" + lock + "\"");
		obtainedLocks.put(lock, new Boolean(true));
	}
	
	public void requestLock(final String lock)	{
		System.out.println("Client with IP " + ip + " is requesting lock named \"" + lock + "\"");
		final String message = messageBuilder.constructMessage(ip, Messages.clientRequestLock, lock);
		messageManager.sendMessage(message, nodeId);
	}
	
	public void releaseLock(final String lock)	{
		System.out.println("Client with IP " + ip + " is releasing lock named " + lock);
		obtainedLocks.put(lock, new Boolean(false));
		final String message = messageBuilder.constructMessage(ip, Messages.clientReleaseLock, lock);
		messageManager.sendMessage(message, nodeId);
	}
	
	public boolean hasLock(final String lock)	{
		Boolean hasLock = obtainedLocks.get(lock);
		return hasLock != null && hasLock.booleanValue();
	}
}
