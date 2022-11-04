package jempasam.objectmanager.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import jempasam.map.MultiMap;
import jempasam.objectmanager.ObjectManager;
import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;

public class DoubleGroupObjectManager<T> implements GroupObjectManager<T>{
	
	
	
	private ObjectManager<T> objectManager;
	private MultiMap<String, T> groupToObject;
	private MultiMap<T, String> objectToGroup;
	
	
	
	public DoubleGroupObjectManager(ObjectManager<T> objectManager) {
		super();
		this.objectManager = objectManager;
		groupToObject=new MultiMap<>(new HashMap<>(), HashSet::new);
		objectToGroup=new MultiMap<>(new HashMap<>(), HashSet::new);
	}
	
	
	
	@Override
	public T get(String name) {
		return objectManager.get(name);
	}
	
	@Override
	public String idOf(T name) {
		return objectManager.idOf(name);
	}
	
	@Override
	public T register(String name, T obj) {
		return objectManager.register(name, obj);
	}
	
	@Override
	public T register(String name, T obj, String categoryName) {
		groupToObject.add(categoryName, obj);
		objectToGroup.add(obj, categoryName);
		return objectManager.register(name, obj);
	}
	
	@Override
	public SamStream<String> getCategories(T object) {
		return SamStreams.create(objectToGroup.get(object));
	}
	
	@Override
	public SamStream<T> ofCategories(String name) {
		return SamStreams.create(groupToObject.get(name));
	}
	
	@Override
	public int size() {
		return objectManager.size();
	}
	
	@Override
	public SamStream<Entry<String, T>> stream() {
		return objectManager.stream();
	}
	
	@Override
	public SamStream<Entry<String, Collection<T>>> categories() {
		return SamStreams.create(groupToObject.entrySet());
	}
	
	@Override
	public SamStream<String> categoriesNames() {
		return SamStreams.create(groupToObject.keySet());
	}
	
	@Override
	public void addToCategory(T obj, String categoryName) {
		if(objectManager.idOf(obj)!=null) {
			groupToObject.add(categoryName, obj);
			objectToGroup.add(obj, categoryName);
		}
	}
	
	@Override
	public void removeFromCategory(T obj, String categoryName) {
		Collection<String> groups=objectToGroup.remove(categoryName);
		for(String str : groups)groupToObject.removeOne(str, obj);
	}
}
