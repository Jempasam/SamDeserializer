package jempasam.data.deserializer.language;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import jempasam.converting.SimpleValueParser;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.deserializer.AbstractDataDeserializer;
import jempasam.data.deserializer.language.contextmover.ContextMover;
import jempasam.data.deserializer.language.datawriter.DataWriter;
import jempasam.data.serializer.JsonDataSerializer;
import jempasam.logger.SLogger;
import jempasam.logger.SLoggers;
import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.text.TokenizerConfig;
import jempasam.samstream.text.TokenizerSStream;

public class LanguageDataDeserializer extends AbstractDataDeserializer{
	
	
	
	private TokenType basetype;
	private List<TokenAssociation> tokens;
	
	
	
	public LanguageDataDeserializer(SLogger logger, Function<InputStream, SamStream<String>> tokenizerSupplier, TokenType basetype) {
		super(logger, tokenizerSupplier);
		this.basetype=basetype;
		this.tokens=new ArrayList<>();
	}
	
	
	
	@Override
	public ObjectChunk loadFrom(InputStream i) {
		tokens.clear();
		SamStream.BufferedSStream<String> input=tokenizerSupplier.apply(i).then(SamStreams.create("%END_OF_FILE%")).buffered(10);
		
		List<TokenType> stack=new ArrayList<>();
		List<ObjectChunk> chunkStack=new ArrayList<>();
		
		ObjectChunk chunk=new SimpleObjectChunk("root");
		stack.add(basetype);
		chunkStack.add(chunk);
		
		while(input.hasNext()) {
			String token=input.tryNext();
			if(!stack.get(stack.size()-1).walk(stack, chunkStack, input, token)) {
				logger.error("Unexpected token \""+token+"\" after token of type "+stack.get(stack.size()-1).getName());
				logger.error("After "+tokens.toString());
				logger.error("Should be: "+stack.get(stack.size()-1).getRepresentation());
				return null;
			}
			tokens.add(new TokenAssociation(input.actual(), stack.get(stack.size()-1)));
		}
		
		return chunk;
	}
	
	public Collection<TokenAssociation> tokens(){
		return tokens;
	}
	
	public static class TokenAssociation{
		public final String token;
		public final TokenType type;
		TokenAssociation(String token, TokenType type) {
			super();
			this.token = token;
			this.type = type;
		}
		@Override
		public String toString() {
			return "("+token+": "+type.getName()+")";
		}
	}
	
}
