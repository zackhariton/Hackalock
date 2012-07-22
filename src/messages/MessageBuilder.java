package messages;

import java.util.ArrayList;

public class MessageBuilder {
	private final String seperator = ";";
	private final String exclusionToken = ":";
	
	public MessageBuilder(){}

	public String constructMessage(final int sender, final String type, final String content, final int load,
			final String clientIp, final ArrayList<Integer> excludedNodes)	{
		if (excludedNodes.size() == 0)
			return sender + seperator + type + seperator + content + seperator + load + seperator + clientIp;
		String returnString = sender + seperator + type + seperator + content + seperator + load + seperator + clientIp
				+ exclusionToken + excludedNodes.get(0);
		for (int index = 0; index < excludedNodes.size(); index++)	{
			returnString += seperator + excludedNodes.get(index);
		}
		return returnString;
	}

	public String constructMessage(final int sender, final String type, final String content, final int load,
			final String clientIp)	{
		if (clientIp.length() == 0)
			System.out.println("Test");
		return sender + seperator + type + seperator + content + seperator + load + seperator + clientIp;
	}

	public String constructMessage(final int sender, final String type, final String content)	{
		return sender + seperator + type + seperator + content + seperator;
	}
	
	public String constructMessage(final String senderIp, final String type, final String content)	{
		return senderIp + seperator + type + seperator + content + seperator;
	}
}
