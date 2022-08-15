package jempasam.data.container;

import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.deserializer.AfterLoadDataDeserializer;
import jempasam.data.deserializer.DataDeserializer;
import jempasam.data.deserializer.ModifiersDataDeserializer;
import jempasam.data.loader.ObjectLoader;
import jempasam.data.modifier.DataModifier;
import jempasam.logger.SLogger;
import jempasam.objectmanager.ObjectManager;

public class CompositeObjectDeserializer<T>{
	
	
	
	private DataDeserializer deserializer=null;
	private ObjectLoader<T> loader=null;
	private ObjectManager<T> manager=null;
	private SLogger logger=null;
	
	
	
	public CompositeObjectDeserializer(SLogger logger, ObjectManager<T> manager) {
		this.logger=logger;
		this.manager=manager;
	}
	
	
	
	public SLogger logger() {
		return logger;
	}
	public ObjectManager<T> manager(){
		return manager;
	}
	
	public DataDeserializer deserializer() {
		return deserializer;
	}
	
	public DataDeserializer deserializer(DataDeserializer newdes) {
		deserializer=newdes;
		return deserializer;
	}
	
	public DataModifier modifier(DataModifier modifier) {
		deserializer=new ModifiersDataDeserializer(deserializer, Arrays.asList(modifier));
		return modifier;
	}
	
	public Consumer<ObjectChunk> action(Consumer<ObjectChunk> action) {
		AfterLoadDataDeserializer afterload=new AfterLoadDataDeserializer(deserializer);
		afterload.register(action);
		return action;
	}
	
	public ObjectLoader<T> loader() {
		return loader;
	}
	
	public ObjectLoader<T> loader(ObjectLoader<T> newobj) {
		loader=newobj;
		return loader;
	}
	
	public void loadFrom(InputStream input) {
		ObjectChunk data=deserializer.loadFrom(input);
		loader.load(manager, data);
	}
	
}
