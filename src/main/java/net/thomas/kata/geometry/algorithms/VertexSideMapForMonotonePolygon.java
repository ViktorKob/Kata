package net.thomas.kata.geometry.algorithms;

import static java.util.Collections.singletonMap;
import static net.thomas.kata.geometry.algorithms.VertexSide.BOTTOM;
import static net.thomas.kata.geometry.algorithms.VertexSide.LEFT;
import static net.thomas.kata.geometry.algorithms.VertexSide.RIGHT;
import static net.thomas.kata.geometry.algorithms.VertexSide.TOP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.thomas.kata.geometry.objects.PolygonVertex;

public class VertexSideMapForMonotonePolygon {
	private final Map<PolygonVertex, VertexSide> sides;
	private final PolygonVertex bottom;

	public VertexSideMapForMonotonePolygon(PolygonVertex top) {
		sides = new HashMap<>(singletonMap(top, TOP));
		bottom = determineBottomVertex(top);
		sides.put(bottom, BOTTOM);
		getLeftSide(top, bottom).forEach((vertex) -> sides.put(vertex, LEFT));
		getRightSide(top, bottom).forEach((vertex) -> sides.put(vertex, RIGHT));
	}

	private PolygonVertex determineBottomVertex(PolygonVertex topVertex) {
		PolygonVertex current = topVertex;
		do {
			current = current.getAfter();
		} while (current.getAfter().y <= current.getY());
		return current;
	}

	private List<PolygonVertex> getLeftSide(PolygonVertex top, final PolygonVertex bottom) {
		return determineSide(top, bottom, PolygonVertex::getAfter);
	}

	private List<PolygonVertex> getRightSide(PolygonVertex top, final PolygonVertex bottom) {
		return determineSide(top, bottom, PolygonVertex::getBefore);
	}

	private List<PolygonVertex> determineSide(PolygonVertex top, PolygonVertex bottom, Function<PolygonVertex, PolygonVertex> next) {
		final List<PolygonVertex> side = new LinkedList<>();
		PolygonVertex current = top;
		do {
			current = next.apply(current);
			side.add(current);
		} while (current != bottom);
		side.remove(current);
		return side;
	}

	public VertexSide getSide(PolygonVertex vertex) {
		return sides.get(vertex);
	}

	public PolygonVertex getBottom() {
		return bottom;
	}
}