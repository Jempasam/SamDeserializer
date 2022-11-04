package jempasam.data.deserializer.language.datawriter;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;

public interface DataWriter {
	void register(String token, List<ObjectChunk> objectStack);
	
	public static final DataWriter IGNORE=new DataWriter() {
		@Override
		public void register(String token, List<ObjectChunk> objectStack) {
		}
		public String toString() {return "IGNORE";}
	};
	
	public static final DataWriter EXIT=new DataWriter() {
		@Override
		public void register(String token, List<ObjectChunk> objectStack) {
			objectStack.remove(objectStack.size()-1);
		}
		public String toString() {return "EXIT";}
	};
	
	public static final DataWriter EXIT2=new DataWriter() {
		@Override
		public void register(String token, List<ObjectChunk> objectStack) {
			objectStack.remove(objectStack.size()-1);
			objectStack.remove(objectStack.size()-1);
		}
		public String toString() {return "EXIT2";}
	};
	
	public static final DataWriter EXIT3=new DataWriter() {
		@Override
		public void register(String token, List<ObjectChunk> objectStack) {
			objectStack.remove(objectStack.size()-1);
			objectStack.remove(objectStack.size()-1);
			objectStack.remove(objectStack.size()-1);
		}
		public String toString() {return "EXIT3";}
	};
	
	public static DataWriter member(String name) {
		return new MemberDataWriter(name);
	}
	
	public static DataWriter member(String name, String value) {
		return new FixedMemberDataWriter(name, value);
	}
	
	public static DataWriter open(String name, String memberName) {
		return new OpenAndMemberDataWriter(name, memberName);
	}
	
	public static DataWriter open(String name) {
		return new OpenDataWriter(name);
	}
	
	public static DataWriter composite(DataWriter ...writers) {
		return new CompositeDataWriter(writers);
	}
	
	public static Class<?> defaultSubclass(){
		return HybridDataWriter.class;
	}
}
