package jempasam.converting;

public interface ValueParser {
	public <F,T> T parse(Class<T> to, F converted);
}
