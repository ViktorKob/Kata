package net.thomas.kata.patterns.creational;

public class ImmutableParameterObject {
	public final String someValue;
	public final int someOtherValue;

	public ImmutableParameterObject(String someValue, int someOtherValue) {
		this.someValue = someValue;
		this.someOtherValue = someOtherValue;
	}

	public static void main(String[] args) {
		final ImmutableParameterObject parameterObject = new ImmutableParameterObject("Hello, world parameter!", 42);
		System.out.println(parameterObject.someValue + ", " + parameterObject.someOtherValue);
	}
}