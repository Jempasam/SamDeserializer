package jempasam.data.modifier;

import java.util.function.UnaryOperator;

import jempasam.data.chunk.ObjectChunk;

public interface DataModifier extends UnaryOperator<ObjectChunk> {
	
	void applyOn(ObjectChunk data);
	
	@Override
	default ObjectChunk apply(ObjectChunk t) {
		try {
			ObjectChunk ret=(ObjectChunk)t.clone();
			applyOn(ret);
			return ret;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
