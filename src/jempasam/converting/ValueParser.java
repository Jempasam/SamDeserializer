package jempasam.converting;

public interface ValueParser {
	public <T> T parse(Class<T> type, String string);
}
