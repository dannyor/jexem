package dnl.jexem.prcs;

public class ProcessInfo {

	private int processID;
	private int parentProcessID;
	private String cmd;

	private int cpuUsage;
	private int memUsage;
	private String startTime;
	private String userID;

	public ProcessInfo() {
	}

	public ProcessInfo(int processID, int parentID, String cmd) {
		super();
		this.processID = processID;
		this.parentProcessID = parentID;
		this.cmd = cmd;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public int getParentProcessID() {
		return parentProcessID;
	}

	public void setParentProcessID(int parentProcessID) {
		this.parentProcessID = parentProcessID;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(int cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public int getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(int memUsage) {
		this.memUsage = memUsage;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		return "ProcessInfo [userID=" + userID + ", processID=" + processID + ", parentProcessID="
				+ parentProcessID + ", cpuUsage=" + cpuUsage + ", startTime=" + startTime
				+ ", cmd=" + cmd + "]";
	}

	public String toStringNixStyle() {
		return userID + f(processID) + f(parentProcessID) + f(cpuUsage) + f(startTime) + f(cmd);
	}

	private String f(Object obj) {
		if (obj == null) {
			return "";
		}
		return " " + String.valueOf(obj);
	}
}
