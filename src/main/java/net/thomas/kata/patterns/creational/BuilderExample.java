package net.thomas.kata.patterns.creational;

public class BuilderExample {

	private static final int UNKNOWN = -1;
	private final String someRequiredValue;
	private final int someOptionalValue;

	private BuilderExample(String someRequiredValue) {
		this(someRequiredValue, UNKNOWN);
	}

	private BuilderExample(String someRequiredValue, int someOptionalValue) {
		this.someRequiredValue = someRequiredValue;
		this.someOptionalValue = someOptionalValue;
	}

	public String getSomeRequiredValue() {
		return someRequiredValue;
	}

	public int getSomeOptionalValue() {
		return someOptionalValue;
	}

	public static void main(String[] args) {
		final BuilderExample example = new Builder("Hello, World Builder!").setOptionalValue(12).build();
		System.out.println(example.getSomeRequiredValue());
	}

	public static class Builder {
		private final String someRequiredValue;
		private Integer someOptionalValue;

		public Builder(String someRequiredValue) {
			this.someRequiredValue = someRequiredValue;
			someOptionalValue = null;
		}

		public Builder setOptionalValue(int someOptionalValue) {
			this.someOptionalValue = someOptionalValue;
			return this;
		}

		public BuilderExample build() {
			if (someOptionalValue != null) {
				return new BuilderExample(someRequiredValue, someOptionalValue);
			} else {
				return new BuilderExample(someRequiredValue);
			}
		}
	}
}
