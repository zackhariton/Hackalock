package nodes;

import java.util.ArrayList;

import Master.MessageManager;

import constants.Messages;

import messages.MessageBuilder;

public class Node {
	private final int maxNodeCount = 1000;
	private final int softLockLimit = 5;
	private final int hardLockLimit = 10;
	private final int id;
	private int lockCount = 0;
	private ArrayList<Integer> nodes = new ArrayList<Integer>();
	private MessageManager messageManager;
	private ArrayList<String> clients = new ArrayList<String>();
	private MessageParser messageParser = new MessageParser();
	private MessageBuilder messageBuilder = new MessageBuilder();
	private LockManager lockManager = new LockManager();

	public Node(final int id, MessageManager messageManager)	{
		this.id = id;
		this.messageManager = messageManager;
	}

	public int getId()	{
		return id;
	}

	public void receiveMessage(final String message)	{
		final String messageType = messageParser.getMessageType(message);
		if (messageType.equals(Messages.requestLock))
			processLockRequest(message);
		else if (messageType.equals(Messages.grantLock))
			processGrantedLock(message);
		else if (messageType.equals(Messages.refuseLock))
			processRefusedLock(message);
		else if (messageType.equals(Messages.clientRequestLock))
			processClientRequestedLock(messageParser.getMessageContent(message), messageParser.getMessageClientIp(message));
		else if (messageType.equals(Messages.clientReleaseLock))
			processClientReleasedLock(messageParser.getMessageContent(message), messageParser.getMessageClientIp(message));
		else if (messageType.equals(Messages.clientConnectionRequest))
			processClientConnectionRequest(message);
	}

	private void processLockRequest(final String message)	{
		final String lock = messageParser.getMessageContent(message);
		if (lockManager.haveLock(lock))
			refuseLock(message, lock);
		else	{
			if (lockManager.isLockProcessing(lock))	{
				final int requesterLoad = messageParser.getMessageLoad(message);
				if (requesterLoad < getLoad())
					grantLock(message, lock);
				else	{
					refuseLock(message, lock);

					final String clientIp = messageParser.getMessageClientIp(message);
					final int clientId = clients.size();
					clients.add(clientIp);

					ArrayList<Integer> excludedNodes = messageParser.getExcludedNodes(message);
					excludedNodes.add(messageParser.getMessageSender(message));

					lockManager.makeLockProcessing(lock, clientId);
					askForLock(lock, clientIp, excludedNodes);
				}
			}
			else
				grantLock(message, lock);
		}
	}

	private void grantLock(final String message, final String lock)	{
		final String clientIp = messageParser.getMessageClientIp(message);
		final String messageToSend = messageBuilder.constructMessage(id, Messages.grantLock, lock, getLoad(), clientIp);
		lockManager.makeLockGiven(lock);
		messageManager.sendMessage(messageToSend, messageParser.getMessageSender(message));
	}

	private void refuseLock(final String message, final String lock)	{
		final String clientIp = messageParser.getMessageClientIp(message);
		final String messageToSend = messageBuilder.constructMessage(id, Messages.refuseLock, lock, getLoad(), clientIp);
		messageManager.sendMessage(messageToSend, messageParser.getMessageSender(message));
	}

	private void processGrantedLock(final String message)	{
		final String lock = messageParser.getMessageContent(message);
		if (!lockManager.isLockGiven(lock))	{
			lockManager.removeNodeFromLockQueue(lock, messageParser.getMessageSender(message));
			if (!lockManager.nodesLeftInLockQueue(lock))	{
				takeLock(lock);
				System.out.println("Node with ID " + id + " granted lock named \"" + lock + "\" to client with ip " + messageParser.getMessageClientIp(message));
				final String clientMessage = messageBuilder.constructMessage(id, Messages.clientGrantLock, lock);
				messageManager.sendMessage(clientMessage, messageParser.getMessageClientIp(message));
			}
		}
	}

	private void processRefusedLock(final String message)	{
		final String lock = messageParser.getMessageContent(message);
		final String clientMessage = messageBuilder.constructMessage(id, Messages.refuseLock, lock);
		final int clientId = lockManager.getLockProcessor(lock);
		if (clientId != -1)
			messageManager.sendMessage(clientMessage, clientId);
	}

	private void processClientRequestedLock(final String lock, final String clientIp)	{
		if (lockManager.lockAvailable(lock))	{
			final int clientId = clients.indexOf(clientIp);
			lockManager.makeLockProcessing(lock, clientId);
			askForLock(lock, clientIp);
		}
		else	{
			final String message = messageBuilder.constructMessage(id, Messages.clientRefuseLock, lock);
			messageManager.sendMessage(message, clientIp);
		}
	}

	private void processClientReleasedLock(final String lock, final String clientIp)	{
		if (lockManager.isLockTaken(lock))	{
			System.out.println("Node with ID " + id + " is releasing lock named \"" + lock + "\"");
			lockManager.makeLockReleased(lock);
			final String message = messageBuilder.constructMessage(id, Messages.clientLockReleased, lock);
			messageManager.sendMessage(message, clientIp);
		}
		else	{
			//Throw an error
		}
	}

	private void processClientConnectionRequest(final String message)	{
		clients.add(messageParser.getMessageClientIp(message));
	}

	private void takeLock(final String lock)	{
		lockManager.makeLockTaken(lock);
		lockCount++;
		if (lockCount == softLockLimit)	{
			startNewNode();
		}
	}

	private void askForLock(final String lock, final String clientIp)	{
		final String requestLockMessage = messageBuilder.constructMessage(id, Messages.requestLock, lock, getLoad(), clientIp);
		if (nodes.size() > 0) {
			lockManager.setNodeLockQueue(lock, nodes);
			for (Integer node: nodes)	{
				messageManager.sendMessage(requestLockMessage, node.intValue());
			}
		}
		else	{
			lockManager.makeLockTaken(lock);
			final String messageToSend = messageBuilder.constructMessage(id, Messages.clientGrantLock, lock, getLoad(), clientIp);
			messageManager.sendMessage(messageToSend, clientIp);
		}
	}

	private void askForLock(final String lock, final String clientIp, final ArrayList<Integer> excludedNodes)	{
		final String requestLockMessage = messageBuilder.constructMessage(id, Messages.requestLock, lock, getLoad(), clientIp, excludedNodes);
		for (Integer node: nodes)	{
			if (!excludedNodes.contains(node))
				messageManager.sendMessage(requestLockMessage, node.intValue());
		}
	}

	public void addNode(final int nodeId)	{
		nodes.add(nodeId);
	}

	private void startNewNode()	{
		System.out.println("Spinning up a new node");
		//Spin up a new node
	}

	private int getLoad()	{
		return lockCount*maxNodeCount+id;
	}
}
