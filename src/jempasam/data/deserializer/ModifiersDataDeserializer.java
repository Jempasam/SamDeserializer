package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.Collection;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.modifier.DataModifier;

public class ModifiersDataDeserializer implements DataDeserializer{
	
	
	
	private Collection<DataModifier> modifiers;
	private DataDeserializer decorated;
	
	
	
	public ModifiersDataDeserializer(DataDeserializer decorated, Collection<DataModifier> modifiers) {
		super();
		this.modifiers = modifiers;
		this.decorated = decorated;
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		ObjectChunk ret=decorated.loadFrom(i);
		for(DataModifier modifier : modifiers) {
			modifier.applyOn(ret);
		}
		return ret;
	}
	
	
}
