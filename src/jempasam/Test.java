package jempasam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.deserializer.DataDeserializer;
import jempasam.data.deserializer.DataDeserializers;
import jempasam.data.modifier.ExtracterDataModifier;
import jempasam.data.modifier.placer.AdvancedDataPlacer;
import jempasam.data.modifier.placer.DataPlacer;
import jempasam.data.modifier.placer.PlacerDataModifier;
import jempasam.data.serializer.JsonDataSerializer;
import jempasam.logger.SLoggers;

public class Test {
	public static void main(String[] args) {
		DataDeserializer deserializer=DataDeserializers.createJSONLikeStrobjoDS(SLoggers.OUT);
		try {
			ObjectChunk data=deserializer.loadFrom(new FileInputStream(new File("test.swjson")));
			JsonDataSerializer serializer=new JsonDataSerializer();
			ExtracterDataModifier extracter=new ExtracterDataModifier(SLoggers.OUT);
			PlacerDataModifier modifier=new PlacerDataModifier(SLoggers.OUT, Arrays.<DataPlacer>asList(new AdvancedDataPlacer(extracter.variables()::get)));
			
			serializer.write(System.out, data);
			data=extracter.apply(data);
			data=modifier.apply(data);
			serializer.write(System.out, data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		/*Iterator<? extends DataChunk> it=magasin.recursiveStream().valuesOfType(Integer.class).iterator();
		while(it.hasNext())System.out.println(it.next());*/
	}
}
