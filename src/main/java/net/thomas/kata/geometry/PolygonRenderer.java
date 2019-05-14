package net.thomas.kata.geometry;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonRenderer extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int POINT_DIAMETER = 21;
	private static final int ZOOM_FACTOR = 30;
	private static final int DISPLACEMENT = 200;
	private static final int ROOF = 600;

	private static final PolygonVertex SIMPLE_CLEAN_SAMPLE = new PolygonBuilder()
			.add(new PolygonVertex(8, 10), new PolygonVertex(-10, 10), new PolygonVertex(-8, -10), new PolygonVertex(10, -10))
			.build();
	private static final PolygonVertex SIMPLE_SAMPLE_WITH_CUTS = new PolygonBuilder()
			.add(new PolygonVertex(0, -1), new PolygonVertex(10, -10), new PolygonVertex(9, 0), new PolygonVertex(10, 10), new PolygonVertex(0, 1),
					new PolygonVertex(-10, 10), new PolygonVertex(-9, 0), new PolygonVertex(-10, -10))
			.build();
	private static final PolygonVertex BOOK_EXAMPLE_POLYGON = new PolygonBuilder()
			.add(new PolygonVertex(6, 3), new PolygonVertex(4, 2), new PolygonVertex(4, 5), new PolygonVertex(1, 4.1), new PolygonVertex(0, 5),
					new PolygonVertex(-2, 4), new PolygonVertex(-.5, 2), new PolygonVertex(-1, 1), new PolygonVertex(-2.8, 2), new PolygonVertex(-3, -1),
					new PolygonVertex(-1.2, -3), new PolygonVertex(0, -2), new PolygonVertex(1.5, -5), new PolygonVertex(1.4, 0), new PolygonVertex(5, -1))
			.build();

	private final Collection<PolygonVertex> monotonePolygons;
	private final PolygonVertex originalPolygon;

	public static void main(String[] args) {
		final PolygonVertex polygon = BOOK_EXAMPLE_POLYGON;
		System.out.println(polygon.allToString());
		final PolygonUtil util = new PolygonUtilImpl();
		final Collection<PolygonVertex> monotoneParts = util.getMonotoneParts(polygon);
		final PolygonRenderer renderer = new PolygonRenderer(polygon, monotoneParts);
		renderer.setVisible(true);
	}

	public PolygonRenderer(PolygonVertex originalPolygon, Collection<PolygonVertex> monotonePolygons) {
		this.originalPolygon = originalPolygon;
		this.monotonePolygons = monotonePolygons;
		setLocation(500, 400);
		setSize(1024, 768);
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
		drawEdges(originalPolygon, graphics);
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