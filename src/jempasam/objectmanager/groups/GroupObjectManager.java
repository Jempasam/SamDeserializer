package jempasam.objectmanager.groups;

import java.util.Collection;
import java.util.Map;

import jempasam.objectmanager.ObjectManager;
import jempasam.samstream.stream.SamStream;

public interface GroupObjectManager<T> extends ObjectManager<T>{
	SamStream<T> ofCategories(String name);
	SamStream<String> getCategories(T object);
	T register(String name, T obj, String categoryName);
	SamStream<Map.Entry<String, Collection<T>>> categories();
	SamStream<String> categoriesNames();
	
	void addToCategory(T obj, String categoryName);
	void removeFromCategory(T obj, String categoryName);
}
