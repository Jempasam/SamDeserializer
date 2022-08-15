package jempasam.data.chunk.value;

public class FloatChunk extends AbstractValueChunk<Float>{
	
	
	
	public FloatChunk(String name, Float value) {
		super(name,value);
	}
	
	
	
	@Override
	public Class<Float> getType() {
		return Float.class;
	}
	
	@Override
	public String getValueAsString() {
		return Float.toString(getValue());
	}
}