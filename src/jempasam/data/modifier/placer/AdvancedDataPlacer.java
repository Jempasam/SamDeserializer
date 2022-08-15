package jempasam.data.modifier.placer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import jempasam.data.chunk.DataChunk;
import jempasam.data.modifier.placer.PlacerDataModifier.Placement;
import jempasam.logger.SLogger;

public class AdvancedDataPlacer implements DataPlacer {
	
	
	
	private Function<String, List<DataChunk>> chunkSupplier;
	private Random random;
	
	
	
	public AdvancedDataPlacer(Function<String, List<DataChunk>> chunkSupplier) {
		super();
		this.chunkSupplier = chunkSupplier;
		this.random = new Random();
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
			if(probability) {
				for(int i=0; i<repeat; i++) {
					try {
						List<DataChunk> chunks=chunkSupplier.apply(name);
						if(chunks==null || chunks.size()==0)logger.error("Invalid chunk name \""+name+"\"");
						else placement.place(chunks.get(random.nextInt(chunks.size())).clone());
					} catch (CloneNotSupportedException e) { logger.error("Cloning Error");}
				}
			}
			if(!unique)remainingPlacement.remove(placement);
		}
	}
}
