package jempasam.data.chunk;

public interface DataChunk extends Cloneable {
	
	String getName();
	void setName(String name);
	
	DataChunk clone() throws CloneNotSupportedException;
}
