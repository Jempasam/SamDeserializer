package jempasam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;
import jempasam.data.deserializer.DataDeserializer;
import jempasam.data.deserializer.DataDeserializers;
import jempasam.data.deserializer.language.LanguageDataDeserializer;
import jempasam.data.deserializer.language.SimpleLanguageLoader;
import jempasam.data.serializer.DataSerializer;
import jempasam.data.serializer.JsonDataSerializer;
import jempasam.logger.SLoggers;
import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.text.TokenizerConfig;
import jempasam.samstream.text.TokenizerSStream;

class Test {

	public static void main(String[] args) {
		long time=System.currentTimeMillis();
		SamStream<Integer> stream=SamStreams.create(i->i<500000, i->{
			double o=2;
			for(int y=0; y<1000; y++)o=Math.log1p(500*Math.log(60*Math.cos(50+Math.sin(Math.pow(Math.sqrt(y*o*i),y*10)))))/56;
			return i;
		});
		stream.parallel().forEach(i->System.out.println(i));
		System.out.println((System.currentTimeMillis()-time)+"ms");
		/*DataSerializer serializer=new JsonDataSerializer();
		TokenizerConfig config=new TokenizerConfig();
		config.commentChars="#";
		config.cutChars="\t\n\r ";
		config.uniqueChars="{}()=;.,";
		DataDeserializer deserializer=DataDeserializers.createJSONLikeStrobjoDS(SLoggers.OUT);
		SimpleLanguageLoader loader=new SimpleLanguageLoader(SLoggers.OUT);
		try {
			ObjectChunk data=deserializer.loadFrom(new FileInputStream(new File("exemple/javaLang.swjson")));
			data.childStream().objects().forEach(oc->oc.add(new StringChunk("name", oc.getName())));
			loader.load(data);
			LanguageDataDeserializer lang=new LanguageDataDeserializer(SLoggers.OUT, i->new TokenizerSStream(i, config), loader.tokenTypes().get("filestart"));
			ObjectChunk result=lang.loadFrom(new FileInputStream(new File("exemple/test.java")));
			System.out.println(serializer.write(result));
			System.out.println(lang.tokens());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
	}
	
}
