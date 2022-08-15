package jempasam.data.chunk.stream;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;


public class ChildChunkStream implements DataChunkStream<DataChunk>{
	
	
	
	private int index;
	private ObjectChunk target;
	private boolean succeed;
	
	
	
	public ChildChunkStream(ObjectChunk target) {
		super();
		this.target=target;
		reset();
	}
	
	@Override
	public DataChunk tryNext() {
		if(index<target.size()) {
			index++;
			return target.get(index-1);
		}
		else {
			succeed=false;
			return null;
		}
	}
	
	@Override
	public void reset() {
		index=0;
		this.succeed=true;
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	@Override
	public ObjectChunk actualParent() {
		return target;
	}
}
