package nodes;

import java.util.ArrayList;

import constants.Messages;

public class MessageParser {
//	private ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	private final String seperator = ";";
	private final String exclusionToken = ":";
	
	public MessageParser()	{}
	
	public int getMessageSender(final String message)	{
		return Integer.parseInt(message.substring(0, indexOfBreak(1, message)-1));
	}
	
	public String getMessageType(final String message)	{
		return message.substring(indexOfBreak(1, message), indexOfBreak(2, message)-1);
	}
	
	public String getMessageContent(final String message)	{
		return message.substring(indexOfBreak(2, message), indexOfBreak(3, message)-1);
	}
	
	public int getMessageLoad(final String message)	{
		return Integer.parseInt(message.substring(indexOfBreak(3, message), indexOfBreak(4, message)-1));
	}
	
	public String getMessageClientIp(final String message)	{
		if (!isClientRequest(message))
			return message.substring(indexOfBreak(4, message), indexOfBreak(5, message));
		else
			return message.substring(0, indexOfBreak(1, message)-1);
	}
	
	private boolean isClientRequest(final String message)	{
		return getMessageType(message).equals(Messages.clientConnectionRequest)
				|| getMessageType(message).equals(Messages.clientRequestLock)
				|| getMessageType(message).equals(Messages.clientReleaseLock);
	}
	
	public ArrayList<Integer> getExcludedNodes(final String message)	{
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		int startIndex = message.indexOf(exclusionToken)+1;
		int endIndex = message.indexOf(seperator, startIndex);
		while (endIndex != -1)	{
			returnList.add(Integer.parseInt(message.substring(startIndex, endIndex)));
			startIndex = endIndex+1;
			endIndex = message.indexOf(seperator, startIndex);
		}
		returnList.add(Integer.parseInt(message.substring(startIndex, message.length())));
		return returnList;
	}
	
	private int indexOfBreak(final int breakCount, final String message)	{
		int returnInt = 0;
		for (int count = 0; count < breakCount; count++)	{
			int temp  = message.indexOf(seperator, returnInt);
			if (temp != -1)
				returnInt = temp+1;
			else	{
				returnInt = message.length();
				break;
			}
		}
		return returnInt;
	}
}
