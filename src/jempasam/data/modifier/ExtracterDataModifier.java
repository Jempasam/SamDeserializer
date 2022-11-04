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

public class ExtracterDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String open;
	private String close;
	private String separator;
	private MultiMap<String, DataChunk> variables;
	
	
	
	public ExtracterDataModifier(SLogger logger, String open, String close, String separator) {
		super();
		this.logger = logger;
		this.open = open;
		this.close = close;
		this.separator = separator;
		variables=new MultiMap<>(new HashMap<>(), ArrayList::new);
	}
	
	public ExtracterDataModifier(SLogger logger) {
		this(logger, "<", ">", ":");
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
				String splited[]=chunk.getName().substring(open.length(), chunk.getName().length()-close.length()).split(Pattern.quote(separator));
				ObjectChunk parent=stream.actualParent();
				todo.add(()->{
					variables.add(splited[0],chunk);
					if(splited.length>1)chunk.setName(splited[1]);
					parent.remove(chunk);
				});
			}
		});
		todo.forEach(Runnable::run);
	}
}
