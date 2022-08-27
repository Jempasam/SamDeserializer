package jempasam.converting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleValueParser implements ValueParser{
	
	private Map<Class<?>, Map<Class<?>, Function<? extends Object, ? extends Object>>> parsers;
	
	public SimpleValueParser() {
		parsers=new HashMap<>();
	}

	
	@SuppressWarnings("unchecked")
	public <F,T> T parse(Class<T> to, F converted) {
		Map<Class<?>, Function<? extends Object, ? extends Object>> fromToConverterMap=parsers.get(to);
		if(fromToConverterMap!=null) {
			for(Map.Entry<Class<? extends Object>, Function<? extends Object, ? extends Object>> entry : fromToConverterMap.entrySet()) {
				if(entry.getKey().isAssignableFrom(converted.getClass())) {
					return ((Function<F,T>)entry.getValue()).apply(converted);
				}
			}
		}
		return null;
	}
	
	public <F,T> void add(Class<F> from, Class<T> to, Function<F,T> serializer) {
		Map<Class<?>, Function<? extends Object, ? extends Object>> fromToConverterMap=parsers.get(to);
		if(fromToConverterMap==null) {
			fromToConverterMap=new HashMap<>();
			parsers.put(to, fromToConverterMap);
		}
		
		fromToConverterMap.put(from, serializer);
	}

}
