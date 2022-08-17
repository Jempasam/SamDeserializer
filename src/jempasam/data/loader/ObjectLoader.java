package jempasam.data.loader;

import jempasam.data.chunk.ObjectChunk;
import jempasam.objectmanager.ObjectManager;

public interface ObjectLoader<T> {
	void load(ObjectManager<? super T> manager, ObjectChunk data);
}
