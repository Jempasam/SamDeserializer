package jempasam.data.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.logger.SLogger;
import jempasam.map.MultiMap;

public class ExtracterDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String open;
	private String close;
	private MultiMap<String, DataChunk> variables;
	
	
	
	public ExtracterDataModifier(SLogger logger, String open, String close) {
		super();
		this.logger = logger;
		this.open = open;
		this.close = close;
		variables=new MultiMap<>(new HashMap<>(), ArrayList::new);
	}
	
	public ExtracterDataModifier(SLogger logger) {
		this(logger, "<", ">");
	}
	
	
	
	public MultiMap<String, DataChunk> variables(){
		return variables;
	}
	
	@Override
	public void applyOn(ObjectChunk data) {
		List<Runnable> todo=new ArrayList<>();
		RecursiveChunkStream stream=data.recursiveStream();
		stream.forEach(chunk->{
			if(chunk.getName().startsWith(open)&&chunk.getName().endsWith(close)) {
				String name=chunk.getName().substring(open.length(), chunk.getName().length()-close.length());
				ObjectChunk parent=stream.actualParent();
				todo.add(()->{
					variables.add(name,chunk);
					parent.remove(chunk);
				});
			}
		});
		todo.forEach(Runnable::run);
	}
}
