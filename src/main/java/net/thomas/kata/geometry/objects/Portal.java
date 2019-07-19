package net.thomas.kata.geometry.objects;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Portal extends Line2D.Double {
	private static final long serialVersionUID = 1L;

	public Portal(Point2D boundaryLeft, Point2D boundaryRight) {
		super(boundaryLeft, boundaryRight);
	}

	public Point2D getCenter() {
		return new Point2D.Double((getP1().getX() + getP2().getX()) / 2, (getP1().getY() + getP2().getY()) / 2);
	}

	public Point2D getBestIntersectionPoint(final Line2D.Double other) {
		final double t = determineT(x1, x2, other.x1, other.x2, y1, y2, other.y1, other.y2);
		if (t < 0) {
			return getP1();
		} else if (t > 1) {
			return getP2();
		} else {
			final double x = x1 + t * (x2 - x1);
			final double y = y1 + t * (y2 - y1);
			return new Point2D.Double(x, y);
		}
	}

	private double determineT(double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4) {
		return ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
	}
}