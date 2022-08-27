package jempasam.data.deserializer.language.datawriter;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class MemberDataWriter implements DataWriter {
	
	
	
	private String name;
	
	
	@LoadableParameter(paramnames = {"name"})
	public MemberDataWriter(String name) {
		super();
		this.name = name;
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		objectStack.get(objectStack.size()-1).add(new StringChunk(name, token));
	}
}
