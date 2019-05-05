package net.thomas.kata.geometry.objects;

public class PolygonBuilder {
	PolygonVertex currentVertex;

	public PolygonBuilder() {
		currentVertex = null;
	}

	public PolygonBuilder add(double x, double y) {
		if (currentVertex == null) {
			currentVertex = new PolygonVertex(x, y);
		} else {
			currentVertex.insertAfter(new PolygonVertex(x, y));
			currentVertex = currentVertex.getAfter();
		}
		return this;
	}

	public PolygonBuilder add(PolygonVertex... vertices) {
		for (final PolygonVertex vertex : vertices) {
			if (currentVertex == null) {
				currentVertex = vertex;
			} else {
				currentVertex.insertAfter(vertex);
				currentVertex = currentVertex.getAfter();
			}
		}
		return this;
	}

	public PolygonVertex build() {
		return currentVertex.getAfter();
	}
}