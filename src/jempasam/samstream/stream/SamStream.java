package jempasam.samstream.stream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import jempasam.samstream.collectors.SamCollector;

public interface SamStream<T> extends BaseSamStream<T>{
	
	@Override
	default void reset() {
		throw new UnsupportedOperationException("Unresettable SamStream. Use \"remaining\" methods variant instead.");
	}
	
	// Transform Stream
	default <O> SamStream<O> map(Function<T, O> mapper){
		return new MapSStream<>(this,mapper);
	}
	
	default <O> SamStream<O> map(BiFunction<Integer, T, O> mapper){
		return new Map2SStream<>(this,mapper);
	}
	
	default SamStream<Numerated<T>> numerate(){
		return new Map2SStream<>(this,(number,value)->new Numerated<>(number, value));
	}
	
	default <O> SamStream<O> flatMap(Function<T, SamStream<O>> mapper){
		return new FlattenSStream<>(new MapSStream<>(this, mapper));
	}
	
	
	// Limit
	default SamStream<T> skip(int skipped){
		return new AtResetSStream<>(this, stream->{
			for(int i=0; i<skipped; i++)stream.tryNext();
		});
	}
	
	default SamStream<T> limit(int countlimit){
		return new CounterSStream<>(this, countlimit);
	}
	
	
	// Test
	default SamStream<T> filter(Predicate<T> test){
		return new FilterSStream<>(this, test);
	}
	
	default SamStream<T> filter(BiPredicate<Integer,T> test){
		return new Filter2SStream<>(this, test);
	}
	
	default SamStream<T> until(Predicate<T> tester){
		return new UntilSStream<>(this,tester);
	}
	
	default SamStream<T> distinct(){
		return new DistinctSStream<>(this);
	}
	
	
	// Action
	default void forEachRemaining(Consumer<T> action) {
		T value;
		do {
			value=tryNext();
			if(hasSucceed())action.accept(value);
			else break;
		}while(true);
	}
	
	default void forEach(BiConsumer<SamStream<T>,T> action) {
		reset();
		forEachRemaining(action);
	}
	
	default void forEachRemaining(BiConsumer<SamStream<T>,T> action) {
		T value;
		do {
			value=tryNext();
			if(hasSucceed())action.accept(this,value);
			else break;
		}while(true);
	}
	
	default void forEach(Consumer<T> action) {
		reset();
		forEachRemaining(action);
	}
	
	default <M,O> O collectRemaining(SamCollector<T, M, O> collector) {
		forEachRemaining(input -> {
			collector.give(input);
		});
		return collector.getResult();
	}
	
	default <M,O> O collect(SamCollector<T, M, O> collector) {
		reset();
		return collectRemaining(collector);
	}
	
	default Optional<T> next(){
		T value=tryNext();
		return hasSucceed() ? Optional.of(value) : Optional.empty();
	}
	
	default Optional<T> first(){
		reset();
		return next();
	}
	
	default Optional<T> last(){
		T ret=tryNext();
		T next=tryNext();
		if(!hasSucceed()) {
			reset();
			ret=tryNext();
			if(!hasSucceed())return Optional.empty();
		}
		while(hasSucceed()) {
			ret=next;
			next=tryNext();
		}
		return Optional.of(next);
	}
	
	default SamStream<T> parallel(){
		return new ParallelSamStream<>(this);
	}
	
	default SStreamIterator<T> iterator(){
		return new SStreamIterator<>(this);
	}
	
	
	
abstract static class DecoratorSStream<I,O> implements SamStream<O>{	
		
		SamStream<I> input;
		
		
		public DecoratorSStream(SamStream<I> input) {
			super();
			this.input = input;
		}
		
		public boolean hasSucceed() {
			return input.hasSucceed();
		}
		
		@Override
		public void reset() {
			input.reset();
		}
	}

	abstract static class SameDecoratorSStream<T> extends DecoratorSStream<T,T>{	
		
		public SameDecoratorSStream(SamStream<T> input) {
			super(input);
			this.input = input;
		}
		
		public boolean hasSucceed() {
			return input.hasSucceed();
		}
		
		@Override
		public void reset() {
			input.reset();
		}
		
		@Override
		public T tryNext() {
			return input.tryNext();
		}
	}

	static class MapSStream<I,O> extends DecoratorSStream<I,O>{	
		
		private Function<I, O> mapper;
		
		public MapSStream(SamStream<I> input, Function<I, O> mapper) {
			super(input);
			this.mapper=mapper;
		}
		
		@Override
		public O tryNext() {
			I ret=input.tryNext();
			return input.hasSucceed() ? mapper.apply(ret) : null;
		}
	}
	
	static class FlattenSStream<I> extends DecoratorSStream<SamStream<I>,I>{	
		
