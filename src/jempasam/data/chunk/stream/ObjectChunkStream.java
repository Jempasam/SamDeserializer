package jempasam.data.chunk.stream;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;

public class ObjectChunkStream implements DataChunkStream<ObjectChunk> {
	
	
	
	private DataChunkStream<? extends DataChunk> decorator;
	
	
	
	public ObjectChunkStream(DataChunkStream<? extends DataChunk> dataChunkIterator) {
		super();
		this.decorator = dataChunkIterator;
	}
	
	
	
	@Override
	public ObjectChunk tryNext() {
		if(!decorator.hasSucceed())return null;
		DataChunk value;
		while(true) {
			value=decorator.tryNext();
			if(!decorator.hasSucceed())return null;
			if(value instanceof ObjectChunk)return (ObjectChunk)value;
		}
	}
	
	@Override
	public boolean hasSucceed() {
		return decorator.hasSucceed();
	}

	@Override
	public ObjectChunk actualParent() {
		return decorator.actualParent();
	}
	
	
}
