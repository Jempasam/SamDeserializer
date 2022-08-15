package jempasam.data.modifier.placer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.modifier.DataModifier;
import jempasam.logger.SLogger;
import jempasam.map.MultiMap;

public class PlacerDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String open;
	private String close;
	private String separator;
	private String separator2;
	private Collection<? extends DataPlacer> placers;
	
	
	
	public PlacerDataModifier(SLogger logger, Collection<? extends DataPlacer> placers, String open, String close, String separator, String separator2) {
		super();
		this.logger = logger;
		this.open = open;
		this.close = close;
		this.separator = separator;
		this.separator2 = separator2;
		this.placers=placers;
	}
	
	public PlacerDataModifier(SLogger logger, Collection<? extends DataPlacer> placers) {
		this(logger, placers, "<", ">", ";", ":");
	}
	
	
	
	private MultiMap<String, DataChunk> models;
	@Override
	public void applyOn(ObjectChunk data) {
		generateObjectChunk(data);
	}
	
	private void generateObjectChunk(ObjectChunk basechunk) {
		// Get choices
		List<Runnable> removelist=new ArrayList<>();
		MultiMap<String, Placement> placements=new MultiMap<String, Placement>(new HashMap<>(), ArrayList::new);
		List<Placement> placementList=new ArrayList<>();
		RecursiveChunkStream stream=basechunk.recursiveStream();
		stream.forEach(datachunk->{
			if(datachunk instanceof StringChunk) {
				StringChunk targetchunk=(StringChunk)datachunk;
				ObjectChunk parentchunk=stream.actualParent();
				if(targetchunk.getValue().startsWith(open) && targetchunk.getValue().endsWith(close)) {
					String parameters[]=targetchunk.getValue().substring(open.length(),targetchunk.getValue().length()-close.length()).split(separator);
					Placement placement=new Placement((toplace)->{
						int place=parentchunk.find(targetchunk);
						toplace.setName(targetchunk.getName());
						parentchunk.add(place+1, toplace);
						if(toplace instanceof ObjectChunk)generateObjectChunk((ObjectChunk)toplace);
					});
					removelist.add(()->parentchunk.remove(targetchunk));
					String group=null;
					for(String param : parameters) {
						String infos[]=param.split(separator2);
						if(infos.length==2 && infos[0].equals("group"))group=infos[1];
						else placement.addParameter(infos[0], Arrays.copyOfRange(infos, 1, infos.length));
					}
					if(group==null)placementList.add(placement);
					else placements.add(group, placement);
				}
			}
		});
		for(Map.Entry<String, List<Placement>> placement : placements.entrySet()) {
			for(DataPlacer placer : placers)placer.place(logger, placement.getValue(), placement.getKey());
			placement.getValue().forEach(p->p.error(logger));
		}
		for(Placement placement : placementList) {
			for(DataPlacer placer : placers)placer.place(logger, Arrays.asList(placement), null);
			placement.error(logger);
		}
		removelist.forEach(Runnable::run);
	}
	
	public class Placement{
		
		
		private HashMap<String, String[]> parameters;
		private Consumer<DataChunk> placer;
		private Random random;
		
		
		public Placement(Consumer<DataChunk> placer) {
			super();
			this.parameters = new HashMap<>();
			this.placer = placer;
			this.random=new Random();
		}
		
		public <T> T getParameter(String name, Set<Integer> sizes, Function<String[], T> func, Supplier<T> def){
			String[] param=parameters.get(name);
			if(param!=null && sizes.contains(param.length)) {
				parameters.remove(name);
				return func.apply(param);
			}
			else return def.get();
		}
		
		public int getIntParameter(String name,  IntSupplier def){
			String[] ret=this.<String[]>getParameter(name, Set.of(1,2), strs->strs, ()->null);
			if(ret==null)return def.getAsInt();
			else {
				if(ret.length==1)return Integer.parseInt(ret[0]);
				else {
					int min=Integer.parseInt(ret[0]);
					int max=Integer.parseInt(ret[1]);
					return min+random.nextInt(max-min+1);
				}
			}
		}
		
		public String getStringParameter(String name,  Supplier<String> def){
			return this.<String>getParameter(name, Set.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), strs->strs[random.nextInt(strs.length)], def);
		}
		
		public boolean getBooleanParameter(String name,  Supplier<Boolean> def){
			return this.<Boolean>getParameter(
				name,
				Set.of(1),
				strs->{
					try {
						return Float.parseFloat(strs[0])>=random.nextFloat();
					}catch (NumberFormatException e) {
						return !strs[0].equals("false");
					}
				},
				def
			);
		}
		
		public void addParameter(String name, String[] parameters) {
			this.parameters.put(name, parameters);
		}
		
		public void place(DataChunk placed) {
			placer.accept(placed);
		}
		
		public void error(SLogger logger) {
			for(String name : parameters.keySet())logger.warning("Unused argument \""+name+"\"");
		}
	}
}
