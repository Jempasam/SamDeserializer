package jempasam.data.chunk;

import java.util.List;

import jempasam.data.chunk.stream.ChildChunkStream;
import jempasam.data.chunk.stream.DataChunkStream;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.data.chunk.value.ValueChunk;
import jempasam.samstream.Streamable;

public interface ObjectChunk extends DataChunk, Streamable<DataChunk>{
	
	// GET
	DataChunk get(String name);
	DataChunk get(int index) throws IndexOutOfBoundsException;
	
	//FIND
	int find(DataChunk dc);
	
	// ADD
	void add(int index,DataChunk e);
	default void add(DataChunk e) {
		add(size(),e);
	}
	
	// SET
	void set(int index, DataChunk e);
	
	// REMOVE
	boolean remove(DataChunk e);
	default DataChunk remove(String name) {
		DataChunk d=get(name);
		if(d!=null) {
			remove(d);
			return d;
		}
		return null;
	}
	
	default DataChunk remove(int index) throws IndexOutOfBoundsException{
		DataChunk d=get(index);
		remove(d);
		return d;
	}
	
	// SIZE
	int size();
	
	// ITERATORS AND STREAMS
	
	@Override
	default DataChunkStream<DataChunk> stream() {
		return childStream();
	}
	
	default RecursiveChunkStream recursiveStream() {
		return new RecursiveChunkStream(this);
	}
	
	default DataChunkStream<DataChunk> childStream() {
		return new ChildChunkStream(this);
	}
	
	// MISC
	default void replace(DataChunk replaced, DataChunk replacement) {
		int place=find(replaced);
		set(place, replacement);
	}
	
	default void merge(ObjectChunk tomerge) {
		for(DataChunk chunk : tomerge) {
			if(chunk instanceof ValueChunk) {
				DataChunk dc=childStream().values().filter(a->a.getName().equals(chunk.getName())).next().orElse(null);
				try {
					if(dc != null)tomerge.replace(dc, chunk.clone());
					else add(chunk.clone());
				}
				catch (CloneNotSupportedException e) { }
			}
			else {
				DataChunk dc=get(chunk.getName());
				if(dc != null && dc instanceof DataChunk) ((ObjectChunk)dc).merge(((ObjectChunk)chunk));
				else add(chunk);
			}
		}
	}
	
	default List<ValueChunk<?>> fillWithValues(String nameprefix, List<ValueChunk<?>> values) {
		for(DataChunk d : this) {
			try {
				if(d instanceof ValueChunk)values.add((ValueChunk<?>)d.clone());
				else if(d instanceof ObjectChunk)((ObjectChunk) d).fillWithValues(nameprefix+d.getName()+".", values);
			}catch (CloneNotSupportedException e) { }
		}
		return values;
	}
	
}
