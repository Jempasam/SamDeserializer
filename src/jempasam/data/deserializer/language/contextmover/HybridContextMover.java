package jempasam.data.deserializer.language.contextmover;

import java.util.List;

import jempasam.data.deserializer.language.TokenType;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;
import jempasam.samstream.stream.SamStream.BufferedSStream;

@Loadable
public class HybridContextMover implements ContextMover{
	
	
	
	private TokenType entered;
	private TokenType moved;
	
	
	
	public HybridContextMover(TokenType entered, TokenType moved) {
		super();
		this.entered = entered;
		this.moved = moved;
	}
	
	@LoadableParameter
	public HybridContextMover() {
		entered=null;
		moved=null;
	}
	
	
	@LoadableParameter(name = "enterTo")
	public void setEntered(TokenType type) {
		entered=type;
	}
	
	@LoadableParameter(name = "moveTo")
	public void setMoved(TokenType type) {
		moved=type;
	}
	
	@Override
	public TokenType define(List<TokenType> typeStack, BufferedSStream<String> tokenizer) {
		if(moved!=null)typeStack.set(typeStack.size()-1, moved);
		if(entered!=null)typeStack.add(entered);
		return entered!=null  ? entered : moved;
	}
	
	@Override
	public String toString() {
		return "("+(moved!=null ? " moveTo "+moved.getName() : "")+(entered!=null ? " enterTo "+entered.getName() : "")+")";
	}
	
	
	
}
