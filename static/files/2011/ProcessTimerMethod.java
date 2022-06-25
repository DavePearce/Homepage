import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;


public class ProcessTimerMethod {
	private static final int GRANULARITY = 50; // ms
	
	public static String JAVA_CMD = System.getenv("JAVA_CMD");
	public static String CLASSPATH = System.getenv("CLASSPATH");
	
	public static Object exec(int timeout, String receiver, String method, 
			Object... args) throws Throwable {
		ProcessBuilder builder = new ProcessBuilder(JAVA_CMD,"-cp",CLASSPATH, "ProcessTimerMethod");
		Process child = builder.start();
		try {
			// first, send over the method in question + args
			OutputStream output = child.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(output);
			oos.writeObject(receiver);
			oos.writeObject(method);		
			oos.writeObject(args);		
			oos.flush();		
			// second, read the result whilst checking for a timeout
			InputStream input = child.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(input);
			int total = 0;
			while(total < timeout && input.available() == 0) {
				try {
					Thread.sleep(GRANULARITY);
				} catch(InterruptedException e) {				
				}
				total = total + GRANULARITY;
			}
			if(input.available() != 0) {
				return ois.readObject();								
			} else {				
				throw new TimeoutException("timed out after " + timeout + "ms");
			}
		} finally {
			// make sure child process is destroyed.
			child.destroy();
		}
	}
	
	public static void main(String[] args) throws IOException {			
		ObjectInputStream ois = new ObjectInputStream(System.in);
		ObjectOutputStream oos = new ObjectOutputStream(System.out);
		
		try {
			System.setOut(System.err); // to divert any output from the child code
			String receiver = (String) ois.readObject();
			String name = (String) ois.readObject();
			Object[] arguments = (Object[]) ois.readObject();

			// now, find the object
			Class[] paramtypes = new Class[arguments.length];
			int i = 0;
			for (Object arg : arguments) {
				paramtypes[i++] = arg.getClass();
			}
			Class clazz = Class.forName(receiver);
			Method method = clazz.getMethod(name, paramtypes);

			Object value = method.invoke(null, arguments);
			oos.writeObject(value);			
		} catch(InvocationTargetException e) { 
			Throwable cause = e.getCause();
			String result = cause.toString();
			/*			
			for(StackTraceElement ste : cause.getStackTrace()) {
				result += ste + "\n";
			}
			*/
			oos.writeObject(result);			
		} catch (Exception e) {
			oos.writeObject(e.toString());			
		}
		oos.flush();
	}
}
