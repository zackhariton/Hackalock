package messages;

import java.util.Hashtable;

import nodes.Node;

public class InternalMessageManager {
	Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node>();
	
	public InternalMessageManager()	{}
	
	public void sendMessage(final String message, final int nodeId)	{
		nodes.get(new Integer(nodeId)).receiveMessage(message);
	}
}
