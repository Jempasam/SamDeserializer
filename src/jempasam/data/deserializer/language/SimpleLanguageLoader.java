package jempasam.data.deserializer.language;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jempasam.converting.SimpleValueParser;
import jempasam.converting.ValueParsers;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.deserializer.language.contextmover.ContextMover;
import jempasam.data.deserializer.language.datawriter.DataWriter;
import jempasam.data.deserializer.language.datawriter.ModelDataWriter;
import jempasam.data.loader.SimpleObjectLoader;
import jempasam.logger.SLogger;
import jempasam.objectmanager.HashObjectManager;

public class SimpleLanguageLoader {
	
	
	
	private SimpleValueParser valueParser;
	
	private HashObjectManager<TokenType> tokenTypeManager;
	private HashObjectManager<TokenPredicate> tokenPredicateManager;
	private HashObjectManager<DataWriter> dataWriterManager;
	private HashObjectManager<ContextMover> contextMoverManager;
	
	private SimpleObjectLoader<TokenType> tokenTypeLoader;
	private SimpleObjectLoader<TokenPredicate> tokenPredicateLoader;
	private SimpleObjectLoader<DataWriter> dataWriterLoader;
	private SimpleObjectLoader<ContextMover> contextMoverLoader;
	
	
	
	public SimpleLanguageLoader(SLogger logger) {
		tokenTypeManager=new HashObjectManager<>();
		tokenPredicateManager=new HashObjectManager<>();
		dataWriterManager=new HashObjectManager<>();
		contextMoverManager=new HashObjectManager<>();
		
		// Value Parser
		valueParser=ValueParsers.createSimpleValueParser();
		Map<String,TokenPredicate> predicate=new HashMap<>();
		predicate.put("END_OF_FILE", TokenPredicate.regex(Pattern.compile("\\%END_OF_FILE\\%$")));
		valueParser.add(String.class, TokenPredicate.class, str->{
			TokenPredicate ret=tokenPredicateManager.get(str);
			if(ret==null) {
				ret=predicate.get(str);
				if(ret==null) {
					ret=TokenPredicate.regex(Pattern.compile(str));
					predicate.put(str, ret);
				}
			}
			return ret;
		});
		valueParser.add(String.class, TokenType.class, tokenTypeManager::get);
		valueParser.add(String.class, DataWriter.class, dataWriterManager::get);
		valueParser.add(String.class, ContextMover.class, contextMoverManager::get);
		valueParser.add(ObjectChunk.class, DataWriter.class, ModelDataWriter::new);
		
		tokenPredicateManager.register("ANY", TokenPredicate.ANY);
		tokenPredicateManager.register("END_OF_FILE", TokenPredicate.regex(Pattern.compile("\\%END_OF_FILE\\%")));
		dataWriterManager.register("EXIT", DataWriter.EXIT);
		dataWriterManager.register("EXIT2", DataWriter.EXIT2);
		dataWriterManager.register("EXIT3", DataWriter.EXIT3);
		dataWriterManager.register("IGNORE", DataWriter.IGNORE);
		contextMoverManager.register("EXIT", ContextMover.EXIT);
		contextMoverManager.register("EXIT2", ContextMover.EXIT2);
		contextMoverManager.register("EXIT3", ContextMover.EXIT3);
		contextMoverManager.register("KEEP", ContextMover.KEEP);
		
		// Loaders
		tokenTypeLoader=new SimpleObjectLoader<>(logger, valueParser, TokenType.class, "jempasam.data.deserializer.language.");
		tokenPredicateLoader=new SimpleObjectLoader<>(logger, valueParser, TokenPredicate.class, "jempasam.data.deserializer.language.");
		dataWriterLoader=new SimpleObjectLoader<>(logger, valueParser, DataWriter.class, "jempasam.data.deserializer.language.datawriter.DataWriter");
		contextMoverLoader=new SimpleObjectLoader<>(logger, valueParser, ContextMover.class, "jempasam.data.deserializer.language.contextmover.");
	}
	
	public void load(ObjectChunk data) {
		if(data.get("predicates")!=null) tokenPredicateLoader.load(tokenPredicateManager,  (ObjectChunk)data.get("predicates"));
		if(data.get("writers")!=null) dataWriterLoader.load(dataWriterManager,  (ObjectChunk)data.get("writers"));
		if(data.get("movers")!=null) contextMoverLoader.load(contextMoverManager,  (ObjectChunk)data.get("movers"));
		if(data.get("tokens")!=null) tokenTypeLoader.load(tokenTypeManager, (ObjectChunk)data.get("tokens"));
	}



	public SimpleValueParser getValueParser() {
		return valueParser;
	}

	public HashObjectManager<TokenType> tokenTypes() {
		return tokenTypeManager;
	}

	public HashObjectManager<TokenPredicate> tokenPredicates() {
		return tokenPredicateManager;
	}

	public HashObjectManager<DataWriter> dataWriters() {
		return dataWriterManager;
	}

	public HashObjectManager<ContextMover> contextMovers() {
		return contextMoverManager;
	}
	
	
	
	public SimpleObjectLoader<DataWriter> dataWriterLoader() {
		return dataWriterLoader;
	}
	
	public SimpleObjectLoader<ContextMover> contextMoverLoader() {
		return contextMoverLoader;
	}
	
	public SimpleObjectLoader<TokenPredicate> tokenPredicateLoader() {
		return tokenPredicateLoader;
	}
	
	public SimpleObjectLoader<TokenType> tokenTypeLoader() {
		return tokenTypeLoader;
	}
	
	
}
