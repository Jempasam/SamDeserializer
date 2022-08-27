package jempasam.data.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import jempasam.converting.ValueParser;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.chunk.value.ValueChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;
import jempasam.logger.SLogger;
import jempasam.objectmanager.ObjectManager;
import jempasam.reflection.ReflectionUtils;

public class SimpleObjectLoader<T> implements ObjectLoader<T>{
	
	
	
	private SLogger logger;
	private ValueParser valueParser;
	
	private Class<T> baseClass;
	private String prefixe;
	
	
	
	public SimpleObjectLoader(SLogger logger, ValueParser valueParser, Class<T> baseClass, String prefixe) {
		super();
		this.logger = logger;
		this.valueParser = valueParser;
		this.baseClass = baseClass;
		this.prefixe = prefixe;
	}
	
	
	
	@Override
	public void load(ObjectManager<? super T> manager, ObjectChunk data) {
		for(DataChunk d : data) {
			logger.enter().debug(d.getName());
			if(d instanceof ObjectChunk) {
				ObjectChunk od=(ObjectChunk)d;
				Set<String> excluded=new HashSet<>();
				Object o=manager.get(od.getName());
				if(o==null) {
					o=createObjectEmpty(od, baseClass, excluded);
					manager.register(od.getName(), (T)o);
				}
				if(o!=null) {
					hydrateObject(o, od, excluded);
					logger.info("RESULT: registred");
				}
				else logger.info("RESULT: Ignored");
			}
			logger.exit();
		}
	}
	
	private static class InsideException extends Exception{
		public InsideException(String message) { super(message); }
	}
	
	private interface ThrowingConsumer<T>{
		void accept(T t) throws Exception;
	}
	
	private interface ThrowingBiConsumer<T,Y>{
		void accept(T t, Y y) throws Exception;
	}
	
	private class ObjectParam{
		public Class<?> type=null;
		public ThrowingBiConsumer<Object,Object> setter=null;
	}
	
	private Object createObjectEmpty(ObjectChunk data, Class<?> rootclass, Set<String> excluded) {
		Class<?> objectclass=null;
		Object newobject=null;
		
		try {
			objectclass=getType(data, rootclass);
			newobject=createObjectEmptyOfClass(objectclass, data, excluded);
			excluded.add("type");
			if(newobject==null) throw new ClassNotFoundException();
			logger.debug("Instantiate as "+objectclass.getName());
			return newobject;
		}catch (ClassNotFoundException e) {
			logger.debug("Cannot instantiate");
			return null;
		}
	}
	
	private void hydrateObject(Object object, ObjectChunk data, Set<String> excluded) {
		logger.enter().debug("Hydrate");
		Class<?> objectclass=object.getClass();
		for(DataChunk d : data) {
			if(!excluded.contains(d.getName())) {
				logger.enter().debug("parameter \""+d.getName()+"\"");
				try {
					//Get parameter setter and type
					ObjectParam param=getParameter(objectclass, d.getName());
					if(param==null)throw new NoSuchMethodException();
					Object value=getValue(d,param.type);
					if(param==null)throw new InvalidParameterException();
					param.setter.accept(object, value);
					
				}catch(NoSuchMethodException e){
					logger.debug("This parameter does not exist");
				}catch (InvalidParameterException e) {
					logger.debug("Invalid parameter value.");
				}catch(Exception e) {
					logger.debug("Unexpexted error of type \""+e.getClass().getName()+"\"");
					e.printStackTrace();
				}
				logger.exit();
			}
		}
		
		//Init
		try {
			for(Method m : ReflectionUtils.getAllMethods(objectclass)) {
				if(m.getName().equals("init") && m.isAnnotationPresent(LoadableParameter.class) && m.getParameterCount()==0) {
					m.setAccessible(true);
						m.invoke(object);
					m.setAccessible(false);
				}
			}
		}catch(Exception e) {}
		logger.exit();
	}
	
	private Object createObject(ObjectChunk data, Class<?> rootclass) {
		logger.enter().debug("Object "+data.getName());
		Set<String> excluded=new HashSet<>();
		Object object=createObjectEmpty(data, rootclass, excluded);
		if(object!=null) {
			hydrateObject(object, data, excluded);
		}
		logger.exit();
		return object;
	}
	
