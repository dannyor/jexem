package dnl.cmd;

import java.util.Arrays;

public abstract class ProcessExecution {

	protected String command;
	protected String[] args;
	protected int exitStatus;
	protected ExecutionStatus executionStatus;
	protected boolean completed;

	public ProcessExecution(String... command) {
		this.command = command[0];
		this.args = Arrays.copyOfRange(command, 1, command.length);
	}

	public String getFullCommand() {
		StringBuilder sb = new StringBuilder(command);
		for (String arg : args) {
			sb.append(" ");
			sb.append(arg);
		}
		return sb.toString();
	}

	public String getCommand() {
		return command;
	}

	public String[] getArgs() {
		return args;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	/**
	 * <code>true</code> if the underlying process has exited.
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return completed;
	}

	public abstract String getOutput();

	public abstract String getErrorOutput();

	/**
	 * Executes the command with the given timeout. If timeout is reached, the
	 * underlying process is killed.
	 * 
	 * @param timeout
	 *            the time out in millisec. If <=0, an infinite timeout is
	 *            assumed.
	 */
	public abstract void executeCommand(long timeout);

	public abstract void kill();

	/**
	 * Executes the command.
	 */
	public void executeCommand() {
		executeCommand(0);
	}

}