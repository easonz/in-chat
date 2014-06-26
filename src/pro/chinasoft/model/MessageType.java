package pro.chinasoft.model;

public enum MessageType
{
	send,     // 0 : msg from yourself
	recive,   // 0 : msg from friends
	unread;   // 0 : msg from friends and not read
	
	public int value() {
		return this.ordinal();
	}
	
	public static MessageType valueOf(int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) {
			throw new IndexOutOfBoundsException("Invalid ordinal");
		}
		return values()[ordinal];
	}
}