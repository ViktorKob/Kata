package net.thomas.kata.patterns.creational;

public class Singleton {
	private static Singleton instance;
	static {
		instance = new Singleton();
	}

	public static synchronized Singleton getInstance() {
		return instance;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final Singleton singleton = getInstance();
	}
}
