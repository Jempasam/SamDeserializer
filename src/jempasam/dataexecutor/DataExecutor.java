package jempasam.dataexecutor;

import jempasam.data.chunk.DataChunk;

public interface DataExecutor {
	Object execute(DataChunk dc);
}