	private Object createObjectEmptyOfClass(Class<?> clazz, ObjectChunk data, Set<String> used){
		for(Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if(constructor.isAnnotationPresent(LoadableParameter.class)) {
				String[] names=constructor.getAnnotation(LoadableParameter.class).paramnames();
				if(constructor.getParameterCount()!=names.length)logger.error("Bad parameter name count of constructor of "+clazz.getSimpleName());
				Object args[]=new Object[names.length];
				used.clear();
				for(int i=0; i<names.length; i++) {
					DataChunk dataparam=data.get(names[i]);
					if(dataparam==null)break;
					Object value=getValue(dataparam, constructor.getParameters()[i].getType());
					if(value==null)break;
					args[i]=value;
					used.add(names[i]);
				}
				try {
					constructor.setAccessible(true);
					Object newobject=constructor.newInstance(args);
					constructor.setAccessible(false);
					return newobject;
				}catch (InstantiationError | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.error("Instantiation error "+e.getClass().getSimpleName());
					return null;
				}
			}
		}
		logger.error("Miss parameter for constructor of constructor not found. The object cannot be instantiated.");
		return null;
	}
	
	private Object getValue(DataChunk datachunk, Class<?> type){
		Object ret=null;
		logger.enter().debug("Value "+datachunk+":");
		if(datachunk instanceof ValueChunk<?>) {
			ValueChunk<?> valuechunk=(StringChunk)datachunk;
			
			//Try to parse
			Object value=valuechunk.getValue();
			Object parsed=valueParser.parse(type, value);
			if(parsed!=null) {
				logger.info("Parameter registred as \""+valuechunk.getName()+"\"=\""+valuechunk.getValue()+"\"");
				ret=parsed;
			}
			else logger.info("Parameter \""+valuechunk.getName()+"\" is of unparseable type. Replace string value by an object value.");
		}
		else if(datachunk instanceof ObjectChunk){
			ObjectChunk objectchunk=(ObjectChunk)datachunk;
			
			//If is object
			Object parsed=valueParser.parse(type, objectchunk);
			if(parsed!=null) {
				ret=parsed;
			}
			else if(type.isPrimitive() || type==String.class) logger.info("This parameter should be primitive not an object. Replace object value by string value.");
			else {
				Object obj=createObject(objectchunk, type);
				if(obj==null) logger.info("Invalid object parameter");
				else ret=obj;
			}
		}
		else logger.info("A parameter should be of type ObjectChunk or ValueChunk.");
		logger.exit();
		return ret;
	}
	
	private ObjectParam getParameter(Class<?> type, String name) {
		ObjectParam ret=new ObjectParam();
		//Get field
		for(Field f : ReflectionUtils.getAllFields(type)) {
			if(f.isAnnotationPresent(LoadableParameter.class) && (name.equals(f.getAnnotation(LoadableParameter.class).name()) || name.equals(f.getName())) ){
				ret.setter=(o,v)->{
					f.setAccessible(true);
					f.set(o, v);
					f.setAccessible(false);
				};
				ret.type=f.getType();
				return ret;
			}
		}
		
		//Get method
		for(Method m : ReflectionUtils.getAllMethods(type)) {
			if(		m.isAnnotationPresent(LoadableParameter.class) &&
					(name.equals(m.getAnnotation(LoadableParameter.class).name()) || name.equals(m.getName())) &&
					m.getParameterCount()==1
					){
				ret.setter=(o,v)->{
					m.setAccessible(true);
					m.invoke(o,v);
					m.setAccessible(false);
				};
				ret.type=m.getParameterTypes()[0];
				return ret;
			}
		}
		return null;
	}
	
	private Class<?> getType(ObjectChunk data, Class<?> rootclass) throws ClassNotFoundException{
		ClassLoader loader=getClass().getClassLoader();
		Class<?> type=null;
		
		//Get the object class name
		DataChunk classchunk=data.get("type");
		
		if(!(classchunk instanceof StringChunk)) {
			//Without no type precised
			try {
				//Check if rootclass have default class defined
				Method defaultclass=ReflectionUtils.getMethod(rootclass, "defaultSubclass");
				if(defaultclass==null || !Modifier.isStatic(defaultclass.getModifiers())) throw new Exception();
				defaultclass.setAccessible(true);
				type=(Class<?>)defaultclass.invoke(null);
				defaultclass.setAccessible(false);
			}catch(Exception e) {
				//Check if root type is a valid type
				type=rootclass;
				if(!type.isAnnotationPresent(Loadable.class))new ClassNotFoundException("Miss the parameter \"type\".");
			}
		}
		else {
			//With type precised
			String classname=((StringChunk)classchunk).getValue();

			//Get the type by name
			try {
				type=loader.loadClass(prefixe+classname);
			}catch(ClassNotFoundException e) {
				try {
					type=loader.loadClass(classname);
				}catch(ClassNotFoundException ee) {
					type=null;
				}
			}

			//Check if type exist
			if(type==null || !type.isAnnotationPresent(Loadable.class))
				throw new ClassNotFoundException("Class \""+prefixe+classname+"\" or \""+classname+"\" does not exist.");
		}
		
		//Check if type is child of roottype
		if(!rootclass.isAssignableFrom(type))
			throw new ClassNotFoundException("The type \""+type.getName()+"\" cannot be used here.");
		
		return type;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
