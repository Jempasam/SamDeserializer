package jempasam.data.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;

import jempasam.converting.ValueParser;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;
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
	public void load(ObjectManager<T> manager, ObjectChunk data) {
		for(DataChunk d : data) {
			logger.enter(data.getName());
			if(d instanceof ObjectChunk) {
				Object o=createObject((ObjectChunk)d, baseClass);
				if(o!=null) {
					manager.register(d.getName(), (T)o);
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
	
	private Object createObject(ObjectChunk data, Class<?> rootclass) {
		
		String classname="";
		Class<?> objectclass=null;
		Object newobject=null;
		
		logger.enter(data.getName());
		try {
			//Get the object type
			objectclass=getType(data, rootclass);
			
			//Instantiate the object
			Constructor<?> constructor=objectclass.getDeclaredConstructor();
			constructor.setAccessible(true);
			newobject=constructor.newInstance();
			constructor.setAccessible(false);
			
			//Load parameters
			for(DataChunk d : data) {
				if(!d.getName().equals("type")) {
					logger.enter("parameter \""+d.getName()+"\"");
					try {
						//Get parameter setter and type
						ObjectParam param=getParameter(objectclass, d.getName());
						if(param==null)throw new NoSuchMethodException();
						Object value=getValue(d,param.type);
						if(param==null)throw new InvalidParameterException();
						param.setter.accept(newobject, value);
						
					}catch(NoSuchMethodException e){
						logger.info("This parameter does not exist");
					}catch (InvalidParameterException e) {
						logger.info("Invalid parameter value.");
					}catch(Exception e) {
						logger.info("Unexpexted error of type \""+e.getClass().getName()+"\"");
						e.printStackTrace();
					}
					logger.exit();
				}
			}
		} catch (ClassNotFoundException e) {
			logger.info(e.getMessage());
		} catch(NoSuchMethodException | InstantiationException e) {
			logger.info("The type \""+classname+"\" is not instantiable");
		} catch (Exception e) {
			logger.info("Unexpexted error of type \""+e.getClass().getName()+"\"");
			e.printStackTrace();
		}
		
		logger.exit();
		
		//Init
		try {
			for(Method m : ReflectionUtils.getAllMethods(objectclass)) {
				if(m.getName().equals("init") && m.isAnnotationPresent(LoadableParameter.class) && m.getParameterCount()==0) {
					m.setAccessible(true);
						m.invoke(newobject);
					m.setAccessible(false);
				}
			}
		}catch(Exception e) {}
		
		return newobject;
	}
	
	private Object getValue(DataChunk datachunk, Class<?> type){
		Object ret=null;
		logger.enter("Value "+datachunk+":");
		if(datachunk instanceof StringChunk) {
			StringChunk oc=(StringChunk)datachunk;
			
			//Try to parse
			Object parsed=valueParser.parse(type, oc.getValue());
			if(parsed!=null) {
				logger.info("Parameter registred as \""+oc.getName()+"\"=\""+oc.getValue()+"\"");
				ret=parsed;
			}
			else logger.info("Parameter \""+oc.getName()+"\" is of unparseable type. Replace string value by an object value.");
		}
		else if(datachunk instanceof ObjectChunk){
			ObjectChunk oc=(ObjectChunk)datachunk;
			
			//If is object
			if(type.isPrimitive() || type==String.class) logger.info("This parameter should be primitive not an object. Replace object value by string value.");
			else {
				Object obj=createObject(oc, type);
				if(obj==null) logger.info("Invalid object parameter");
				else ret=obj;
			}
		}
		else logger.info("A parameter should be of type Object or String.");
		logger.exit();;
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
				try {
					if(!type.isAnnotationPresent(Loadable.class))
						throw new NoSuchMethodException();
					type.getDeclaredConstructor();
				}catch(NoSuchMethodException ee) {
					throw new ClassNotFoundException("Miss the parameter \"type\".");
				}
			}
		}
		else {
			//With type precised
			String classname=((StringChunk)classchunk).getValue();
			
			//Get the type by name
			try {
				type=loader.loadClass(prefixe+classname);
			}catch(ClassNotFoundException e) {
				type=loader.loadClass(classname);
			}
			
			//Check if type exist
			if(type==null || !type.isAnnotationPresent(Loadable.class))
				throw new ClassNotFoundException("Class \""+prefixe+classname+"\" or \""+classname+"\" does not exist.");
		}
		//Check if type is valid (instantiable)
		try {
			type.getDeclaredConstructor();
		}catch(NoSuchMethodException e) {
			throw new ClassNotFoundException("Class \""+type.getName()+"\" is malformed. Ask the software developper.");
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
