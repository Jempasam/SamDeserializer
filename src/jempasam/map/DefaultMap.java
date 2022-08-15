package jempasam.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DefaultMap<K,V> implements Map<K,V>{
	
	
	
	private Map<K,V> internal;
	private Supplier<V> defaultFactory;
	
	
	
	public DefaultMap(Map<K, V> internal, Supplier<V> defaultFactory) {
		super();
		this.internal = internal;
		this.defaultFactory = defaultFactory;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		V value=internal.get(key);
		if(value==null) {
			value=defaultFactory.get();
			internal.put((K)key, value);
		}
		return value;
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
	public V put(K key, V value) {
		return internal.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return internal.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
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
	public Collection<V> values() {
		return internal.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return internal.entrySet();
	}
	
}
