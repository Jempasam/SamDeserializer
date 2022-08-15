package jempasam.converting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleValueParser implements ValueParser{
	
	private Map<Class<?>,Function<String,? extends Object>> parsers;
	
	public SimpleValueParser() {
		parsers=new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(Class<T> type, String string) {
		Function<String,? extends Object> parser=parsers.get(type);
		if(parser==null)return null;
		else return (T)parser.apply(string);
	}
	
	public <T> void add(Class<T> type, Function<String,T> serializer) {
		parsers.put(type, serializer);
	}

}
