package jempasam.logger;

public class SLoggers {
	
	
	
	private SLoggers() {}
	
	
	
	public static final SLogger OUT=new OutputStreamSLogger(System.out);
	public static final SLogger ERR=new OutputStreamSLogger(System.err);
	public static final SLogger VOID=new SLogger() {
		@Override public void log(String message, int level) { }
		@Override public void exit() { }
		@Override public SLogger enter() { return this; }
	};
}
