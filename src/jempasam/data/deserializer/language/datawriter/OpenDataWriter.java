package jempasam.data.deserializer.language.datawriter;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class OpenDataWriter implements DataWriter {
	
	
	
	private String name;
	
	
	@LoadableParameter(paramnames = {"name"})
	public OpenDataWriter(String name) {
		super();
		this.name = name;
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		ObjectChunk object=new SimpleObjectChunk(name);
		objectStack.get(objectStack.size()-1).add(object);
		objectStack.add(object);
	}
}
