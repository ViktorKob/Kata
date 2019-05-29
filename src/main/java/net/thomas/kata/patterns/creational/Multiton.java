package net.thomas.kata.patterns.creational;

public enum Multiton {
	A,
	B;
	private Container instance;

	private Multiton() {
		instance = new Container();
	}

	public synchronized Container getInstance() {
		return instance;
	}

	static class Container {
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final Container aInstance = A.getInstance();
		final Container bInstance = B.getInstance();
	}
}