package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jempasam.data.chunk.ObjectChunk;

public class OnLoadDataDeserializer implements DataDeserializer{
	
	
	
	private List<Consumer<DataDeserializer>> observers;
	private DataDeserializer decorated;
	
	
	
	public OnLoadDataDeserializer(DataDeserializer decorated) {
		observers=new ArrayList<>();
		this.decorated=decorated;
	}
	
	
	
	public void register(Consumer<DataDeserializer> observer) {
		observers.add(observer);
	}



	@Override
	public ObjectChunk loadFrom(InputStream i) {
		for(Consumer<DataDeserializer> o : observers) {
			o.accept(decorated);
		}
		return decorated.loadFrom(i);
	}
	
	
}
