package jempasam.converting;

import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

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
	
	public static SimpleValueParser createCompleteValueParser() {
		Random rd=new Random();
		SimpleValueParser ret=createSimpleValueParser();
		ret.add(String.class, IntUnaryOperator.class, (String str)->{
			if(str.startsWith("=")) {
				String nbstr=str.substring(1);
				if(nbstr.contains("..")) {
					String[] parts=nbstr.split("\\.\\.", 2);
					int aa=parseNum(parts[0], Integer::parseInt);
					int bb=parseNum(parts[1], Integer::parseInt)-aa;
					return new IntUnaryOperator() { public int applyAsInt(int a) { return aa+rd.nextInt(bb); } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					int nb=parseNum(nbstr, Integer::parseInt);
					return new IntUnaryOperator() { public int applyAsInt(int a) { return nb; } public String toString() { return ""+nb; } };
				}
			}
			else {
				if(str.contains("..")) {
					String[] parts=str.split("\\.\\.", 2);
					int aa=parseNum(parts[0], Integer::parseInt);
					int bb=parseNum(parts[1], Integer::parseInt)-aa;
					return new IntUnaryOperator() { public int applyAsInt(int a) { return a+aa+rd.nextInt(bb); } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					int nb=parseNum(str, Integer::parseInt);
					return new IntUnaryOperator() { public int applyAsInt(int a) { return a+nb; } public String toString() { return ""+nb; } };
				}
			}
		});
		ret.add(String.class, DoubleUnaryOperator.class, (String str)->{
			if(str.startsWith("=")) {
				String nbstr=str.substring(1);
				if(nbstr.contains("..")) {
					String[] parts=nbstr.split("\\.\\.", 2);
					double aa=parseNum(parts[0], Double::parseDouble);
					double bb=parseNum(parts[1], Double::parseDouble)-aa;
					return new DoubleUnaryOperator() { public double applyAsDouble(double a) { return aa+rd.nextDouble()*bb; } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					double nb=parseNum(nbstr, Double::parseDouble);
					return new DoubleUnaryOperator() { public double applyAsDouble(double a) { return nb; } public String toString() { return ""+nb; } };
				}
			}
			else {
				if(str.contains("..")) {
					String[] parts=str.split("\\.\\.", 2);
					double aa=parseNum(parts[0], Double::parseDouble);
					double bb=parseNum(parts[1], Double::parseDouble)-aa;
					return new DoubleUnaryOperator() { public double applyAsDouble(double a) { return a+aa+rd.nextDouble()*bb; } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					double nb=parseNum(str, Double::parseDouble);
					return new DoubleUnaryOperator() { public double applyAsDouble(double a) { return a+nb; } public String toString() { return ""+nb; } };
				}
			}
		});
		ret.add(String.class, LongUnaryOperator.class, (String str)->{
			if(str.startsWith("=")) {
				String nbstr=str.substring(1);
				if(nbstr.contains("..")) {
					String[] parts=nbstr.split("\\.\\.", 2);
					long aa=parseNum(parts[0], Long::parseLong);
					int bb=(int)(parseNum(parts[1], Long::parseLong)-aa);
					return new LongUnaryOperator() { public long applyAsLong(long a) { return aa+rd.nextInt(bb); } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					int nb=parseNum(nbstr, Integer::parseInt);
					return new LongUnaryOperator() { public long applyAsLong(long a) { return nb; } public String toString() { return ""+nb; } };
				}
			}
			else {
				if(str.contains("..")) {
					String[] parts=str.split("\\.\\.", 2);
					long aa=parseNum(parts[0], Long::parseLong);
					int bb=(int)(parseNum(parts[1], Long::parseLong)-aa);
					return new LongUnaryOperator() { public long applyAsLong(long a) { return a+aa+rd.nextInt(bb); } public String toString() { return aa+"-"+(aa+bb); } };
				}
				else {
					long nb=parseNum(str, Long::parseLong);
					return new LongUnaryOperator() { public long applyAsLong(long a) { return a+nb; } public String toString() { return ""+nb; } };
				}
			}
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
