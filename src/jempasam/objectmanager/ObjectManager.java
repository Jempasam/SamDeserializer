package jempasam.objectmanager;

import java.util.Map.Entry;
import java.util.function.Supplier;

import jempasam.samstream.Streamable;

public interface ObjectManager<T> extends Streamable<Entry<String,T>> {
	T get(String name);
	String idOf(T name);
	T register(String name, T obj);
	int size();
	
	default <Y extends T> Y getOrDefault(Class<Y> type, String name, Supplier<Y> def) {
		T ret=get(name);
		if(ret!=null && type.isInstance(ret)) return type.cast(ret);
		else return def.get();
	}
}
