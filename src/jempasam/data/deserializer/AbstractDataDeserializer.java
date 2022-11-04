package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;
import jempasam.samstream.stream.SamStream;

public abstract class AbstractDataDeserializer implements DataDeserializer{
	
	
	
	protected SLogger logger;
	protected Function<InputStream, SamStream<String>> tokenizerSupplier;
	
	
	
	protected AbstractDataDeserializer(SLogger logger, Function<InputStream, SamStream<String>> tokenizerSupplier) {
		super();
		this.logger = logger;
		this.tokenizerSupplier = tokenizerSupplier;
	}
	
	
	
	public SLogger getLogger() {
		return logger;
	}
	
	protected List<DataChunk> createDataChunks(List<String> names, List<DataChunk> values){
		List<DataChunk> ret=new ArrayList<>();
		
		if(values.size()==0 && names.size()==0) {
			logger.debug("Values are missing. Parameter is ignored.");
		}
		else {
			if(names.size()==0) names.add("");
			else if(values.size()==0) {
				for(String n : names)values.add(new StringChunk("", n));
				names.clear();
				names.add("");
			}
			for(String n : names) {
				for(DataChunk v: values) {
					DataChunk d;
					try {
						d = v.clone();
						d.setName(n);
						ret.add(d);
					} catch (CloneNotSupportedException e) { }
				}
			}
		}
		return ret;
	}

}
