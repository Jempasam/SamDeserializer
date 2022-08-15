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
import jempasam.textanalyzis.reader.IteratorBufferedReader;
import jempasam.textanalyzis.tokenizer.Tokenizer;

public class StrobjoDataDeserializer extends AbstractDataDeserializer{
	
	
	
	private String openToken="(";
	private String closeToken=")";
	private String affectationToken=":";
	private String separatorToken=",";
	
	public String getOpenToken() { return openToken; }
	public String getCloseToken() { return closeToken; }
	public String getAffectationToken() { return affectationToken; }
	public String getSeparatorToken() { return separatorToken; }
	
	public void setOpenToken(String s) { openToken=s; }
	public void setCloseToken(String s) { closeToken=s; }
	public void setAffectationToken(String s) { affectationToken=s; }
	public void setSeparatorToken(String s) { separatorToken=s; }
	
	
	
	public StrobjoDataDeserializer(Function<InputStream, Tokenizer> tokenizerSupplier, SLogger logger) {
		super(logger,tokenizerSupplier);
	}
		
	
	
	@Override
	public ObjectChunk loadFrom(InputStream input) {
		ObjectChunk ret=loadChunk(new IteratorBufferedReader<String>(tokenizerSupplier.apply(input),new String[5]));
		ret.setName("root");
		return ret;
	}
	
	private ObjectChunk loadChunk(IteratorBufferedReader<String> tokenizer) {
		logger.enter("new Object");
		ObjectChunk newchunk=new SimpleObjectChunk(null);
		
		String token;
		boolean endofobject=false;
		boolean endofparameter;
		
		List<String> names=new ArrayList<>();
		List<DataChunk> values=new ArrayList<>();
		int i=1;
		//Load names of parameter and their values
		while(!endofobject) {
			logger.enter("Parameter "+i);
			
			endofparameter=false;
			
			//LOAD NAMES
			while(true) {
				token=tokenizer.next();
				//CLOSE OBJECT
				if(token==null || token.equals(closeToken)) {
					endofobject=true;
					break;
				}
				//CLOSE NAME LIST
				else if(token.equals(affectationToken)) {
					break;
				}
				//CLOSE PARAMETER
				else if(token.equals(separatorToken)) {
					endofparameter=true;
					break;
				}
				//ADD NAME
				else names.add(token);
			}
			logger.info("names:"+names);
			
			//LOAD VALUES
			if(!endofobject && !endofparameter)
			while(true) {
				token=tokenizer.next();
				//CLOSE OBJECT
				if(token==null || token.equals(closeToken)) {
					endofobject=true;
					break;
				}
				else if(token.equals(separatorToken)) {
					break;
				}
				else{
					tokenizer.backward();
					DataChunk dc=loadDataChunkValue(tokenizer);
					if(dc!=null) {
						values.add(dc);
					}
				}
			}
			//CANCELING ERROR
			createDataChunks(names, values).forEach(newchunk::add);
			
			values.clear();
			names.clear();
			i++;
			logger.exit();
		}
		logger.info("Result: "+newchunk);
		logger.exit();
		return newchunk;
	}
	
	private DataChunk loadDataChunkValue(IteratorBufferedReader<String> tokenizer) {
		String token;
		if((token=tokenizer.next())!=null) {
			if(token.equals(openToken)) {
				logger.info("As object");
				return loadChunk(tokenizer);
			}
			else{
				logger.info("As value");
				logger.info("="+token);
				return new StringChunk("", token);
			}
		}
		else return null;
	}
}
