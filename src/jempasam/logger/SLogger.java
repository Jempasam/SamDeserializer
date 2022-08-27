package jempasam.logger;

public interface SLogger {
	
	
	
	public static final int ERROR=100;
	public static final int WARNING=200;
	public static final int INFO=300;
	public static final int VERBOSE=400;
	public static final int DEBUG=500;
	
	
	
	void log(String message, int level);
	SLogger enter();
	void exit();
	
	
	
	default void error(String message) { log(message,ERROR); }
	default void warning(String message) { log(message,WARNING); }
	default void info(String message) { log(message,INFO); }
	default void verbose(String message) { log(message,VERBOSE); }
	default void debug(String message) { log(message,DEBUG); }
}
