package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.stream.SamStream.BufferedSStream;

public class ChardentDataDeserializer extends AbstractDataDeserializer {
	
	
	
	private String separatorToken="\n";
	private String affectationToken=":";
	private String indentorToken="-";
	
	public String getIndentorToken() { return indentorToken;}
	public String getAffectationToken() { return affectationToken;}
	public String getSeparatorToken() { return separatorToken; }
	
	public void setIndentorToken(String indentorToken) {this.indentorToken = indentorToken;}
	public void setAffectationToken(String affectationToken) { this.affectationToken = affectationToken;}
	public void setSeparatorToken(String separatorToken) { this.separatorToken = separatorToken;}
	
	
	
	public ChardentDataDeserializer(Function<InputStream, SamStream<String>> tokenizerSupplier, SLogger logger) {
		super(logger,tokenizerSupplier);
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		BufferedSStream<String> input=tokenizerSupplier.apply(i).buffered(5);
		ObjectChunk ret=loadObject(input, 0);
		ret.setName("root");
		return ret;
	}
	
	private int countIndent(BufferedSStream<String> input) {
		if(!input.hasNext())return 0;
		int i=0;
		while(input.hasNext() && input.tryNext().equals(indentorToken)) {
			i++;
		}
		input.back();
		logger.debug("indent "+i);
		return i;
	}
	
	private ObjectChunk loadObject(BufferedSStream<String> input, int actual_indent) {
		logger.enter().debug("new Object");
		
		ObjectChunk ret=new SimpleObjectChunk("");
		
		String token;
		int paramindent=actual_indent;
		while(input.hasNext() && paramindent>=actual_indent) {
			List<String> names=new ArrayList<>();
			List<DataChunk> values=new ArrayList<>();
			try {
				logger.enter().debug("parameter indented by "+paramindent);
				//Load names
				while(input.hasNext() && !(token=input.tryNext()).equals(affectationToken)) {
					if(token.equals(separatorToken))throw new Throwable();
					else names.add(token);
				}
				logger.debug("names: "+names);
				
				//Load string values
				while(input.hasNext() && !(token=input.tryNext()).equals(separatorToken)) {
					values.add(new StringChunk("", token));
				}
				logger.debug("string values count: "+values.size());
				
				// Create object
				paramindent=countIndent(input);
				if(paramindent>actual_indent) {
					logger.debug("as Object");
					values.add(loadObject(input, paramindent));
				}
				createDataChunks(names, values).forEach(ret::add);
				logger.exit();
			}catch (Throwable t) { }
		}
		logger.exit();
		return ret;
	}
	
}
