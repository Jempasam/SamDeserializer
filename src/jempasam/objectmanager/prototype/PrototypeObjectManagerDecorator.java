package jempasam.objectmanager.prototype;

import java.util.Iterator;
import java.util.Map.Entry;

import jempasam.objectmanager.ObjectManager;
import jempasam.samstream.stream.SamStream;

public class PrototypeObjectManagerDecorator<Y,T extends Prototype<Y>> implements PrototypeManager<Y,T>{
	
	ObjectManager<T> manager;
	
	public PrototypeObjectManagerDecorator(ObjectManager<T> manager) {
		this.manager=manager;
	}
	
	@Override
	public T get(String name) {
		return manager.get(name);
	}
	
	@Override
	public String idOf(T name) {
		return manager.idOf(name);
	}
	
	@Override
	public int size() {
		return manager.size();
	}
	
	@Override
	public T register(String name, T obj) {
		return manager.register(name, obj);
	}
	
	@Override
	public SamStream<Entry<String, T>> stream() {
		return manager.stream();
	}
	
	@Override
	public Iterator<Entry<String, T>> iterator() {
		return manager.iterator();
	}

}
