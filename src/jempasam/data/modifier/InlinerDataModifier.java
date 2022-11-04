package jempasam.data.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.logger.SLogger;
import jempasam.map.MultiMap;

public class InlinerDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String name;
	
	
	
	public InlinerDataModifier(SLogger logger, String name) {
		super();
		this.logger = logger;
		this.name = name;
	}
	
	public InlinerDataModifier(SLogger logger) {
		this(logger, "inline");
	}
	
	
	
	@Override
	public void applyOn(ObjectChunk data) {
		List<Runnable> todo=new ArrayList<>();
		data.childStream().mapToParent().forEach(pair->{
			todo.add(()->{
				if(pair.getChild() instanceof ObjectChunk) {
					applyOn((ObjectChunk)pair.getChild());
					if(pair.getChild().getName().equals(name)) {
						int place=pair.getParent().find(pair.getChild());
						pair.getParent().remove(place);
						for(DataChunk v : ((ObjectChunk)pair.getChild())) {
							pair.getParent().add(place, v);
							place++;
						}
					}
				}
			});
		});
		todo.forEach(Runnable::run);
	}
}
