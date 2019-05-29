package net.thomas.kata.patterns.creational;

public class LazySingleton {
	private static LazySingleton instance;

	public static synchronized LazySingleton getInstance() {
		if (instance == null) {
			instance = new LazySingleton();
		}
		return instance;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final LazySingleton singleton = getInstance();
	}
}
