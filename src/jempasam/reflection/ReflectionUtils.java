package jempasam.reflection;

import java.awt.Container;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtils {
	
	public static Collection<Method> getAllMethods(Class<?> clazz){
		List<Method> ret=new ArrayList<>();
		while(clazz!=null) {
			Collections.addAll(ret, clazz.getDeclaredMethods());
			clazz=clazz.getSuperclass();
		}
		return ret;
	}
	
	public static Collection<Field> getAllFields(Class<?> clazz){
		List<Field> ret=new ArrayList<>();
		while(clazz!=null) {
			Collections.addAll(ret, clazz.getDeclaredFields());
			clazz=clazz.getSuperclass();
		}
		return ret;
	}
	
	public static Method getMethod(Class<?> clazz, String name, Class<?> ...args){
		if(clazz==null)return null;
		try {
			return clazz.getDeclaredMethod(name, args);
		} catch (SecurityException|NoSuchMethodException e) {
			return getMethod(clazz.getSuperclass(), name, args);
		}
	}
	
	public static Field getField(Class<?> clazz, String name){
		if(clazz==null)return null;
		try {
			return clazz.getDeclaredField(name);
		} catch (SecurityException|NoSuchFieldException e) {
			return getField(clazz.getSuperclass(), name);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getAllMethods(Container.class).stream().map(Method::getName).collect(Collectors.joining(" ;\n ")));
	}
}
