package jempasam.data.chunk.stream;

import java.util.ArrayDeque;
import java.util.Deque;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;

public class RecursiveChunkStream implements DataChunkStream<DataChunk>{
	
	
	
	private Deque<Integer> posStack;
	private Deque<ObjectChunk> targStack;
	private ObjectChunk target;
	private ObjectChunk parent;
	
	
	
	public RecursiveChunkStream(ObjectChunk chunk) {
		super();
		target=chunk;
		posStack=new ArrayDeque<>();
		posStack.add(-1);
		targStack=new ArrayDeque<>();
		targStack.add(target);
	}
	
	
	
	@Override
	public DataChunk tryNext() {
		while(!posStack.isEmpty()) {
			int lastpos=posStack.getLast()+1;
			posStack.removeLast();
			posStack.addLast(lastpos);
			ObjectChunk lastelem=targStack.getLast();
			if(lastpos >= lastelem.size()) {
				posStack.removeLast();
				targStack.removeLast();
			}
			else {
				parent=targStack.getLast();
				DataChunk selected=parent.get(lastpos);
				if(selected instanceof ObjectChunk) {
					posStack.add(-1);
					targStack.add((ObjectChunk)selected);
				}
				return selected;
			}
		}
		return null;
	}
	
	@Override
	public void reset() {
		posStack.clear();
		posStack.add(-1);
		targStack.clear();
		targStack.add(target);
	}
	
	@Override
	public boolean hasSucceed() {
		return !posStack.isEmpty();
	}
	
	public ObjectChunk[] actualPath() {
		return targStack.toArray(size->new ObjectChunk[size]);
	}
	
	public ObjectChunk actualParent() {
		return parent;
	}
}
