package jempasam.data.chunk.value;

import jempasam.data.chunk.AbstractDataChunk;

public abstract class AbstractValueChunk<T> extends AbstractDataChunk implements ValueChunk<T>{
	
	
	
	private T value;
	
	
	
	public AbstractValueChunk(String name, T value) {
		super(name);
		this.value=value;
	}



	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value=value;
	}
	
	public abstract String getValueAsString();
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("\"").append(getName()).append("\":").append(getValueAsString());
		return sb.toString();
	}
	
	@Override
	public AbstractValueChunk<T> clone() throws CloneNotSupportedException {
		AbstractValueChunk<T> ret=(AbstractValueChunk<T>)super.clone();
		ret.setValue(getValue());
		return ret;
	}
	

}