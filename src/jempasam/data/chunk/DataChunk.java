package jempasam.data.chunk;

import jempasam.samstream.SamStreams;
import jempasam.samstream.graph.Graph;
import jempasam.samstream.graph.GraphSamStream;
import jempasam.samstream.stream.SamStream;

public interface DataChunk extends Cloneable {
	
	String getName();
	void setName(String name);
	
	DataChunk clone() throws CloneNotSupportedException;
	
	default GraphSamStream<DataChunk,Void> graphStream() {
		return new GraphSamStream<>(DATA_GRAPH, this);
	}
	
	public static Graph<DataChunk, Void> DATA_GRAPH=new Graph<DataChunk, Void>(){
		public SamStream<GraphLink<DataChunk, Void>> getLinks(DataChunk parent) {
			if(parent instanceof ObjectChunk) {
				return ((ObjectChunk)parent).childStream().map(child->new GraphLink<>(child, null));
			}
			else return SamStreams.empty();
		}
	};
	
}
