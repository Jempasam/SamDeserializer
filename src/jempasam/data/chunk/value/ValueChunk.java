package jempasam.data.chunk.value;

import jempasam.data.chunk.DataChunk;

public interface ValueChunk<T> extends DataChunk{
	T getValue();
	void setValue(T value);
	Class<T> getType();
	
	@SuppressWarnings("unchecked")
	default boolean setIfSameType(ValueChunk<?> copied) {
		if(getType()==copied.getType()) {
			setValue((T)copied);
			return true;
		}
		return false;
	}
}
