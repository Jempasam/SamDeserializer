package jempasam.data.deserializer.language.datawriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class HybridDataWriter implements DataWriter {
	
	private ObjectChunk root;
	private ObjectChunk target;
	private List<StringChunk> tosave;
	
	
	@LoadableParameter
	public HybridDataWriter() {
		super();
		this.root=new SimpleObjectChunk(null);
		this.target=this.root;
		this.tosave = new ArrayList<>();
	}
	
	
	
	@LoadableParameter(name = "open")
	public void open(String name) {
		ObjectChunk obj=new SimpleObjectChunk(name);
		target.add(obj);
		target=obj;
	}
	
	@LoadableParameter(name = "as")
	public void as(String name) {
		StringChunk chunk=new StringChunk(name,"TOKEN");
		tosave.add(chunk);
		target.add(chunk);
	}
	
	@LoadableParameter(name = "value")
	public void value(String value) {
		tosave.get(tosave.size()-1).setValue(value);
		tosave.remove(tosave.size()-1);
	}
	
	public void register(String token, List<ObjectChunk> objectStack) {
		ObjectChunk oldtarget=objectStack.get(objectStack.size()-1);
		tosave.stream().forEach(sc->sc.setValue(token));
		try {
			for(DataChunk dc : root.childStream()) oldtarget.add(dc.clone());
		}catch (CloneNotSupportedException e) { }
		if(target!=root) {
			ObjectChunk newtarget=oldtarget;
			while(true) {
				Optional<ObjectChunk> opt=newtarget.childStream().objects().last();
				if(opt.isPresent())newtarget=opt.get();
				else break;
			}
			objectStack.add(newtarget);
		}
		tosave.stream().forEach(sc->sc.setValue("TOKEN"));
	}
	
	@Override
	public String toString() {
		return "("+root+" )";
	}
}
