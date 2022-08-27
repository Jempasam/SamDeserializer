package jempasam.objectmanager;

public class ObjectManagers {
	/*public static <T> ObjectLoader<T> createLoader(ObjectManager<T> manager, Class<T> type, String prefix, String name){
		ObjectLoader<T> newproto=null;
		SLogger logger=new SimpleSLogger(System.out);
		if(name.equals("strobjo")) {
			newproto=new SimpleObjectLoader<T>(
					manager,
					logger,
					new StrobjoDataDeserializer((i)->new InputStreamSimpleTokenizer(i, " \t\r\n", ":(),", "\"'"),logger),
					ValueParsers.createSimpleValueParser(),
					type,
					prefix);
		}
		return newproto;
	}*/
}
