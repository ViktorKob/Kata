package net.thomas.kata.geometry;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonRenderer extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Collection<PolygonVertex> monotonePolygons;
	private final PolygonVertex originalPolygon;

	public static void main(String[] args) {
		final PolygonVertex polygon = buildPolygon();
		final PolygonUtil util = new PolygonUtilImpl();
		final Collection<PolygonVertex> monotoneParts = util.getMonotoneParts(polygon);
		final PolygonRenderer renderer = new PolygonRenderer(polygon, monotoneParts);
		renderer.setVisible(true);
	}

	private static PolygonVertex buildPolygon() {
		final PolygonVertex polygon = new PolygonBuilder()
				.add(new PolygonVertex(0, -1), new PolygonVertex(10, -10), new PolygonVertex(9, 0), new PolygonVertex(10, 10), new PolygonVertex(0, 1),
						new PolygonVertex(-10, 10), new PolygonVertex(-9, 0), new PolygonVertex(-10, -10))
				.build();
		return polygon;
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
		graphics.fillOval(translateIntoFramespace(vertex.x) - 4, translateIntoFramespace(vertex.y) - 4, 9, 9);
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
		graphics.drawLine(translateIntoFramespace(first.x), translateIntoFramespace(first.y), translateIntoFramespace(second.x),
				translateIntoFramespace(second.y));
	}

	private int translateIntoFramespace(double value) {
		return (int) (value * 10 + 200);
	}
}