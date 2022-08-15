package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jempasam.data.chunk.ObjectChunk;

public class AfterLoadDataDeserializer implements DataDeserializer{
	
	
	
	private List<Consumer<ObjectChunk>> observers;
	private DataDeserializer decorated;
	
	
	
	public AfterLoadDataDeserializer(DataDeserializer decorated) {
		observers=new ArrayList<>();
		this.decorated=decorated;
	}
	
	
	
	public void register(Consumer<ObjectChunk> observer) {
		observers.add(observer);
	}



	@Override
	public ObjectChunk loadFrom(InputStream i) {
		ObjectChunk ret=decorated.loadFrom(i);
		for(Consumer<ObjectChunk> o : observers) {
			o.accept(ret);
		}
		return ret;
	}
	
	
}
