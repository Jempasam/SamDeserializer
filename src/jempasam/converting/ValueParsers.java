package jempasam.converting;

import java.util.function.Function;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;

public class ValueParsers {
	public static SimpleValueParser createSimpleValueParser() {
		SimpleValueParser ret=new SimpleValueParser();
		
		ret.add(String.class, Boolean.TYPE, Boolean::parseBoolean);
		
		ret.add(String.class, Character.TYPE,	(s)->s.charAt(0));
		
		ret.add(String.class, Byte.TYPE, (str)->parseNum(str,Byte::parseByte));
		ret.add(String.class, Short.TYPE, (str)->parseNum(str,Short::parseShort));
		ret.add(String.class, Integer.TYPE, (str)->parseNum(str,Integer::parseInt));
		ret.add(String.class, Long.TYPE, (str)->parseNum(str,Long::parseLong));
		
		ret.add(String.class, Float.TYPE, Float::parseFloat);
		ret.add(String.class, Double.TYPE, Double::parseDouble);
		
		ret.add(String.class, String.class, Function.identity());
		
		ret.add(ObjectChunk.class, ObjectChunk.class, oc->{
			try {
				return oc.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		});
		ret.add(ObjectChunk.class, StringChunk.class, objectchunk->{
			if(objectchunk.size()==1 && objectchunk.get(0) instanceof StringChunk) {
				return (StringChunk)objectchunk.get(0);
			}
			else return null;
		});
		return ret;
	}
	private static <T extends Number> T parseNum(String str, Function<String,T> parser) {
		if(str.length()>2 && str.startsWith("0x")) {
			str=str.substring(2);
		}
		return parser.apply(str);
	}
}
