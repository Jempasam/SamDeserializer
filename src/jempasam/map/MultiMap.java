package jempasam.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MultiMap<K,V> implements Map<K,Collection<V>>{
	
	
	
	private Map<K,Collection<V>> internal;
	private Supplier<Collection<V>> listFactory;
	
	
	
	public MultiMap(Map<K, Collection<V>> internal, Supplier<Collection<V>> listFactory) {
		super();
		this.internal = internal;
		this.listFactory = listFactory;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Collection<V> get(Object key) {
		Collection<V> list=internal.get(key);
		if(list==null) {
			list=listFactory.get();
			internal.put((K)key, list);
		}
		return list;
	}
	
	public void add(K key, V value) {
		get(key).add(value);
	}
	
	public void count(K key) {
		get(key).size();
	}
	
	public void removeOne(K key, V value) {
		get(key).remove(value);
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internal.containsKey(value);
	}

	@Override
	public Collection<V> put(K key, Collection<V> value) {
		return internal.put(key, value);
	}

	@Override
	public Collection<V> remove(Object key) {
		return internal.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Collection<V>> m) {
		internal.putAll(m);
	}

	@Override
	public void clear() {
		internal.clear();
	}

	@Override
	public Set<K> keySet() {
		return internal.keySet();
	}

	@Override
	public Collection<Collection<V>> values() {
		return internal.values();
	}

	@Override
	public Set<Entry<K, Collection<V>>> entrySet() {
		return internal.entrySet();
	}
	
	@Override
	public String toString() {
		return internal.toString();
	}
	
}
