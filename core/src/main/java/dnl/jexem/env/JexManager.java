package dnl.jexem.env;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dnl.jexem.env.ManagedProcess;

public class JexManager {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		String[] paths = {
				"classpath:applicationContext.xml"
		};
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(paths);
		
		Map<?,?> managedProcessesMap = applicationContext.getBeansOfType(ManagedProcess.class);
		Collection<ManagedProcess> managedProcesses = (Collection<ManagedProcess>) managedProcessesMap.values();
		for (ManagedProcess managedProcess : managedProcesses) {
			managedProcess.start();
		}
		while(true){
			Thread.sleep(5000);
		}
	}
	

}
