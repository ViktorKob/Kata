package net.thomas.kata.geometry.algorithms;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.thomas.kata.geometry.PolygonUtil;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonUtilImpl implements PolygonUtil {

	@Override
	public Collection<PolygonVertex> getMonotoneParts(PolygonVertex polygon) {
		return new MonotonePolygonExtractor(polygon).getMonotonePolygons();
	}

	/***
	 * Based on Berg, Krevald, Overmars & Schwarzkopf - Computational Geometry (2nd ed.) <BR>
	 * Chapter 3 - Polygon Triangulation
	 *
	 * @author (implementation) Thomas Brinck
	 *
	 */
	static class MonotonePolygonExtractor {
		private enum VertexType {
			START,
			END,
			REGULAR,
			SPLIT,
			MERGE
		}

		private final Set<PolygonVertex> helperVertices;
		private final List<PolygonVertex> sweepline;
		private final Map<PolygonVertex, VertexType> vertexTypes;

		public MonotonePolygonExtractor(PolygonVertex polygon) {
			vertexTypes = determineVertexTypes(polygon);
			sweepline = polygon.buildSweepline();
			helperVertices = new TreeSet<>();
		}

		private Map<PolygonVertex, VertexType> determineVertexTypes(PolygonVertex polygon) {
			final Map<PolygonVertex, VertexType> vertexTypes = new HashMap<>();
			for (final PolygonVertex vertex : polygon) {
				vertexTypes.put(vertex, getVertexType(vertex));
			}
			return vertexTypes;
		}

		private VertexType getVertexType(PolygonVertex vertex) {
			if (vertex.y > vertex.getBefore().y && vertex.y > vertex.getAfter().y) {
				if (vertex.getBefore().x < vertex.getAfter().getX()) {
					return VertexType.START;
				} else {
					return VertexType.SPLIT;
				}
			}
			if (vertex.y < vertex.getBefore().y && vertex.y < vertex.getAfter().y) {
				if (vertex.getBefore().x < vertex.getAfter().getX()) {
					return VertexType.MERGE;
				} else {
					return VertexType.END;
				}
			}
			return VertexType.REGULAR;
		}

		public Collection<PolygonVertex> getMonotonePolygons() {
			for (final PolygonVertex vertex : sweepline) {
				final VertexType vertexType = vertexTypes.get(vertex);
				System.out.println("Vertex [" + vertex.x + ", " + vertex.y + "] is a " + vertexType + " vertex");
				// switch (vertexType) {
				// case START:
				// handleStartVertex(vertex, helperVertices);
				//
				// }
			}
			return emptySet();
		}
	}
}