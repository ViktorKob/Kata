package net.thomas.kata.patterns.concurrency;

public class ThreadSpecificStorage {
	private static ThreadLocal<String> values;

	static {
		values = new ThreadLocal<>();
	}

	public static void setValue(String value) {
		values.set(value);
		try {
			Thread.sleep(50);
		} catch (final InterruptedException e) {
		}
	}

	public static String getValue() {
		return values.get();
	}

	public static void main(String[] args) {
		new Thread(() -> {
			setValue("Hello, World 1!");
			System.out.println(getValue());
		}).start();
		new Thread(() -> {
			setValue("Hello, World 2!");
			System.out.println(getValue());
		}).start();
		new Thread(() -> {
			setValue("Hello, World 3!");
			System.out.println(getValue());
		}).start();
	}
}