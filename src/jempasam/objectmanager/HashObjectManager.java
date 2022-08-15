package jempasam.objectmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;

public class HashObjectManager<T> implements ObjectManager<T>{
	
	
	
	Map<String, T> content;
	Map<T,String> reversed;
	
	
	
	public HashObjectManager() {
		content=new HashMap<>();
		reversed=new HashMap<>();
	}
	
	
	
	@Override
	public T get(String name) {return content.get(name);}
	
	@Override
	public String idOf(T name) { return reversed.get(name); }
	
	@Override
	public int size() { return content.size(); }
	
	@Override
	public Iterator<Entry<String, T>> iterator() {return content.entrySet().iterator();}
	
	@Override
	public SamStream<Entry<String, T>> stream() { return SamStreams.create(content.entrySet());}
	
	@Override
	public T register(String name, T obj) {
		T prev=content.get(name);
		if(prev!=null)reversed.remove(prev);
		
		content.put(name, obj);
		reversed.put(obj, name);
		return obj;
	}

}