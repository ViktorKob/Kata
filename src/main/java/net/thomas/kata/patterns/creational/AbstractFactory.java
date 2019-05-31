package net.thomas.kata.patterns.creational;

import static net.thomas.kata.patterns.creational.ComponentType.GOODBYE;
import static net.thomas.kata.patterns.creational.ComponentType.HELLO;

import java.util.EnumMap;
import java.util.Map;

public class AbstractFactory {

	private final Map<ComponentType, ComponentBuilder> products;

	public AbstractFactory() {
		products = new EnumMap<>(ComponentType.class);
		products.put(HELLO, HelloComponent::new);
		products.put(GOODBYE, GoodbyeComponent::new);
	}

	public Component createComponent(ComponentType type) {
		if (products.containsKey(type)) {
			return products.get(type).createComponent();
		} else {
			throw new RuntimeException("Key of type " + type + " has no builder function in the factory");
		}
	}

	@FunctionalInterface
	interface ComponentBuilder {
		Component createComponent();
	}

	public static void main(String[] args) {
		final AbstractFactory factory = new AbstractFactory();
		System.out.println(factory.createComponent(HELLO).getComponentType());
		System.out.println(factory.createComponent(GOODBYE).getComponentType());
	}
}

enum ComponentType {
	HELLO,
	GOODBYE
}

interface Component {
	String getComponentType();
}

class HelloComponent implements Component {
	@Override
	public String getComponentType() {
		return "Hello, World Factory!";
	}
}

class GoodbyeComponent implements Component {
	@Override
	public String getComponentType() {
		return "Goodbye, World Factory!";
	}
}