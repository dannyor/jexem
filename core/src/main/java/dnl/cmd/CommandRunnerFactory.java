package dnl.cmd;

/**
 * A factory that centralizes <code>CommandExecution</code> creation.
 * 
 * @author Daniel Orr
 * 
 */
public class CommandRunnerFactory {

	/**
	 * Creates a <code>CommandExecution</code> that will execute a command locally.
	 * 
	 * @param command
	 * @return
	 */
	public static ProcessExecution createLocalCommandRunner(String... command) {
		LocalProcessExcecution commandRunner = new LocalProcessExcecution(command);
		return commandRunner;
	}
}
