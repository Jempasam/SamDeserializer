package jempasam.data.deserializer.language.contextmover;

import java.util.List;

import jempasam.data.deserializer.language.TokenType;
import jempasam.data.deserializer.language.datawriter.CompositeDataWriter;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.stream.SamStream.BufferedSStream;

public interface ContextMover {
	TokenType define(List<TokenType> typeStack, SamStream.BufferedSStream<String> tokenizer);
	
	public static ContextMover enterTo(TokenType type) {
		return new HybridContextMover(type,null);
	}
	
	public static ContextMover moveTo(TokenType type) {
		return new HybridContextMover(null, type);
	}
	
	public static ContextMover enterToFrom(TokenType type, TokenType from) {
		return new HybridContextMover(type, from);
	}
	
	public static final ContextMover EXIT=new ContextMover() {
		@Override
		public TokenType define(List<TokenType> typeStack, BufferedSStream<String> tokenizer) {
			typeStack.remove(typeStack.size()-1);
			return null;
		}
	};
	
	public static final ContextMover KEEP=new ContextMover() {
		@Override
		public TokenType define(List<TokenType> typeStack, BufferedSStream<String> tokenizer) {
			return null;
		}
	};
	
	public static final ContextMover PASS_DOWN=new ContextMover() {
		@Override
		public TokenType define(List<TokenType> typeStack, BufferedSStream<String> tokenizer) {
			typeStack.remove(typeStack.size()-1);
			tokenizer.back();
			return null;
		}
	};
	
	public static Class<?> defaultSubclass(){
		return HybridContextMover.class;
	}
}
