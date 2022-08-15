package jempasam.data.deserializer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jempasam.data.chunk.ObjectChunk;

public interface DataDeserializer {
	ObjectChunk loadFrom(InputStream i);
	default ObjectChunk loadFrom(String str) {
		return loadFrom(new ByteArrayInputStream(str.getBytes()));
	}
}
