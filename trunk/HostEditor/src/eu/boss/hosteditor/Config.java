package eu.boss.hosteditor;

public class Config {
	public static final int NEW_HOST = 0;
	public static final int EDIT_HOST = 1;
	public static final int RESULT_DELETE = 3;

	public static final String HOST_FILE = "/etc/hosts";
	public static final String HOST = "HOST";
	public static final String IP = "IP";
	public static final String IS_NEW = "IS_NEW";
	
	public static Host BASE_HOST;
	static{
		BASE_HOST=new Host("127.0.0.1", "localhost");
	}

}
