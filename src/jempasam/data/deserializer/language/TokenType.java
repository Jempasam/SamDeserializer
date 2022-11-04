package jempasam.data.deserializer.language;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;
import jempasam.samstream.stream.SamStream;

@Loadable
public class TokenType {
	
	
	
	private String name;
	private List<TokenAnalyzer> then;
	
	
	@LoadableParameter(paramnames = {"name"})
	public TokenType(String name) {
		super();
		this.name = name;
		this.then = new ArrayList<>();
	}
	
	

	public String getName() {
		return name;
	}
	
	@LoadableParameter
	public void then(TokenAnalyzer analyzer) {
		then.add(analyzer);
	}
	
	@LoadableParameter
	public void include(TokenType included) {
		then.addAll(included.then);
	}
	
	public boolean walk(List<TokenType> typeStack, List<ObjectChunk> chunkStack, SamStream.BufferedSStream<String> tokenizer, String token) {
		for(TokenAnalyzer t : then)if(t.use(typeStack, chunkStack, tokenizer, token))return true;
		return false;
	}
	
	public String getRepresentation() {
		System.out.println(then);
		return then.stream().map(TokenAnalyzer::getRepresentation).collect(Collectors.joining(" or "));
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("\"").append(name).append("\" then (").append(then.stream().map(o->o==null ? "null" : o.toString()).collect(Collectors.joining(", "))).append(")");
		return sb.toString();
	}
	
	
	
}
