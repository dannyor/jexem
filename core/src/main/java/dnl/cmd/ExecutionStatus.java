package dnl.cmd;

public enum ExecutionStatus {

	COMMAND_NOT_FOUND,
	/**
	 * Indicates that the process had output something to the error strem. In
	 * most cases this means that the process has failed and exited.
	 */
	HAS_ERROR_OUTPUTS

}
