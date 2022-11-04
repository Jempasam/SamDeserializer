package jempasam.logger;

public class VisibilitySLogger implements SLogger{
	
	
	
	private SLogger decorated;
	private int visibility;
	
	
	
	public VisibilitySLogger(SLogger decorated, int visibility) {
		super();
		this.decorated = decorated;
		this.visibility=visibility;
	}
	
	
	
	@Override
	public SLogger enter() {
		decorated.enter();
		return this;
	}
	
	@Override
	public void exit() {
		decorated.exit();
	}
	
	@Override
	public void log(String message, int level) {
		if(level<=visibility)decorated.log(message, level);
	}
	
	

}
