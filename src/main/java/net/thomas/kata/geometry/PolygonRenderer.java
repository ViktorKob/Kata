package net.thomas.kata.geometry;

import java.util.Collection;

import javax.swing.JFrame;

import net.thomas.kata.geometry.algorithms.PolygonUtilImpl;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonRenderer extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		final PolygonVertex polygon = buildPolygon();
		final PolygonUtil util = new PolygonUtilImpl();
		final Collection<PolygonVertex> polygonParts = util.getMonotoneParts(polygon);
		final PolygonRenderer renderer = new PolygonRenderer(polygonParts);
		renderer.setVisible(true);
	}

	private static PolygonVertex buildPolygon() {
		final PolygonVertex polygon = new PolygonBuilder()
				.add(new PolygonVertex(0, -1), new PolygonVertex(10, -10), new PolygonVertex(9, 0), new PolygonVertex(10, 10), new PolygonVertex(0, 1),
						new PolygonVertex(-10, 10), new PolygonVertex(-9, 0), new PolygonVertex(-10, -10))
				.build();
		return polygon;
	}

	public PolygonRenderer(Collection<PolygonVertex> polygonParts) {
		setLocation(500, 400);
		setSize(1024, 768);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		for (final PolygonVertex monotonePolygon : polygonParts) {
			System.out.println(monotonePolygon.allToString());
		}
		System.exit(0);
	}
}