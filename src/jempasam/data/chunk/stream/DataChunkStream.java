package jempasam.data.chunk.stream;

import java.util.HashMap;
import java.util.Map;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.samstream.stream.SamStream;

public interface DataChunkStream<T extends DataChunk> extends SamStream<T>{
	
	ObjectChunk actualParent();
	
	default void rename(String name) {
		forEach(datachunk->datachunk.setName(name));
	}
	
	default void rename(String oldname, String name) {
		forEach(datachunk->{
			if(datachunk.getName().equals(oldname))datachunk.setName(name);
		});
	}
	
	default ValueChunkStream<? extends Object> values() {
		return new ValueChunkStream<>(Object.class,this);
	}
	
	default <Y> ValueChunkStream<Y> valuesOfType(Class<Y> type){
		return new ValueChunkStream<>(type,this);
	}
	
	default ObjectChunkStream objects() {
		return new ObjectChunkStream(this);
	}
	
	default void numerateSameName(){
		Map<String, Integer> count=new HashMap<>();
		forEach(datachunk->{
			int number=count.getOrDefault(datachunk.getName(), 0);
			count.put(datachunk.getName(), number+1);
			
			if(number>0)datachunk.setName(datachunk.getName()+Integer.toString(number+1));
		});
	}
	
	default SamStream<ChildParentPair> mapToParent(){
		return map(dc->new ChildParentPair(this.actualParent(), dc));
	}
	
	
	
	public static class ChildParentPair{
		private ObjectChunk parent;
		private DataChunk child;
		public ChildParentPair(ObjectChunk parent, DataChunk child) {
			super();
			this.parent = parent;
			this.child = child;
		}
		public ObjectChunk getParent() {
			return parent;
		}
		public DataChunk getChild() {
			return child;
		}
		
	}
}
