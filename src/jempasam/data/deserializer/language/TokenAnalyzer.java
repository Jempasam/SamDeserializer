package jempasam.data.deserializer.language;

import java.util.List;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.deserializer.language.contextmover.ContextMover;
import jempasam.data.deserializer.language.datawriter.DataWriter;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;
import jempasam.samstream.stream.SamStream;

@Loadable
public class TokenAnalyzer {
	
	
	
	private TokenPredicate test;
	private ContextMover mover;
	private DataWriter writer;
	
	
	@LoadableParameter(paramnames = {"tester","mover","writer"})
	public TokenAnalyzer(TokenPredicate tester, ContextMover mover, DataWriter writer) {
		super();
		this.test = tester;
		this.mover = mover;
		this.writer = writer;
	}



	public boolean use(List<TokenType> typeStack, List<ObjectChunk> chunkStack, SamStream.BufferedSStream<String> tokenizer, String token) {
		if(test.test(token)) {
			mover.define(typeStack, tokenizer);
			writer.register(token, chunkStack);
			return true;
		}
		else return false;
	}
	
	public String getRepresentation() {
		return test.getRepresentation();
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("(if ").append(test).append(" move ").append(mover).append(" write ").append(writer).append(")");
		return sb.toString();
	}
	
	
}
