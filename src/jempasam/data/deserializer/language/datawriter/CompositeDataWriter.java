package jempasam.data.deserializer.language.datawriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class CompositeDataWriter implements DataWriter {
	
	
	
	private List<DataWriter> writers;
	
	
	
	public CompositeDataWriter(DataWriter[] writers) {
		super();
		this.writers = new ArrayList<>(Arrays.asList(writers));
	}
	
	@LoadableParameter
	public CompositeDataWriter() {
		super();
		this.writers = new ArrayList<>();
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		for(DataWriter dw : writers)dw.register(token, objectStack);
	}
	
	@LoadableParameter(name = "")
	public void addWriter(DataWriter writer) {
		writers.add(writer);
	}
}
