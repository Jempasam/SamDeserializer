package jempasam.data.chunk.stream;

import java.util.function.Consumer;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.ValueChunk;

public class ValueChunkStream<T> implements DataChunkStream<ValueChunk<T>>{
	
	
	
	private DataChunkStream<?> dataChunkIterator;
	private Class<? extends T> type;
	
	
	
	public ValueChunkStream(Class<? extends T> type, DataChunkStream<?> dataChunkIterator) {
		super();
		this.dataChunkIterator = dataChunkIterator;
		this.type=type;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ValueChunk<T> tryNext() {
		if(!dataChunkIterator.hasSucceed())return null;
		DataChunk value;
		while(true) {
			value=dataChunkIterator.tryNext();
			if(!dataChunkIterator.hasSucceed())return null;
			if(value instanceof ValueChunk && type.isAssignableFrom(((ValueChunk<?>)value).getType())) {
				return (ValueChunk<T>)value;
			}
		}
	}
	
	@Override
	public boolean hasSucceed() {
		return dataChunkIterator.hasSucceed();
	}
	
	@Override
	public ObjectChunk actualParent() {
		return dataChunkIterator.actualParent();
	}
	
	public void setValue(T value) {
		forEach(valuechunk->valuechunk.setValue(value));
	}
	
	public void replaceValue(T oldvalue, T value) {
		forEach(valuechunk->{
			if(valuechunk.getValue().equals(value))valuechunk.setValue(value);
		});
	}
	
	@Override
	public void reset() {
		dataChunkIterator.reset();
	}
	
	@Override
	public synchronized void syncNext(Consumer<ValueChunk<T>> action) {
		ValueChunk<T> v=tryNext();
		if(hasSucceed())action.accept(v);
	}
}
