package jempasam.data.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import jempasam.data.chunk.ObjectChunk;

public interface DataSerializer {
	
	void write(OutputStream output, ObjectChunk data);
	
	default String write(ObjectChunk data) {
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		write(output,data);
		return output.toString();
	}
}
