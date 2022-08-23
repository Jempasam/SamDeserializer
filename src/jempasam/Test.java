package jempasam;

import jempasam.data.loader.tags.Loadable;
import jempasam.data.loader.tags.LoadableParameter;

@Loadable
public class Test {
	
	
	private String name;
	private int age;
	
	@LoadableParameter(paramnames = {"name","age"})
	public Test(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	@LoadableParameter
	public Test() {
	}
	
	@Override
	public String toString() {
		return name+" "+age+" ans";
	}
	
	public static void test(int num) {
		System.out.println(num+" : "+(num*16)+", "+(num<<4));
	}
	public static void main(String[] args) {
		test(0);
		test(1);
		test(2);
		test(-1);
		test(-2000000000);
	}
}
