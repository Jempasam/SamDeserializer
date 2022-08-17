package jempasam.data.modifier.placer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.modifier.placer.PlacerDataModifier.Placement;
import jempasam.logger.SLogger;

public class AdvancedDataPlacer implements DataPlacer {
	
	
	
	private Function<String, List<DataChunk>> chunkSupplier;
	private Random random;
	private String openvar;
	private String closevar;
	
	
	
	public AdvancedDataPlacer(Function<String, List<DataChunk>> chunkSupplier, String openvar, String closevar) {
		super();
		this.chunkSupplier = chunkSupplier;
		this.random = new Random();
		this.openvar=openvar;
		this.closevar=closevar;
	}
	
	
	
	@Override
	public void place(SLogger logger, List<Placement> placements, String groupname) {
		List<Placement> remainingPlacement=new ArrayList<>(placements);
		int remaining=1;
		while(remainingPlacement.size()>0 && remaining>0) {
			Placement placement=remainingPlacement.get(random.nextInt(remainingPlacement.size()));
			remaining=remaining-1+placement.getIntParameter("remaining", ()->0);
			
			boolean unique=placement.getBooleanParameter("unique", ()->false);
			int repeat=placement.getIntParameter("repeat", ()->1);
			boolean probability=placement.getBooleanParameter("probability", ()->true);
			String name=placement.getStringParameter("name", ()->"NONAME");
			String vars[]=placement.getStringArrayParameter("vars", ()->new String[0]);
			if(probability) {
				for(int i=0; i<repeat; i++) {
					try {
						List<DataChunk> chunks=chunkSupplier.apply(name);
						if(chunks==null || chunks.size()==0)logger.error("Invalid chunk name \""+name+"\"");
						else {
							DataChunk toplace=chunks.get(random.nextInt(chunks.size())).clone();
							fill(toplace,vars);
							placement.place(toplace);
						}
					} catch (CloneNotSupportedException e) { logger.error("Cloning Error");}
				}
			}
			if(!unique)remainingPlacement.remove(placement);
		}
	}
	
	private void fill(DataChunk chunk, String[] vars) {
		ObjectChunk obj=new SimpleObjectChunk(null);
		obj.add(chunk);
		
		obj.recursiveStream().forEach(ch->{
			for(int i=0; i<vars.length; i++) {
				String motif=openvar+Integer.toString(i)+closevar;
				ch.setName(ch.getName().replace(motif, vars[i]));
				if(ch instanceof StringChunk) {
					StringChunk strch=(StringChunk)chunk;
					strch.setValue(strch.getValue().replace(motif, vars[i]));
				}
			}
		});
	}
}
