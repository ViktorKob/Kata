package net.thomas.kata.patterns.creational;

public class LazyInitialization {
	private Container container;

	public synchronized Container getContainer() {
		if (container == null) {
			container = new Container();
		}
		return container;
	}

	static class Container {
	}
}