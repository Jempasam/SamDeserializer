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

public class BaliseDataDeserializer extends AbstractDataDeserializer{
	
	
	
	private boolean permissive=false;
	
	private String openBaliseToken="<";
	private String closeBaliseToken=">";
	private String endBaliseToken="/";
	private String assignementToken="=";
	private String separatorToken=";";
	
	public void setPermissive(boolean permissive) { this.permissive = permissive; }
	public boolean isPermissive() { return permissive; }
	
	public String getOpenBaliseToken() {return openBaliseToken;}
	public String getCloseBaliseToken() {return closeBaliseToken;}
	public String getEndBaliseToken() {return endBaliseToken;}
	public String getAssignementToken() {return assignementToken;}
	public String getSeparatorToken() {return separatorToken;}

	public void setOpenBaliseToken(String openBaliseToken) {this.openBaliseToken = openBaliseToken;}
	public void setCloseBaliseToken(String closeBaliseToken) {this.closeBaliseToken = closeBaliseToken;}
	public void setEndBaliseToken(String endBaliseToken) {this.endBaliseToken = endBaliseToken;}
	public void setAssignementToken(String assignementToken) {this.assignementToken = assignementToken;}
	public void setSeparatorToken(String separatorToken) {this.separatorToken=separatorToken;}
	
	
	
	public BaliseDataDeserializer(Function<InputStream, Tokenizer> tokenizerSupplier, SLogger logger) {
		super(logger,tokenizerSupplier);
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		IteratorBufferedReader<String> input=new IteratorBufferedReader<>(tokenizerSupplier.apply(i), new String[5]);
		ObjectChunk ret=loadObject(input);
		ret.setName("root");
		return ret;
	}
	
	private ObjectChunk loadObject(IteratorBufferedReader<String> input) {
		logger.enter("new object");
		String token=null;
		ObjectChunk ret=new SimpleObjectChunk(null);
		//Each parameter
		boolean inparam=true;
		boolean hasmember=true;
		while(inparam&&input.hasNext()){
			List<String> names=new ArrayList<>();
			List<DataChunk> values=new ArrayList<>();
			
			//Load valueparameters
			logger.enter("New parameter");
			paramload:{
				while(input.hasNext() && !(token=input.next()).equals(assignementToken)) {
					if(token.equals(closeBaliseToken)) {
						inparam=false;
						break paramload;
					}
					else if(token.equals(separatorToken)){
						break paramload;
					}
					else if(token.equals(endBaliseToken)){
						token=input.next();
						if(!token.equals(closeBaliseToken)) {
							if(!permissive)logger.info("Miss a closingToken after endBaliseToken in opening balise");
							input.backward();
						}
						hasmember=false;
						inparam=false;
						break paramload;
					}
					else if(token.equals(assignementToken))break;
					else names.add(token);
				}
				
				while(input.hasNext() && !(token=input.next()).equals(separatorToken)) {
					if(token.equals(closeBaliseToken)){
						inparam=false;
						break paramload;
					}
					else if(token.equals(separatorToken)){
						break paramload;
					}
					else if(token.equals(endBaliseToken)){
						token=input.next();
						if(!token.equals(closeBaliseToken)) {
							if(!permissive)logger.info("Miss a closingBaliseToken after endBaliseToken in opening balise");
							input.backward();
						}
						hasmember=false;
						inparam=false;
						break paramload;
					}
					else values.add(new StringChunk(null, token));
				}
			}
			createDataChunks(names, values).forEach(ret::add);
			
			logger.exit();
		}
		while(hasmember&&input.hasNext()) {
			token=input.next();
			if(!token.equals(openBaliseToken)) {
				logger.info("Invalid token \""+token+"\" should be an openBaliseToken");
			}
			else if(input.next().equals(endBaliseToken)){
				token=input.next();
				if(!token.equals(closeBaliseToken)) {
					if(!permissive)logger.info("Miss a closingBaliseToken after endBaliseToken in opening balise");
					input.backward();
				}
				break;
			}
			else {
				input.backward();
				String name=input.next();
				ObjectChunk objectchunk=loadObject(input);
				objectchunk.setName(name);
				ret.add(objectchunk);
			}
		}
		logger.exit();
		return ret;
	}
}