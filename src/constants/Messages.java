package constants;

public interface Messages {
	static final String requestLock = "lockRequest";
	static final String refuseLock = "lockRefuse";
	static final String grantLock = "lockGrant";
	
	static final String clientRequestLock = "clientRequestLock";
	static final String clientReleaseLock = "clientReleaseLock";
	static final String clientLockReleased = "clientLockReleased";
	static final String clientGrantLock = "clientGrantLock";
	static final String clientRefuseLock = "clientRefuseLock";
	static final String clientConnectionRequest = "clientConnectionRequest";
}
