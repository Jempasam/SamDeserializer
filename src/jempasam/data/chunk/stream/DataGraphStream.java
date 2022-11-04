package jempasam.data.chunk.stream;

import java.util.List;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.ValueChunk;
import jempasam.samstream.SamStreams;
import jempasam.samstream.collectors.SamCollectors;
import jempasam.samstream.graph.Graph;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.tree.GraphTreeSamStream;
import jempasam.samstream.tree.path.TreePath;

public class DataGraphStream extends GraphTreeSamStream<DataChunk, Void>{
	
	
	
	public DataGraphStream(DataChunk data) {
		super(DATA_GRAPH,data);
	}
	
	
	
	private static Graph<DataChunk, Void> DATA_GRAPH=new Graph<DataChunk, Void>(){
		public SamStream<GraphLink<DataChunk, Void>> getLinks(DataChunk parent) {
			if(parent instanceof ObjectChunk) {
				return ((ObjectChunk)parent).childStream().map(child->new GraphLink<>(child, null));
			}
			else return SamStreams.empty();
		}
	};
	
	public List<ValueChunk<?>> flatten(List<ValueChunk<?>> values) {
		for(TreePath<DataChunk, Void> d : this.paths()) {
			try {
				ValueChunk<?> n=(ValueChunk<?>)d.getLastValue().clone();
				n.setName(d.values().map(DataChunk::getName).collect(SamCollectors.concatenate(".")));
				values.add(n);
			}catch (CloneNotSupportedException e) { }
		}
		return values;
	}
	
	
}
