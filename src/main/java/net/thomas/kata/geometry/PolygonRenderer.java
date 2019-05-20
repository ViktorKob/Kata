package net.thomas.kata.geometry;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.util.Arrays.asList;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_1;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_2;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_3;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonRenderer extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final int SCREEN_HEIGHT = 400;
	private static final int SCREEN_WIDTH = 1800;
	private static final int POINT_DIAMETER = 15;
	private static final int ZOOM_FACTOR = 10;
	private static final int NEIGHBOUR_OFFSET = 30;
	private static final int DISPLACEMENT = 200;

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
	private static final PolygonVertex MERGE_CASES = new PolygonBuilder()
			.add(new PolygonVertex(0, 0), new PolygonVertex(12, 0), new PolygonVertex(12, 8), new PolygonVertex(10, 6), new PolygonVertex(8, 8),
					new PolygonVertex(6, 4), new PolygonVertex(4, 6), new PolygonVertex(2, 4), new PolygonVertex(0, 8))
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
	private final Collection<PolygonTriangle> triangleGraphs;
	private boolean renderOriginal;
	private boolean renderMonotones;
	private boolean renderTriangles;
	private boolean renderVertices;

	public static void main(String[] args) {
		final PolygonUtil util = new PolygonUtilImpl();
		final Collection<PolygonVertex> polygons = combineSimplePolygons(
				SIMPLE_CLEAN_SAMPLE/*
									 * , COLINEAR_SAMPLE, SIMPLE_SAMPLE_WITH_CUTS, MERGE_CASES, BOOK_EXAMPLE_POLYGON
									 */);
		// polygons = appendHoles(polygons, SIMPLE_CLEAN_SAMPLE_HOLE, COLINEAR_SAMPLE_HOLE);
		final Collection<PolygonVertex> monotonePolygons = util.getMonotoneParts(polygons);
		final Collection<PolygonTriangle> triangleGraphs = util.triangulateMonotonePolygons(monotonePolygons);
		final PolygonRenderer renderer = new PolygonRenderer(polygons, monotonePolygons, triangleGraphs);
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

	public PolygonRenderer(Collection<PolygonVertex> originalPolygons, Collection<PolygonVertex> monotonePolygons, Collection<PolygonTriangle> triangleGraphs) {
		this.originalPolygons = originalPolygons;
		this.monotonePolygons = monotonePolygons;
		this.triangleGraphs = triangleGraphs;
		setLocation(500, 400);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addKeyListener(this);
		renderOriginal = renderMonotones = renderTriangles = renderVertices = true;
	}

	@Override
	public void paint(Graphics g) {
		final Graphics2D graphics = (Graphics2D) g;
		graphics.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		graphics.setComposite(AlphaComposite.getInstance(SRC_OVER));
		if (renderTriangles) {
			graphics.setStroke(new BasicStroke(10.0f));
			graphics.setColor(new Color(.0f, 0.8f, 0.0f, 1.0f));
			for (final PolygonTriangle triangle : triangleGraphs) {
				drawEdges(triangle, graphics);
			}
		}
		if (renderMonotones) {
			graphics.setStroke(new BasicStroke(5.0f));
			graphics.setColor(new Color(0.8f, .0f, .0f, 1.0f));
			for (final PolygonVertex polygon : monotonePolygons) {
				drawEdges(polygon, graphics);
			}
		}
		if (renderOriginal) {
			graphics.setStroke(new BasicStroke(2.0f));
			graphics.setColor(new Color(.0f, .0f, 1.0f, 1.0f));
			for (final PolygonVertex polygon : originalPolygons) {
				drawEdges(polygon, graphics);
			}
		}
		if (renderVertices) {
			graphics.setColor(new Color(.0f, .0f, .0f, 1.0f));
			for (final PolygonVertex polygon : monotonePolygons) {
				drawVertices(polygon, graphics);
			}
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

	private void drawEdges(final PolygonTriangle triangle, Graphics graphics) {
		drawEdges(triangle, graphics, new HashSet<>());
	}

	private void drawEdges(final PolygonTriangle triangle, Graphics graphics, Set<PolygonTriangle> drawnTriangles) {
		drawEdge(triangle.getVertex(VERTEX_1), triangle.getVertex(VERTEX_2), graphics);
		drawEdge(triangle.getVertex(VERTEX_2), triangle.getVertex(VERTEX_3), graphics);
		drawEdge(triangle.getVertex(VERTEX_3), triangle.getVertex(VERTEX_1), graphics);
		drawnTriangles.add(triangle);
		for (final TriangleVertex vertexId : TriangleVertex.values()) {
			final PolygonTriangle neighbour = triangle.getNeighbour(vertexId);
			if (neighbour != null && !drawnTriangles.contains(neighbour)) {
				drawEdges(neighbour, graphics, drawnTriangles);
			}
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
		return SCREEN_HEIGHT - (int) (value * ZOOM_FACTOR + DISPLACEMENT);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == '1') {
			renderOriginal = !renderOriginal;
		} else if (e.getKeyChar() == '2') {
			renderMonotones = !renderMonotones;
		} else if (e.getKeyChar() == '3') {
			renderTriangles = !renderTriangles;
		} else if (e.getKeyChar() == '4') {
			renderVertices = !renderVertices;
		}
		repaint();
	}
}