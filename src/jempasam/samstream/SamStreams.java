package jempasam.samstream;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jempasam.samstream.adapter.DoubleSupplierSStream;
import jempasam.samstream.adapter.IterableSStream;
import jempasam.samstream.adapter.IteratorSStream;
import jempasam.samstream.adapter.UniqueFunctionSStream;
import jempasam.samstream.adapter.UniqueSupplierSStream;
import jempasam.samstream.stream.SamStream;

public class SamStreams {
	private SamStreams() {}
	
	public static <T> SamStream<T> create(Iterable<T> iterable) {
		return new IterableSStream<>(iterable);
	}
	
	public static <T> SamStream<T> create(Iterator<T> iterator) {
		return new IteratorSStream<>(iterator);
	}
	
	public static <T> SamStream<T> create(Stream<T> stream) {
		return new IteratorSStream<>(stream.iterator());
	}

	public static <T> SamStream<T> create(Supplier<T> tryNext) {
		return new UniqueSupplierSStream<>(tryNext);
	}
	
	public static <T> SamStream<T> create(Function<Consumer<Boolean>,T> tryNext) {
		return new UniqueFunctionSStream<>(tryNext);
	}
	
	public static <T> SamStream<T> create(Supplier<T> tryNext, BooleanSupplier hasNext) {
		return new DoubleSupplierSStream<>(tryNext, hasNext);
	}
}
