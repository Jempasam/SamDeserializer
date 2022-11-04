package jempasam.data.modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;
import jempasam.map.MultiMap;

public class GeneratorDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String open;
	private String close;
	private String separator;
	private String separator2;
	private Random random;
	
	
	
	public GeneratorDataModifier(SLogger logger, String open, String close, String separator, String separator2) {
		super();
		this.logger = logger;
		this.open = open;
		this.close = close;
		this.separator = separator;
		this.separator2 = separator2;
		this.random=new Random();
	}
	
	public GeneratorDataModifier(SLogger logger) {
		this(logger, "<", ">",";", ":");
	}
	
	
	private MultiMap<String, DataChunk> models;
	@Override
	public void applyOn(ObjectChunk data) {
		// Fetch model
		List<Runnable> todo=new ArrayList<>();
		models=new MultiMap<>(new HashMap<>(), ArrayList::new);
		data.recursiveStream().forEach((_stream,chunk)->{
			RecursiveChunkStream stream=(RecursiveChunkStream)_stream;
			if(chunk.getName().startsWith(open)&&chunk.getName().endsWith(close)) {
				String name=chunk.getName().substring(open.length(), chunk.getName().length()-close.length());
				ObjectChunk parent=stream.actualParent();
				todo.add(()->{
					models.add(name,chunk);
					parent.remove(chunk);
				});
			}
		});
		todo.forEach(Runnable::run);
		generateObjectChunk(data);
	}
	
	private DataChunk generateModel(String modelname) {
		List<DataChunk> chunks=(ArrayList<DataChunk>)models.get(modelname);
		if(chunks.size()==0) {
			logger.error("Invalid generator model name \""+modelname+"\"");
			return null;
		}
		else {
			DataChunk newchunk=null;
			try {
				newchunk = chunks.get(random.nextInt(chunks.size())).clone();
			} catch (CloneNotSupportedException e) { }
			return newchunk;
		}
	}
	
	private void generateObjectChunk(ObjectChunk chunk) {
		// Get choices
		List<Runnable> removelist=new ArrayList<>();
		MultiMap<String, Choice> choices=new MultiMap<String, Choice>(new HashMap<>(), ArrayList::new);
		RecursiveChunkStream stream=chunk.recursiveStream();
		stream.forEach(datachunk->{
			if(datachunk instanceof StringChunk) {
				StringChunk strchunk=(StringChunk)datachunk;
				if(strchunk.getValue().startsWith(open) && strchunk.getValue().endsWith(close)) {
					String parameters[]=strchunk.getValue().substring(open.length(),strchunk.getValue().length()-close.length()).split(separator);
					Choice choice=new Choice();
					choice.target=strchunk;
					choice.parent=stream.actualParent();
					removelist.add(()->choice.parent.remove(choice.target));
					for(String param : parameters) {
						String infos[]=param.split(separator2);
						if(infos.length==2 && infos[0].equals("id"))choice.id=infos[1];
						else if(infos.length==2 && infos[0].equals("name"))choice.name=infos[1];
						else if(infos.length==2 && infos[0].equals("remaining"))choice.remaining=Integer.parseInt(infos[1]);
						else if(infos.length==2 && infos[0].equals("repeat"))choice.repeat=Integer.parseInt(infos[1]);
						else if(infos.length==3 && infos[0].equals("repeat")) {
							int min=Integer.parseInt(infos[1]);
							int max=Integer.parseInt(infos[2]);
							choice.repeat=random.nextInt(max-min+1)+min;
						}
						else if(infos.length==2 && infos[0].equals("repeatable"))choice.repeatable=!infos[1].equals("false");
						else logger.warning("Invalid genpoint parameter "+Arrays.toString(infos));
					}
					choices.add(choice.id, choice);
				}
			}
		});
		removelist.forEach(Runnable::run);
		
		// Use choices
		for(Collection<Choice> choicegroup : choices.values()) {
			int remaining=1;
			while(choicegroup.size()>0 && remaining>0) {
				Choice choice=((ArrayList<Choice>)choicegroup).get(random.nextInt(choicegroup.size()));
				if(choice.id.equals("GLOBAL")) {
					remaining=1;
					choicegroup.remove(choice);
				}
				else {
					if(!choice.repeatable)choicegroup.remove(choice);
					remaining=remaining+choice.remaining-1;
				}
				for(int i=0; i<choice.repeat; i++) {
					DataChunk newchunk=generateModel(choice.name);
					if(newchunk!=null) {
						newchunk.setName(choice.target.getName());
						choice.parent.add(newchunk);
						if(newchunk instanceof ObjectChunk)generateObjectChunk((ObjectChunk)newchunk);
					}
				}
			}
		}
	}
	
	private class Choice{
		public String name="none";
		public String id="GLOBAL";
		public int repeat=1;
		public int remaining=0;
		public boolean repeatable=true;
		public DataChunk target;
		public ObjectChunk parent;
	}
}
