package dnl.jexem.env;

import org.apache.commons.lang.StringUtils;

import dnl.cmd.CommandRunnerFactory;
import dnl.cmd.ProcessExecution;
import dnl.jexem.prcs.ProcessUtils;

/**
 * Encapsulates a process that is managed by JexeM. A
 * <code>ManagedProcess</code> may be started/stop/restarted by JexeM. Also, it
 * can be monitored for life signs and thus restarted if it hangs for any
 * reason.
 * 
 * @author Daniel Orr
 * 
 */
public class ManagedProcess {

	private String command;
	private String grepKiller;
	private ProcessExecution processExecution;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getGrepKiller() {
		return grepKiller;
	}

	public void setGrepKiller(String grepKiller) {
		this.grepKiller = grepKiller;
	}

	public void start() {
		String[] commandAndArgs = StringUtils.split(command);
		processExecution = CommandRunnerFactory.createLocalCommandRunner(commandAndArgs);
		processExecution.executeCommand();
		System.err.println("start");
	}

	public void stop() {
		System.err.println("stop");
		ProcessUtils.killJavaProcesses(grepKiller);
	}

	public void restart() {
		stop();
		start();
	}

	public static void main(String[] args) {
		new ManagedProcess().start();
	}

}
