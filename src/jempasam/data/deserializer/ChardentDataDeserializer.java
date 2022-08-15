package jempasam.data.deserializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;
import jempasam.logger.OutputStreamSLogger;
import jempasam.textanalyzis.reader.IteratorBufferedReader;
import jempasam.textanalyzis.tokenizer.Tokenizer;
import jempasam.textanalyzis.tokenizer.impl.InputStreamSimpleTokenizer;

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
	
	
	
	public ChardentDataDeserializer(Function<InputStream, Tokenizer> tokenizerSupplier, SLogger logger) {
		super(logger,tokenizerSupplier);
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		IteratorBufferedReader<String> input=new IteratorBufferedReader<>(tokenizerSupplier.apply(i), new String[5]);
		ObjectChunk ret=loadObject(input, countIndent(input));
		ret.setName("root");
		return ret;
	}
	
	private int countIndent(IteratorBufferedReader<String> input) {
		int i=0;
		while(input.hasNext() && input.next().equals(indentorToken)) {
			i++;
		}
		input.backward();
		logger.info("indent "+i);
		return i;
	}
	
	private ObjectChunk loadObject(IteratorBufferedReader<String> input, int actual_indent) {
		logger.enter("new Object");
		
		ObjectChunk ret=new SimpleObjectChunk(null);
		
		String token;
		while( input.hasNext() && !(token=input.next()).equals(separatorToken)) {
			ret.add(new StringChunk("", token));
		}
			
		int newindent;
		while(input.hasNext() && (newindent=countIndent(input))>=actual_indent) {
			List<String> names=new ArrayList<>();
			List<DataChunk> values=new ArrayList<>();
			try {
				logger.enter("parameter");
				//Load names
				while(input.hasNext() && !(token=input.next()).equals(affectationToken)) {
					if(token.equals(separatorToken))throw new Throwable();
					else names.add(token);
				}
				//Load values
				if(newindent>actual_indent) {
					logger.info("as Object");
					values.add(loadObject(input, newindent));
				}
				else {
					logger.info("as Value");
					while(input.hasNext() && !(token=input.next()).equals(separatorToken)) {
						values.add(new StringChunk(null, token));
					}
				}
			}catch (Throwable t) { }
			
			createDataChunks(names, values).forEach(ret::add);
			logger.exit();
		}
		logger.exit();
		return ret;
	}
	
	public static void main(String[] args) {
		DataDeserializer ds=new ChardentDataDeserializer((i)->new InputStreamSimpleTokenizer(i," \r\t",":-\n","\"'"), new OutputStreamSLogger(System.out));
		try {
			System.out.println(ds.loadFrom(new FileInputStream(new File("test.txt"))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
}
