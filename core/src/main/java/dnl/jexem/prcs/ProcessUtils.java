package dnl.jexem.prcs;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import au.com.bytecode.opencsv.CSVReader;
import dnl.cmd.ProcessExecution;
import dnl.cmd.CommandRunnerFactory;

/**
 * 
 * @author Daniel Orr
 * 
 */
public class ProcessUtils {

	private static Pattern psLinePattern = Pattern
			.compile("(\\w+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{1,2})\\s+(\\w+|\\d{2}:\\d{2})\\s+(\\u003F?|[\\w/\\d]+)\\s+(\\d{2}:\\d{2}:\\d{2})\\s+(.*)");
	private static Pattern jpsLinePattern = Pattern.compile("(\\d+)\\s{1}([\\w|/|.]+)(.*)");

	public static void main(String[] args) {
		List<JavaProcessInfo> javaProcesses = getJavaProcesses();
		for (JavaProcessInfo javaProcessInfo : javaProcesses) {
			System.out.println(javaProcessInfo.toStringNixStyle());
		}
	}

	/**
	 * Gets a list of processes running on this machine that is based on the
	 * output of a command. On NIX systems it executes a 'ps -aef' and on Win it
	 * executes 'tasklist /v'.
	 * 
	 * @return
	 */
	public static List<ProcessInfo> getProcesses() {
		if (SystemUtils.IS_OS_LINUX) {
			return getNixProcesses();
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			return getWinProcesses();
		}
		return null;
	}

	private static List<ProcessInfo> getWinProcesses() {
		ProcessExecution commandExecution = CommandRunnerFactory.createLocalCommandRunner(
				"tasklist", "/v", "/fo", "CSV");
		commandExecution.executeCommand(1000);
		if (commandExecution.getExitStatus() != 0) {
			System.err.println("Error executing: " + commandExecution.getFullCommand());
			return Collections.emptyList();
		}
		String output = commandExecution.getOutput();
		CSVReader reader = new CSVReader(new StringReader(output));
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
		try {
			// first line is column titles and thus we get rid of it
			String[] nextLine = reader.readNext();
			while ((nextLine = reader.readNext()) != null) {
				try {
					ProcessInfo pi = new ProcessInfo();
					pi.setCmd(nextLine[0]);
					pi.setProcessID(Integer.parseInt(nextLine[1]));
					String mem = nextLine[4];
					mem = StringUtils.remove(mem, ' ');
					mem = StringUtils.remove(mem, 'K');
					mem = StringUtils.remove(mem, ',');
					pi.setMemUsage(Integer.parseInt(mem) * 1024);
					pi.setUserID(nextLine[6]);
					processes.add(pi);
				} catch (Exception e) {
					// System.err.println("err parsing line> "+ArrayUtils.toString(nextLine)+"> "+e.toString());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return processes;
	}

	private static List<ProcessInfo> getNixProcesses() {
		ProcessExecution commandExecution = CommandRunnerFactory.createLocalCommandRunner("ps",
				"-aef");
		commandExecution.executeCommand(1000);
		if (commandExecution.getExitStatus() != 0) {
			System.err.println("Error executing: " + commandExecution.getFullCommand());
			return Collections.emptyList();
		}
		String output = commandExecution.getOutput();
		String[] lines = StringUtils.split(output, "\n\r");
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
		for (int i = 1; i < lines.length; i++) {
			processes.add(parsePsLine(lines[i]));
		}

		return processes;
	}

	private static ProcessInfo parsePsLine(String line) {

		Matcher matcher = psLinePattern.matcher(line);
		if (matcher.matches()) {
			ProcessInfo pi = new ProcessInfo();
			pi.setUserID(matcher.group(1));
			pi.setProcessID(Integer.parseInt(matcher.group(2)));
			pi.setParentProcessID(Integer.parseInt(matcher.group(3)));
			pi.setCpuUsage(Integer.parseInt(matcher.group(4)));
			pi.setStartTime(matcher.group(5));
			pi.setCmd(matcher.group(8));
			return pi;
		} else {
			System.err.println("could not match: " + line);
			return null;
		}
	}

	/**
	 * Attempts to kill the process with the given id.
	 * 
	 * @param pid
	 */
	public static void killProcess(int pid) {
		if (SystemUtils.IS_OS_LINUX) {
			ProcessExecution commandExecution = CommandRunnerFactory.createLocalCommandRunner(
					"kill", Integer.toString(pid));
			commandExecution.executeCommand(1000);
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			ProcessExecution commandExecution = CommandRunnerFactory.createLocalCommandRunner(
					"taskkill", "/PID", Integer.toString(pid));
			commandExecution.executeCommand(1000);
		}
	}

	/**
	 * Kills all processes that match the given expression. The expression is
	 * being searched in the <code>mainOrJar</code> and <code>arguments</code>
	 * attributes of existing Java processes. Carefull with the expression
	 * though ...
	 * 
	 * @param grepExpression
	 */
	public static void killJavaProcesses(String grepExpression) {
		List<JavaProcessInfo> javaProcesses = grepJavaProcesses(grepExpression);
		for (JavaProcessInfo javaProcessInfo : javaProcesses) {
			killProcess(javaProcessInfo.getProcessID());
		}
	}

	/**
	 * Uses the given expression to resolve Java processes.
	 * 
	 * @param grepExpression
	 * @return
	 */
	public static List<JavaProcessInfo> grepJavaProcesses(String grepExpression) {
		List<JavaProcessInfo> result = new ArrayList<JavaProcessInfo>();
		List<JavaProcessInfo> javaProcesses = getJavaProcesses();
		for (JavaProcessInfo javaProcessInfo : javaProcesses) {
			if (javaProcessInfo.getMainOrJar().indexOf(grepExpression) >= 0) {
				result.add(javaProcessInfo);
			} else if (javaProcessInfo.getArguments() != null
					&& javaProcessInfo.getArguments().indexOf(grepExpression) >= 0) {
				result.add(javaProcessInfo);
			}
		}
		return result;
	}

	/**
	 * Gets info for Java processes running on this machine. This method parses
	 * the output of the jps command and currently returns
	 * <code>JavaProcessInfo</code> that have only a processID, main/jar
	 * attribute, and arguments.
	 * 
	 * @return
	 */
	public static List<JavaProcessInfo> getJavaProcesses() {
		ProcessExecution commandExecution = CommandRunnerFactory.createLocalCommandRunner("jps",
				"-lm");
		commandExecution.executeCommand(1000);
		if (commandExecution.getExitStatus() != 0) {
			System.err.println("Error executing: " + commandExecution.getFullCommand());
			return Collections.emptyList();
		}
		String output = commandExecution.getOutput();
		List<JavaProcessInfo> processInfos = new ArrayList<JavaProcessInfo>();
		String[] lines = StringUtils.split(output, "\n\r");
		for (String line : lines) {
			JavaProcessInfo processInfo = parseJpsLine(line);
			// add anything that is not the excecution of jps
			if (!processInfo.getMainOrJar().endsWith(".Jps")) {
				processInfos.add(processInfo);
			}
		}

		return processInfos;
	}

	private static JavaProcessInfo parseJpsLine(String line) {

		Matcher matcher = jpsLinePattern.matcher(line);
		if (matcher.matches()) {
			JavaProcessInfo pi = new JavaProcessInfo();
			pi.setProcessID(Integer.parseInt(matcher.group(1)));
			pi.setMainOrJar(matcher.group(2));
			pi.setArguments(matcher.group(3));
			return pi;
		} else {
			System.err.println("could not match: " + line);
			return null;
		}
	}

}
