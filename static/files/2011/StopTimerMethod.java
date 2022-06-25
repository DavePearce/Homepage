import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;


public final class StopTimerMethod extends Thread {
	public final Thread owner;
	public final Method method;
	public final Object receiver;
	public final Object[] args;	
	public volatile Object value;
	public volatile Throwable exception;	
	public volatile boolean finished;
	
	public StopTimerMethod(Thread owner, Method method, Object receiver, Object... args) {
		this.owner = owner;
		this.method = method;
		this.receiver = receiver;
		this.args = args;		
	}
	
	public void run() {
		try {
			value = method.invoke(receiver, args);			
		} catch(InvocationTargetException ex) {
			exception = ex.getCause();
		} catch(Throwable ex) {
			exception = ex;
		}
		// signal completion
		this.finished = true;
		owner.interrupt();
	}
	
	public static final int GRANULARITY = 50;
	
	public static Object exec(int timeout, Method method, Object receiver,
			Object... args) throws Throwable {
		
		StopTimerMethod stm = new StopTimerMethod(Thread.currentThread(),method,receiver,args);		
		stm.start();		
		int total = 0;
		while(total < timeout) {
			try {
				Thread.sleep(GRANULARITY);
			} catch(InterruptedException e) {
				// should be dead code
			}
			if (stm.finished) {
				if (stm.exception != null) {
					// clear interrupted status
					throw stm.exception;
				} else {
					// clear interrupted status
					return stm.value;
				}
		    }
			total = total + GRANULARITY;
		}		
		// ok, timeout now in effect
		stm.stop(); // why do I have to use a deprecated method?
		throw new TimeoutException("timed out after " + timeout + "ms");
	}	
}
