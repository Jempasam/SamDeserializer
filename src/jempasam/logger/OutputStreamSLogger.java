package jempasam.logger;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamSLogger implements SLogger{
	OutputStream output;
	String prefix="";
	int level;

	public OutputStreamSLogger(OutputStream output) {
		super();
		this.output = output;
	}

	@Override
	public void log(String message, int level) {
		try {
			output.write(prefix.getBytes());
			output.write(message.getBytes());
			output.write("\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enter(String name) {
		try {
			output.write(prefix.getBytes());
			output.write("In ".getBytes());
			output.write(name.getBytes());
			output.write(":\n".getBytes());
			setlevel(level+1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exit() {
		if(level>0)setlevel(level-1);
	}
	
	private void setlevel(int a) {
		level=a;
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)sb.append("-> ");
		prefix=sb.toString();
	}

}
