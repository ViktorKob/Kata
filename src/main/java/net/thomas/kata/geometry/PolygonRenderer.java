package net.thomas.kata.geometry;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.util.Arrays.asList;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonRenderer extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int SCREEN_HEIGHT = 768;
	private static final int SCREEN_WIDTH = 1600;
	private static final int POINT_DIAMETER = 15;
	private static final int ZOOM_FACTOR = 10;
	private static final int NEIGHBOUR_OFFSET = 30;
	private static final int DISPLACEMENT = 200;
	private static final int ROOF = 600;

	private static final PolygonVertex SIMPLE_CLEAN_SAMPLE = new PolygonBuilder()
			.add(new PolygonVertex(10, 10), new PolygonVertex(0, 10), new PolygonVertex(-10, -10), new PolygonVertex(0, -10))
			.build();
	private static final PolygonVertex COLINEAR_SAMPLE = new PolygonBuilder()
			.add(new PolygonVertex(10, 10), new PolygonVertex(6, 10), new PolygonVertex(3, 10), new PolygonVertex(0, 10), new PolygonVertex(-10, 0),
					new PolygonVertex(-6, 0), new PolygonVertex(-3, 0), new PolygonVertex(0, 0))
			.build();
	private static final PolygonVertex SIMPLE_SAMPLE_WITH_CUTS = new PolygonBuilder()
			.add(new PolygonVertex(0, -1), new PolygonVertex(10, -10), new PolygonVertex(9, 0), new PolygonVertex(10, 10), new PolygonVertex(0, 1),
					new PolygonVertex(-10, 10), new PolygonVertex(-9, 0), new PolygonVertex(-10, -10))
			.build();
	private static final PolygonVertex BOOK_EXAMPLE_POLYGON = new PolygonBuilder()
			.add(new PolygonVertex(12, 6), new PolygonVertex(8, 4), new PolygonVertex(8, 10), new PolygonVertex(2, 8.2), new PolygonVertex(0, 10),
					new PolygonVertex(-4, 8), new PolygonVertex(-1, 4), new PolygonVertex(-2, 2), new PolygonVertex(-5.6, 4), new PolygonVertex(-6, -2),
					new PolygonVertex(-2.4, -6), new PolygonVertex(0, -4), new PolygonVertex(3, -10), new PolygonVertex(2.8, 0), new PolygonVertex(10, -2))
			.build();
	private static final PolygonVertex SIMPLE_CLEAN_SAMPLE_HOLE = new PolygonBuilder()
			.add(new PolygonVertex(4, 5), new PolygonVertex(0, -6), new PolygonVertex(-4, -5), new PolygonVertex(0, 6))
			.build();
	private static final PolygonVertex COLINEAR_SAMPLE_HOLE = new PolygonBuilder()
			.add(new PolygonVertex(0, 7), new PolygonVertex(1.5, 7), new PolygonVertex(3, 7), new PolygonVertex(5, 7), new PolygonVertex(0, 2),
					new PolygonVertex(-1.5, 2), new PolygonVertex(-3, 2), new PolygonVertex(-5, 2))
			.build();

	private final Collection<PolygonVertex> monotonePolygons;
	private final Collection<PolygonVertex> originalPolygons;

	public static void main(String[] args) {
		final PolygonUtil util = new PolygonUtilImpl();
		Collection<PolygonVertex> polygons = combineSimplePolygons(SIMPLE_CLEAN_SAMPLE, COLINEAR_SAMPLE, SIMPLE_SAMPLE_WITH_CUTS, BOOK_EXAMPLE_POLYGON);
		polygons = appendHoles(polygons, SIMPLE_CLEAN_SAMPLE_HOLE, COLINEAR_SAMPLE_HOLE);
		final Collection<PolygonVertex> monotoneParts = util.getMonotoneParts(polygons);
		final PolygonRenderer renderer = new PolygonRenderer(polygons, monotoneParts);
		renderer.setVisible(true);
	}

	private static List<PolygonVertex> combineSimplePolygons(PolygonVertex... polygons) {
		int index = 0;
		for (final PolygonVertex polygon : polygons) {
			for (final PolygonVertex vertex : polygon) {
				vertex.x = vertex.x + index * NEIGHBOUR_OFFSET;
			}
			index++;
		}
		return asList(polygons);
	}

	private static Collection<PolygonVertex> appendHoles(Collection<PolygonVertex> polygons, PolygonVertex... holes) {
		final List<PolygonVertex> mergedPolygons = new LinkedList<>(polygons);
		int index = 0;
		for (final PolygonVertex hole : holes) {
			if (hole != null) {
				for (final PolygonVertex vertex : hole) {
					vertex.x = vertex.x + index * NEIGHBOUR_OFFSET;
				}
				mergedPolygons.add(hole);
			}
			index++;
		}
		return mergedPolygons;
	}

	public PolygonRenderer(Collection<PolygonVertex> originalPolygons, Collection<PolygonVertex> monotonePolygons) {
		this.originalPolygons = originalPolygons;
		this.monotonePolygons = monotonePolygons;
		setLocation(500, 400);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	@Override
	public void paint(Graphics graphics) {
		((Graphics2D) graphics).setStroke(new BasicStroke(3.0f));
		graphics.setColor(BLACK);
		for (final PolygonVertex polygon : monotonePolygons) {
			drawVertices(polygon, graphics);
		}
		graphics.setColor(RED);
		for (final PolygonVertex polygon : monotonePolygons) {
			drawEdges(polygon, graphics);
		}
		graphics.setColor(BLUE);
		for (final PolygonVertex polygon : originalPolygons) {
			drawEdges(polygon, graphics);
		}
	}

	private void drawVertices(final PolygonVertex polygon, Graphics graphics) {
		for (final PolygonVertex vertex : polygon) {
			drawVertex(vertex, graphics);
		}
	}

	private void drawVertex(PolygonVertex vertex, Graphics graphics) {
		graphics.fillOval(translateXIntoFramespace(vertex.x) - POINT_DIAMETER / 2, translateYIntoFramespace(vertex.y) - POINT_DIAMETER / 2, POINT_DIAMETER,
				POINT_DIAMETER);
	}

	private void drawEdges(final PolygonVertex polygon, Graphics graphics) {
		PolygonVertex previous = null;
		for (final PolygonVertex vertex : polygon) {
			if (previous != null) {
				drawEdge(previous, vertex, graphics);
			}
			previous = vertex;
		}
		if (previous != null) {
			drawEdge(previous, polygon, graphics);
		}
	}

	private void drawEdge(PolygonVertex first, PolygonVertex second, Graphics graphics) {
		graphics.drawLine(translateXIntoFramespace(first.x), translateYIntoFramespace(first.y), translateXIntoFramespace(second.x),
				translateYIntoFramespace(second.y));
	}

	private int translateXIntoFramespace(double value) {
		return (int) (value * ZOOM_FACTOR + DISPLACEMENT);
	}

	private int translateYIntoFramespace(double value) {
		return ROOF - (int) (value * ZOOM_FACTOR + DISPLACEMENT);
	}
}