package jempasam.objectmanager;

import java.util.Map.Entry;

import jempasam.samstream.Streamable;

public interface ObjectManager<T> extends Streamable<Entry<String,T>> {
	T get(String name);
	String idOf(T name);
	T register(String name, T obj);
	int size();
}
