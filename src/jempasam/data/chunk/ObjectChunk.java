package jempasam.data.chunk;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jempasam.data.chunk.stream.ChildChunkStream;
import jempasam.data.chunk.stream.DataChunkStream;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.data.chunk.value.ValueChunk;
import jempasam.samstream.Streamable;
import jempasam.samstream.collectors.SamCollector;

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
	
	default DataChunk removeAll(String ...name) {
		Set<String> toremove=new HashSet<>();
		Collections.addAll(toremove, name);
		for(int i=0; i<size(); i++) {
			DataChunk removed=get(i);
			if(toremove.contains(removed.getName())) {
				remove(i);
				i--;
			}
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
	
	// GET
	@SuppressWarnings("unchecked")
	default <T> ValueChunk<T> get(Class<T> type, String name) {
		DataChunk ret=get(name);
		if(ret instanceof ValueChunk && ((ValueChunk<?>)ret).getType().equals(type)) {
			return (ValueChunk<T>)ret;
		}
		else return null;
	}
	
	default <T> Optional<ValueChunk<T>> getOpt(Class<T> type, String name) {
		DataChunk ret=get(name);
		if(ret instanceof ValueChunk && ((ValueChunk<?>)ret).getType().equals(type)) {
			return Optional.of((ValueChunk<T>)ret);
		}
		else return Optional.empty();
	}
	
	default <T> T getValue(Class<T> type, String name) {
		ValueChunk<T> vc=get(type, name);
		if(vc==null)return null;
		else return vc.getValue();
	}
	
	default ObjectChunk getObject(String name) {
		DataChunk ret=get(name);
		if(ret instanceof ObjectChunk) {
			return (ObjectChunk)ret;
		}
		else return null;
	}
	
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
	
	default List<ValueChunk<?>> flatten(String nameprefix, List<ValueChunk<?>> values) {
		for(DataChunk d : this) {
			try {
				if(d instanceof ValueChunk)values.add((ValueChunk<?>)d.clone());
				else if(d instanceof ObjectChunk)((ObjectChunk) d).flatten(nameprefix+d.getName()+".", values);
			}catch (CloneNotSupportedException e) { }
		}
		return values;
	}
	
	@Override ObjectChunk clone() throws CloneNotSupportedException;
	
	public static SamCollector<DataChunk, ObjectChunk, ObjectChunk> collector(String name){
		return new SamCollector<DataChunk, ObjectChunk, ObjectChunk>() {
			private ObjectChunk obj=new SimpleObjectChunk(name);
			@Override public int countIngredient() { return obj.size(); }
			@Override public ObjectChunk getResult() { return obj; }
			@Override public ObjectChunk getState() { return obj; }
			@Override public void give(DataChunk ingredient) { obj.add(ingredient); }
		};
	}
	
}
