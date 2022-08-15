package jempasam.converting;

import java.util.function.Function;

public class ValueParsers {
	public static SimpleValueParser createSimpleValueParser() {
		SimpleValueParser ret=new SimpleValueParser();
		
		ret.add(Boolean.TYPE, Boolean::parseBoolean);
		
		ret.add(Character.TYPE,	(s)->s.charAt(0));
		
		ret.add(Byte.TYPE, (str)->parseNum(str,Byte::parseByte));
		ret.add(Short.TYPE, (str)->parseNum(str,Short::parseShort));
		ret.add(Integer.TYPE, (str)->parseNum(str,Integer::parseInt));
		ret.add(Long.TYPE, (str)->parseNum(str,Long::parseLong));
		
		ret.add(Float.TYPE, Float::parseFloat);
		ret.add(Double.TYPE, Double::parseDouble);
		
		ret.add(String.class, Function.identity());
		return ret;
	}
	private static <T extends Number> T parseNum(String str, Function<String,T> parser) {
		if(str.length()>2 && str.startsWith("0x")) {
			str=str.substring(2);
		}
		return parser.apply(str);
	}
}
