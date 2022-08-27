package jempasam.data.deserializer.language.datawriter;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class FixedMemberDataWriter implements DataWriter {
	
	
	
	private String name;
	private String value;
	
	
	@LoadableParameter(paramnames = {"name","value"})
	public FixedMemberDataWriter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		objectStack.get(objectStack.size()-1).add(new StringChunk(name, value));
	}
}
