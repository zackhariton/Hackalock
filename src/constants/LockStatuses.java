package constants;

public interface LockStatuses {
	static final Integer available = new Integer(0);
	static final Integer processing = new Integer(1);
	static final Integer given = new Integer(2);
	static final Integer taken = new Integer(3);
}
