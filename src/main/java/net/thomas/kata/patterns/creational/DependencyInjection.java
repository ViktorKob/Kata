package net.thomas.kata.patterns.creational;

public class DependencyInjection {

	private Resource resource;

	public DependencyInjection() {
		resource = null;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String fetchValue() {
		return resource.getValue();
	}

	public static void main(String[] args) {
		final DependencyInjection example = new DependencyInjection();
		example.setResource(new ResourceImpl());
		System.out.println(example.fetchValue());
	}
}

interface Resource {
	String getValue();
}

class ResourceImpl implements Resource {
	@Override
	public String getValue() {
		return "Hello, World Dependency!";
	}
}