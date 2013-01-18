package eu.boss.hosteditor;

public class Host {

	private String ipAddress;
	private String hostName;

	public Host(String fileLine) {
		super();
		this.ipAddress = fileLine.substring(0, fileLine.indexOf(" "));
		this.hostName = fileLine.substring(fileLine.indexOf(" ") + 1, fileLine.length());
	}

	public Host(String ipAddress, String hostName) {
		super();
		this.ipAddress = ipAddress;
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String toString() {
		return ipAddress + " " + hostName + "\n";
	}

}
