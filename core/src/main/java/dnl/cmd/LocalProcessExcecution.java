package dnl.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class LocalProcessExcecution extends ProcessExecution {

	private static Logger logger = Logger.getLogger(LocalProcessExcecution.class.getSimpleName());
	
	private String output;
	private String errorOutput;
	private long startTime;
	private Process process;

	public LocalProcessExcecution(String... command) {
		super(command);
	}

	public String getOutput() {
		return output;
	}

	public String getErrorOutput() {
		return errorOutput;
	}

	public void executeCommand(long timeout) {
		List<String> commandAndArgs = new ArrayList<String>();
		commandAndArgs.add(command);
		commandAndArgs.addAll(Arrays.asList(args));
		ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
		try {
			process = pb.start();
			this.startTime = System.currentTimeMillis();
			if(timeout > 0){
				new ControlThread(timeout, process);
			}
			InputStream inputStream = process.getInputStream();
			output = IOUtils.toString(inputStream);
			InputStream errorStream = process.getErrorStream();
			errorOutput = IOUtils.toString(errorStream);
			process.waitFor();
			this.exitStatus = process.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
			this.exitStatus = 1;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void kill() {
		if(process != null){
			process.destroy();
		}
		else {
			logger.info("Cannot kill null process.");
		}
	}
	
	private class ControlThread extends Thread {
		private long timeout;
		private Process p;

		public ControlThread(long timeout, Process p) {
			this.timeout = timeout;
			this.p = p;
		}

		public void run() {
			while (System.currentTimeMillis() - startTime < timeout) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.p.destroy();
		}
	}

}
