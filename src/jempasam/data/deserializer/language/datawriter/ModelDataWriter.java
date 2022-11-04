package jempasam.data.deserializer.language.datawriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.ValueChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class ModelDataWriter implements DataWriter {
	
	
	
	private ObjectChunk model;
	private List<ValueChunk<String>> tosave;
	private List<ValueChunk<String>> tosave2;
	
	
	
	@LoadableParameter(name = "model")
	public ModelDataWriter(ObjectChunk model) {
		super();
		this.model=model;
		this.tosave=new ArrayList<>();
		this.tosave2=new ArrayList<>();
		model.recursiveStream().valuesOfType(String.class).filter(sc->sc.getValue().equals("TOKEN")).forEach(sc->tosave.add(sc));
		model.recursiveStream().valuesOfType(String.class).filter(sc->sc.getName().equals("TOKEN")).forEach(sc->tosave2.add(sc));
	}
	
	
	
	public void register(String token, List<ObjectChunk> objectStack) {
		ObjectChunk oldtarget=objectStack.get(objectStack.size()-1);
		tosave.stream().forEach(sc->sc.setValue(token));
		tosave2.stream().forEach(sc->sc.setName(token));
		try {
			ObjectChunk newtarget=model.clone();
			for(DataChunk dc : newtarget.childStream())oldtarget.add(dc);
			ObjectChunk maxtarget=newtarget;
			while(true) {
				Optional<ObjectChunk> opt=maxtarget.childStream().objects().last();
				if(opt.isPresent())maxtarget=opt.get();
				else break;
			}
			if(maxtarget!=newtarget)objectStack.add(maxtarget);
		}catch (CloneNotSupportedException e) { }
	}
	
	@Override
	public String toString() {
		tosave.stream().forEach(sc->sc.setValue("TOKEN"));
		tosave2.stream().forEach(sc->sc.setValue("TOKEN"));
		return "("+model+" )";
	}
}