		private SamStream<I> stream;
		
		public FlattenSStream(SamStream<SamStream<I>> input) {
			super(input);
			this.stream=input.tryNext();
		}
		
		@Override
		public I tryNext() {
			I ret=stream.tryNext();
			while(!stream.hasSucceed()) {
				stream=input.tryNext();
				if(!input.hasSucceed())return null;
				ret=stream.tryNext();
			}
			return ret;
		}
	}
	
	static class AtResetSStream<T> extends SameDecoratorSStream<T>{	
		
		private Consumer<SamStream<T>> mapper;
		
		public AtResetSStream(SamStream<T> input, Consumer<SamStream<T>> mapper) {
			super(input);
			this.mapper=mapper;
		}
		
		@Override
		public void reset() {
			super.reset();
			mapper.accept(this);
		}
	}
	
	static class Map2SStream<I,O> extends DecoratorSStream<I,O>{	
		
		private BiFunction<Integer,I, O> mapper;
		private int counter;
		
		public Map2SStream(SamStream<I> input, BiFunction<Integer,I, O> mapper) {
			super(input);
			this.mapper=mapper;
			this.counter=-1;
		}
		
		@Override
		public O tryNext() {
			I ret=input.tryNext();
			counter++;
			return input.hasSucceed() ? mapper.apply(counter,ret) : null;
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=-1;
		}
	}
	
	static class FilterSStream<I> extends DecoratorSStream<I,I>{	
		
		private Predicate<I> tester;
		
		public FilterSStream(SamStream<I> input, Predicate<I> tester) {
			super(input);
			this.tester=tester;
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
			} while((succeed=input.hasSucceed()) && !tester.test(ret));
			if(succeed)return ret;
			else return null;
		}
	}

	static class Filter2SStream<I> extends DecoratorSStream<I,I>{	
		
		private BiPredicate<Integer,I> tester;
		private int counter;
		
		public Filter2SStream(SamStream<I> input, BiPredicate<Integer,I> tester) {
			super(input);
			this.tester=tester;
			this.counter=-1;
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
				counter++;
			} while((succeed=input.hasSucceed()) && !tester.test(counter,ret));
			if(succeed)return ret;
			else return null;
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=-1;
		}
	}
	
	static class CounterSStream<I> extends DecoratorSStream<I,I>{	
		
		private int max;
		private int counter;
		
		public CounterSStream(SamStream<I> input, int max) {
			super(input);
			this.max=max;
			this.counter=0;
		}
		
		@Override
		public I tryNext() {
			max--;
			return input.tryNext();
		}
		
		@Override
		public boolean hasSucceed() {
			counter++;
			return counter>=max && input.hasSucceed();
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=0;
		}
	}
	
	static class DistinctSStream<I> extends DecoratorSStream<I,I>{	
		
		private Set<I> set;
		
		public DistinctSStream(SamStream<I> input) {
			super(input);
			this.set=new HashSet<>();
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
			} while((succeed=input.hasSucceed()) && !set.contains(ret));
			if(succeed) {
				set.add(ret);
				return ret;
			}
			else return null;
		}
		
		@Override
		public void reset() {
			super.reset();
			set.clear();
		}
	}
	
	static class UntilSStream<I> extends DecoratorSStream<I,I>{
		
		private Predicate<I> tester;
		private boolean end;
		
		public UntilSStream(SamStream<I> input, Predicate<I> tester) {
			super(input);
			this.tester=tester;
			this.end=false;
		}
		
		@Override
		public I tryNext() {
			if(this.end)return null;
			I ret;
			ret=input.tryNext();
			if(!hasSucceed()) return null;
			if(tester.test(ret)) {
				this.end=true;
				return null;
			}
			return ret;
		}
		
		@Override
		public boolean hasSucceed() {
		return end && input.hasSucceed();
		}
		
		@Override
		public void reset() {
			super.reset();
			this.end=false;
		}
	}
	
	public static class Numerated<T> {
		
		
		private T value;
		private int number;
		
		
		public Numerated(int number, T value) {
			super();
			this.number = number;
			this.value = value;
		}
		
		
		public T getValue() {
			return value;
		}
		
		public int getNumber() {
			return number;
		}
		
		
	}
	
	public static class SStreamIterator<T> implements Iterator<T>{
		
		private SamStream<T> input;
		private T next;
		private T actual;

		public SStreamIterator(SamStream<T> input) {
			super();
			this.input = input;
			next=input.tryNext();
		}
		
		@Override
		public T next() {
			actual=next;
			next=input.tryNext();
			return actual;
		}
		
		public T actual() {
			return actual;
		}
		
		public T peek() {
			return next;
		}
		
		@Override
		public boolean hasNext() {
			return this.input.hasSucceed();
		}
		
		
	}
}
