package nodes;

import java.util.ArrayList;
import java.util.Hashtable;

import constants.LockStatuses;

public class LockManager {
	private Hashtable<String, Integer> locks = new Hashtable<String, Integer>();
	private Hashtable<String, Hashtable<Integer, Boolean>> lockQueues = new Hashtable<String, Hashtable<Integer, Boolean>>();

	public LockManager() {}

	public boolean haveLock(final String lock)	{
		Integer hasLock = locks.get(lock);
		return hasLock != null && getLockStatus(hasLock).equals(LockStatuses.taken);
	}

	public boolean lockAvailable(final String lock)	{
		Integer hasLock = locks.get(lock);
		if (hasLock != null)	{
			hasLock = getLockStatus(hasLock);
			return hasLock.equals(LockStatuses.available) || hasLock.equals(LockStatuses.given);
		}
		else
			return true;
	}

	public boolean isLockGiven(final String lock)	{
		return getLockStatus(locks.get(lock)).equals(LockStatuses.given);
	}

	public boolean isLockProcessing(final String lock)	{
		Integer hasLock = locks.get(lock);
		return hasLock != null && getLockStatus(locks.get(lock)).equals(LockStatuses.processing);
	}

	public boolean isLockTaken(final String lock)	{
		return getLockStatus(locks.get(lock)).equals(LockStatuses.taken);
	}

	public int getLockProcessor(final String lock)	{
		final String lockValue = locks.get(lock).toString();
		if (lockValue.length() > 1)
			return Integer.parseInt(lockValue.substring(1));
		else
			return -1;
	}

	public void makeLockTaken(final String lock)	{
		locks.put(lock, LockStatuses.taken);
	}

	public void makeLockGiven(final String lock)	{
		locks.put(lock, LockStatuses.given);
	}

	public void makeLockProcessing(final String lock, final int clientId)	{
		int lockStatus = Integer.parseInt(LockStatuses.processing.toString() + clientId);
		locks.put(lock, new Integer(lockStatus));
	}

	public void makeLockReleased(final String lock)	{
		locks.put(lock, LockStatuses.available);
	}

	public void setNodeLockQueue(final String lock, ArrayList<Integer> nodes)	{
		Hashtable<Integer, Boolean> lockQueue = lockQueues.get(lock);
		if (lockQueue == null)
			lockQueue = new Hashtable<Integer, Boolean>();
		for (Integer integer: nodes)	{
			lockQueue.put(integer, new Boolean(true));
		}
	}

	public void removeNodeFromLockQueue(final String lock, final int nodeId)	{
		Hashtable<Integer, Boolean> hasLockQueue = lockQueues.get(lock);
		if (hasLockQueue != null)
			hasLockQueue.remove(new Integer(nodeId));
	}

	public boolean nodesLeftInLockQueue(final String lock)	{
		Hashtable<Integer, Boolean> hasLockQueue = lockQueues.get(lock);
		return hasLockQueue != null && hasLockQueue.size() != 0;
	}

	private Integer getLockStatus(Integer lock)	{
		return Integer.parseInt(lock.toString().substring(0,1));
	}
}
