package jempasam.data.deserializer.language;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface TokenPredicate extends Predicate<String>{
	
	String getRepresentation();
	
	public static TokenPredicate regex(Pattern pattern) {
		return new TokenPredicate() {
			@Override
			public boolean test(String t) {
				return pattern.matcher(t).find();
			}
			@Override
			public String toString() {
				return pattern.toString();
			}
			@Override
			public String getRepresentation() {
				return "matching \""+pattern.toString()+"\"";
			}
		};
	}
	
	public static final TokenPredicate ANY=new TokenPredicate() {
		@Override
		public boolean test(String t) {
			return true;
		}
		@Override
		public String toString() {
			return "ANY";
		}
		@Override
		public String getRepresentation() {
			return "any token";
		}
	};
}
