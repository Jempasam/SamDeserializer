package jempasam.data.deserializer.language.datawriter;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class OpenAndMemberDataWriter implements DataWriter {
	
	
	
	private String name;
	private String memberName;
	
	
	@LoadableParameter(paramnames = {"name","memberName"})
	public OpenAndMemberDataWriter(String name, String memberName) {
		super();
		this.name = name;
		this.memberName = memberName;
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		ObjectChunk object=new SimpleObjectChunk(name);
		object.add(new StringChunk(memberName, token));
		objectStack.get(objectStack.size()-1).add(object);
		objectStack.add(object);
	}
}
