package net.thomas.kata.geometry;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide.matchingVertex;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.TRIANGLE_VERTICES;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex;
import net.thomas.kata.geometry.objects.PolygonVertex;
import net.thomas.kata.geometry.pathfinding.PathfindingUtil;
import net.thomas.kata.geometry.pathfinding.objects.Path;
import net.thomas.kata.geometry.pathfinding.objects.Portal;
import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;
import net.thomas.kata.geometry.pathfinding.objects.Triangle;

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
	private final PathfindingUtil pathFindingUtil;

	private final List<Path> paths;

	private boolean renderOriginal;
	private boolean renderMonotones;
	private boolean renderTriangles;
	private boolean renderTriangleGraph;
	private boolean renderVertices;
	private boolean renderPaths;

	public static void main(String[] args) {
		final PolygonUtil util = new PolygonUtilImpl();
		Collection<PolygonVertex> polygons = combineSimplePolygons(SIMPLE_CLEAN_SAMPLE, COLINEAR_SAMPLE, SIMPLE_SAMPLE_WITH_CUTS, MERGE_CASES,
				BOOK_EXAMPLE_POLYGON);
		polygons = appendHoles(polygons, SIMPLE_CLEAN_SAMPLE_HOLE, COLINEAR_SAMPLE_HOLE);
		long stamp = System.nanoTime();
		final Collection<PolygonVertex> monotonePolygons = util.getMonotoneParts(polygons);
		System.out.println("Time spend building monotone parts: " + (System.nanoTime() - stamp) / 1000000.0 + " ms");
		stamp = System.nanoTime();
		final Collection<PolygonTriangle> triangleGraphs = util.triangulateMonotonePolygons(monotonePolygons);
		System.out.println("Time spend building initial triangle graphs: " + (System.nanoTime() - stamp) / 1000000.0 + " ms");
		stamp = System.nanoTime();
		final PathfindingUtil pathFindingUtil = util.buildPathFindingUtil(triangleGraphs);
		System.out.println("Time spend building Pathfinder Util: " + (System.nanoTime() - stamp) / 1000000.0 + " ms");
		final PolygonRenderer renderer = new PolygonRenderer(polygons, monotonePolygons, triangleGraphs, pathFindingUtil);
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

	public PolygonRenderer(Collection<PolygonVertex> originalPolygons, Collection<PolygonVertex> monotonePolygons, Collection<PolygonTriangle> triangleGraphs,
			PathfindingUtil pathFindingUtil) {
		this.originalPolygons = originalPolygons;
		this.monotonePolygons = monotonePolygons;
		this.triangleGraphs = triangleGraphs;
		this.pathFindingUtil = pathFindingUtil;
		paths = new LinkedList<>();
		setLocation(500, 400);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addKeyListener(this);
		addMouseListener(new PathDefinitionListener());
		renderOriginal = renderMonotones = renderTriangles = renderTriangleGraph = renderVertices = renderPaths = true;
	}

	@Override
	public void paint(Graphics g) {
		final Graphics2D graphics = (Graphics2D) g;
		graphics.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		graphics.setComposite(AlphaComposite.getInstance(SRC_OVER));
		if (renderTriangles) {
			renderTriangles(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Triangles (3)", 10, 80);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Triangles (3)", 10, 80);
		}
		if (renderMonotones) {
			renderMonotonePieces(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Monotone Polygons (2)", 10, 60);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Monotone Polygons (2)", 10, 60);
		}
		if (renderOriginal) {
			renderOriginalPolygon(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Polygons (1)", 10, 40);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Polygons (1)", 10, 40);
		}
		if (renderTriangleGraph) {
			renderTriangleGraphs(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Portal Graphs (4)", 10, 100);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Portal Graphs (4)", 10, 100);
		}
		if (renderVertices) {
			renderVertices(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Vertices (5)", 10, 120);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Vertices (5)", 10, 120);
		}
		if (renderPaths) {
			renderPaths(graphics);
			graphics.setColor(new Color(.0f, .5f, .0f, 1.0f));
			graphics.drawString("Paths (6)", 10, 140);
		} else {
			graphics.setColor(new Color(.5f, .0f, .0f, 1.0f));
			graphics.drawString("Paths (6)", 10, 140);
		}
		graphics.setColor(new Color(.0f, .0f, .0f, 1.0f));
		graphics.drawString("Drag from triangle to triangle to create path", 300, 40);
		graphics.drawString("(Esc) to exit", 10, 160);
	}

	private void renderTriangles(final Graphics2D graphics) {
		graphics.setStroke(new BasicStroke(4.0f));
		graphics.setColor(new Color(.0f, 0.8f, 0.0f, 1.0f));
		for (final PolygonTriangle triangle : triangleGraphs) {
			drawEdges(triangle, graphics);
		}
	}

	private void renderMonotonePieces(final Graphics2D graphics) {
		graphics.setStroke(new BasicStroke(3.0f));
		graphics.setColor(new Color(0.8f, .0f, .0f, 1.0f));
		for (final PolygonVertex polygon : monotonePolygons) {
			drawEdges(polygon, graphics);
		}
	}

	private void renderOriginalPolygon(final Graphics2D graphics) {
		graphics.setStroke(new BasicStroke(2.0f));
		graphics.setColor(new Color(.0f, .0f, 1.0f, 1.0f));
		for (final PolygonVertex polygon : originalPolygons) {
			drawEdges(polygon, graphics);
		}
	}

	private void renderTriangleGraphs(Graphics2D graphics) {
		renderNodes(graphics);
	}

	@SuppressWarnings("deprecation")
	private void renderNodes(Graphics2D graphics) {
		graphics.setColor(new Color(.6f, .6f, 0.6f, 1.0f));
		for (final Entry<Triangle, Collection<PortalGraphNode>> triangleData : pathFindingUtil.getTriangle2PortalNodeMap().entrySet()) {
			for (final PortalGraphNode node : triangleData.getValue()) {
				drawPortal(node.getPortal(), graphics);
			}
		}
	}

	private void drawPortal(Portal portal, Graphics2D graphics) {
		final Point2D first = portal.getP1();
		final Point2D second = portal.getP2();
		graphics.drawLine(translateXIntoFramespace(first.getX()), translateYIntoFramespace(first.getY()), translateXIntoFramespace(second.getX()),
				translateYIntoFramespace(second.getY()));
	}

	private void renderVertices(final Graphics2D graphics) {
		graphics.setColor(new Color(.0f, .0f, .0f, 1.0f));
		for (final PolygonVertex polygon : monotonePolygons) {
			drawVertices(polygon, graphics);
		}
	}

	private void renderPaths(Graphics2D graphics) {
		graphics.setColor(new Color(.6f, 0.8f, .0f, 1.0f));
		for (final Path path : paths) {
			final Line2D centerLine = new Line2D.Double(path.origin, path.destination);
			Point2D previous = path.origin;
			for (final Portal portal : path.route) {
				final Point2D next = portal.getBestIntersectionPoint(centerLine);
				graphics.drawLine(translateXIntoFramespace(previous.getX()), translateYIntoFramespace(previous.getY()), translateXIntoFramespace(next.getX()),
						translateYIntoFramespace(next.getY()));
				previous = next;
			}
			final Point2D next = path.destination;
			graphics.drawLine(translateXIntoFramespace(previous.getX()), translateYIntoFramespace(previous.getY()), translateXIntoFramespace(next.getX()),
					translateYIntoFramespace(next.getY()));
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
		for (final TriangleVertex vertex : TRIANGLE_VERTICES) {
			drawEdge(triangle.getVertex(vertex), triangle.getVertex(vertex.next()), graphics);
		}
		drawnTriangles.add(triangle);
		for (final TriangleVertex vertexId : TRIANGLE_VERTICES) {
			final PolygonTriangle neighbour = triangle.getNeighbour(matchingVertex(vertexId));
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

	private double translateXIntoWorldspace(int value) {
		return (value - DISPLACEMENT) / ZOOM_FACTOR;
	}

	private double translateYIntoWorldspace(int value) {
		return (SCREEN_HEIGHT - value - DISPLACEMENT) / ZOOM_FACTOR;
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
			renderTriangleGraph = !renderTriangleGraph;
		} else if (e.getKeyChar() == '5') {
			renderVertices = !renderVertices;
		} else if (e.getKeyChar() == '6') {
			renderPaths = !renderPaths;
		} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		repaint();
	}

	private class PathDefinitionListener extends MouseAdapter {
		private Point2D.Double clickLocationInWorld;

		public PathDefinitionListener() {
			clickLocationInWorld = null;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			final Point location = e.getPoint();
			clickLocationInWorld = new Point2D.Double(translateXIntoWorldspace(location.x), translateYIntoWorldspace(location.y));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			final long stamp = nanoTime();
			final Point location = e.getPoint();
			final Double releaseLocationInWorld = new Point2D.Double(translateXIntoWorldspace(location.x), translateYIntoWorldspace(location.y));
			boolean success = false;
			if (clickLocationInWorld != null) {
				final Path path = pathFindingUtil.buildPath(clickLocationInWorld, releaseLocationInWorld);
				if (path != null) {
					paths.add(path);
					repaint();
					success = true;
				}
			}
			System.out.println("Time spend " + (success ? "building" : "failing to build") + " Path: " + (System.nanoTime() - stamp) / 1000000.0 + " ms");
		}
	}
}