package jempasam.data.serializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.ValueChunk;

public class JsonDataSerializer implements DataSerializer{
	
	
	
	private BufferedWriter writer;
	
	
	
	public JsonDataSerializer() {
	}
	
	
	
	@Override
	public void write(OutputStream output, ObjectChunk data) {
		this.writer=new BufferedWriter(new OutputStreamWriter(output));
		try {
			writeDataChunk(data, 0);
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void writeLineStart(int offset) throws IOException{
		writer.append("\n");
		for(int i=0; i<offset; i++)writer.append("   ");
	}
	
	public void writeDataChunk(DataChunk data, int offset) throws IOException {
		writeLineStart(offset);
		writer.append("\"").append(data.getName()).append("\": ");
		if(data instanceof ObjectChunk) {
			writeObjectChunk((ObjectChunk)data, offset);
		}
		else if(data instanceof ValueChunk<?>) {
			writeValueChunk((ValueChunk<?>)data, offset);
		}
	}
	
	public void writeObjectChunk(ObjectChunk data, int offset) throws IOException {
		writer.append("{");
		int size=data.size()-1;
		for(int i=0; i<size; i++) {
			writeDataChunk(data.get(i), offset+1);
			writer.append(",");
		}
		if(size>-1)writeDataChunk(data.get(size), offset+1);
		writeLineStart(offset);
		writer.append("}");
	}
	
	public void writeValueChunk(ValueChunk<?> data, int offset) throws IOException {
		writer.append("\"").append(data.getValue().toString()).append("\"");
	}
	
}
