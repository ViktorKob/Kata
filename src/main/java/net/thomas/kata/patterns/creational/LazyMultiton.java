package net.thomas.kata.patterns.creational;

public enum LazyMultiton {
	A,
	B;
	private Container instance;

	public synchronized Container getInstance() {
		if (instance == null) {
			instance = new Container();
		}
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