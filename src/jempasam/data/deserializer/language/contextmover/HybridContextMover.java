package jempasam.data.deserializer.language.contextmover;

import java.util.List;

import jempasam.data.deserializer.language.TokenType;
import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;
import jempasam.samstream.stream.SamStream.BufferedSStream;

@Loadable
public class HybridContextMover implements ContextMover{
	
	
	@LoadableParameter(name = "in")
	private TokenType entered;
	@LoadableParameter(name = "to")
	private TokenType moved;
	@LoadableParameter(name = "exit")
	private int exit;
	@LoadableParameter(name = "giveToken")
	private boolean doKeep;
	
	
	
	public HybridContextMover(TokenType entered, TokenType moved, int exit, boolean doKeep) {
		super();
		this.entered = entered;
		this.moved = moved;
		this.doKeep = doKeep;
		this.exit = exit;
	}
	
	
	
	@LoadableParameter
	public HybridContextMover() {
		entered=null;
		moved=null;
		exit=0;
		doKeep=false;
	}
	
	
	
	public void setEntered(TokenType type) {
		entered=type;
	}
	
	
	public void setMoved(TokenType type) {
		moved=type;
	}
	
	public void setExit(int exited) {
		exit=exited;
	}
	
	@Override
	public TokenType define(List<TokenType> typeStack, BufferedSStream<String> tokenizer) {
		for(int i=0; i<exit; i++)typeStack.remove(typeStack.size()-1);
		if(moved!=null)typeStack.set(typeStack.size()-1, moved);
		if(entered!=null)typeStack.add(entered);
		if(doKeep)tokenizer.back();
		return entered!=null  ? entered : moved;
	}
	
	@Override
	public String toString() {
		return "("+(exit>0 ? "exit "+exit+" " : "")+(moved!=null ? " moveTo "+moved.getName() : "")+(entered!=null ? " enterTo "+entered.getName() : "")+")";
	}
	
	
	
}
