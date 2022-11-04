package jempasam.data.deserializer.language.contextmover;

import java.util.List;

import jempasam.data.deserializer.language.TokenType;
import jempasam.samstream.stream.SamStream;

public interface ContextMover {
	TokenType define(List<TokenType> typeStack, SamStream.BufferedSStream<String> tokenizer);
	
	public static ContextMover enterTo(TokenType type) {
		return new HybridContextMover(type,null, 0,false);
	}
	
	public static ContextMover moveTo(TokenType type) {
		return new HybridContextMover(null, type, 0,false);
	}
	
	public static ContextMover enterToFrom(TokenType type, TokenType from) {
		return new HybridContextMover(type, from, 0,false);
	}
	
	public static final ContextMover EXIT=new HybridContextMover(null,null,1,true);
	
	public static final ContextMover EXIT2=new HybridContextMover(null,null,2,true);
	
	public static final ContextMover EXIT3=new HybridContextMover(null,null,3,true);
	
	public static final ContextMover KEEP=new HybridContextMover(null,null,0,false);
	
	public static Class<?> defaultSubclass(){
		return HybridContextMover.class;
	}
}
