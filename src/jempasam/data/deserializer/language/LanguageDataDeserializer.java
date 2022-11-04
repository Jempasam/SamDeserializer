package jempasam.data.deserializer.language;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.SimpleObjectChunk;
import jempasam.data.deserializer.AbstractDataDeserializer;
import jempasam.logger.SLogger;
import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;

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
		AtomicInteger lineCounter=new AtomicInteger(0);
		tokens.clear();
		SamStream.BufferedSStream<String> input=tokenizerSupplier.apply(i)
				.filter(token->{
					if(token.equals("\n")) {
						lineCounter.incrementAndGet();
						return false;
					}
					else return true;
				})
				.then(SamStreams.create("%END_OF_FILE%"))
				.buffered(10);
		
		List<TokenType> stack=new ArrayList<>();
		List<ObjectChunk> chunkStack=new ArrayList<>();
		
		ObjectChunk chunk=new SimpleObjectChunk("root");
		stack.add(basetype);
		chunkStack.add(chunk);
		String last="START";
		while(input.hasNext()&&stack.size()>0) {
			String token=input.tryNext();
			System.out.println(SamStreams.create(stack).map(TokenType::getName).asString());
			System.out.println(SamStreams.create(chunkStack).map(ObjectChunk::getName).asString());
			System.out.println(SamStreams.createAt(Math.max(0, tokens.size()-5),tokens).map(TokenAssociation::toString).asString());
			System.out.println(chunkStack.get(chunkStack.size()-1));
			System.out.println("Analyse: "+token);
			System.out.println();
			if(!stack.get(stack.size()-1).walk(stack, chunkStack, input, token)) {
				logger.error("Unexpected token \""+token+"\" after token \""+last+"\" of type "+stack.get(stack.size()-1).getName());
				int start=Math.max(0, tokens.size()-4);
				logger.error( "After " + SamStreams.<TokenAssociation>create(index->index+start<tokens.size(), index->tokens.get(start+index)).asString() );
				logger.debug("After " + tokens);
				logger.error("In: "+SamStreams.create(stack).map(TokenType::getName).asString());
				logger.error("In: "+SamStreams.create(chunkStack).map(ObjectChunk::getName).asString());
				logger.error("Line: "+lineCounter.intValue());
				logger.error("Should be: "+stack.get(stack.size()-1).getRepresentation());
				return null;
			}
			tokens.add(new TokenAssociation(token, stack.get(stack.size()-1)));
			last=token;
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
