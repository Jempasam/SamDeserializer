package jempasam.data.deserializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.stream.SamStream.BufferedSStream;

public class ChardentBaliseDataDeserializer extends AbstractDataDeserializer {
	
	
	
	public String affectationToken=":";
	public String indentorToken="\t";
	public String separatorToken=",";
	public String openToken="(";
	public String closeToken=")";
	public String endLine="\n";
	
	private int curindent;
	
	public ChardentBaliseDataDeserializer(Function<InputStream, SamStream<String>> tokenizerSupplier, SLogger logger) {
		super(logger,tokenizerSupplier);
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		BufferedSStream<String> input=tokenizerSupplier.apply(i).buffered(5);
		countIndent(input);
		ObjectChunk ret=loadObject(input, -1, true);
		ret.setName("root");
		return ret;
	}
	
	private void countIndent(BufferedSStream<String> input) {
		curindent=0;
		if(!input.hasNext()) return;
		while(input.hasNext()) {
			String next=input.tryNext();
			if(next.equals(endLine)) {
				curindent=0;
			}
			else if(!next.equals(indentorToken)) {
				break;
			}
			else curindent++;
		}
		input.back();
		logger.debug("indent "+curindent);
	}
	
	private ObjectChunk loadObject(BufferedSStream<String> input, int actual_indent, boolean skipBalise) {
		logger.enter().debug("new Object");
		
		ObjectChunk ret=new SimpleObjectChunk("");
		
		String token;
		boolean endOfparameter=false;
		if(!skipBalise) {
			// Balise Inside
			while(input.hasNext()&&!endOfparameter) {
				List<String> names=new ArrayList<>();
				List<DataChunk> values=new ArrayList<>();
				//Load names
				while(input.hasNext()) {
					token=input.tryNext();
					if(token.equals(closeToken)) {
						endOfparameter=true;
						input.back();
						break;
					}
					else if(token.equals(separatorToken)) {
						input.back();
						break;
					}
					else if(token.equals(affectationToken))break;
					else names.add(token);
				}
				logger.debug("names: "+names);
				
				//Load string values
				while(input.hasNext()) {
					token=input.tryNext();
					if(token.equals(closeToken)) {
						endOfparameter=true;
						break;
					}
					else if(token.equals(separatorToken))break;
					else values.add(new StringChunk("", token));
				}
				
				createDataChunks(names, values).forEach(ret::add);
				logger.debug("string values count: "+values.size());
				logger.exit();
			}
			
			if(!input.hasNext() || !input.tryNext().equals(endLine))logger.error("Should have a endLine character after a closeCharacter");
			//Balise Outside
			countIndent(input);
		}
		while(input.hasNext()) {
			if(curindent>actual_indent) {
				List<String> names=new ArrayList<>();
				while(input.hasNext()) {
					token=input.tryNext();
					if(token.equals(openToken)) {
						ObjectChunk child=loadObject(input, curindent, false);
						createDataChunks(names, Arrays.asList(child)).forEach(ret::add);
						break;
					}
					else if(token.equals(affectationToken)) {
						List<DataChunk> values=new ArrayList<>();
						while(input.hasNext()) {
							token=input.tryNext();
							if(token.equals(endLine)) {
								break;
							}
							else values.add(new StringChunk("", token));
						}
						logger.debug(names+" "+values);
						int oldindent=curindent;
						countIndent(input);
						logger.debug("="+oldindent+" "+curindent);
						if(curindent>oldindent) {
							ObjectChunk child=loadObject(input, oldindent, true);
							createDataChunks(new ArrayList<>(), values).forEach(child::add);
							createDataChunks(names, Collections.singletonList(child)).forEach(ret::add);
						}
						else createDataChunks(names, values).forEach(ret::add);
						break;
					}
					else if(token.equals(endLine)) {
						createDataChunks(names, new ArrayList<>()).forEach(ret::add);
						countIndent(input);
						break;
					}
					else names.add(token);
				}
			}
			else break;
		}
		logger.exit();
		return ret;
	}
	
}
