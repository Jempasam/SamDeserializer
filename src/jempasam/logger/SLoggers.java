package jempasam.logger;

public class SLoggers {
	
	
	
	private SLoggers() {}
	
	
	
	public static final SLogger OUT=new OutputStreamSLogger(System.out);
	public static final SLogger ERR=new OutputStreamSLogger(System.err);
}
