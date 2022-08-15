package jempasam.data.chunk.value;

public class IntegerChunk extends AbstractValueChunk<Integer>{
	
	
	
	public IntegerChunk(String name, Integer value) {
		super(name,value);
	}
	
	
	
	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
	
	@Override
	public String getValueAsString() {
		return Integer.toString(getValue());
	}
}