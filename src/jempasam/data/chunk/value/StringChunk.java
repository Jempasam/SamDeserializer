package jempasam.data.chunk.value;

public class StringChunk extends AbstractValueChunk<String>{
	
	
	
	public StringChunk(String name, String value) {
		super(name,value);
	}
	
	
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	@Override
	public String getValueAsString() {
		return "\""+getValue()+"\"";
	}
}