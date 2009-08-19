package dnl.jexem.prcs;

public class JavaProcessInfo extends ProcessInfo {
	private String mainOrJar;
	private String arguments;

	public String getMainOrJar() {
		return mainOrJar;
	}

	public void setMainOrJar(String mainOrJar) {
		this.mainOrJar = mainOrJar;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String toStringNixStyle() {
		return getProcessID() + f(getMainOrJar()) + f(getArguments());
	}

	private String f(Object obj) {
		if (obj == null) {
			return "";
		}
		return " " + String.valueOf(obj);
	}
}
