package net.thomas.kata.geometry.pathfinding.objects;

import static java.lang.Double.NaN;

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

	public boolean isInsidePortal(Line2D.Double other) {
		final double t = determineTFromTwoLines(x1, x2, other.x1, other.x2, y1, y2, other.y1, other.y2);
		return t >= 0 && t < 1;
	}

	public Point2D getBestIntersectionPoint(final Line2D o) {
		double t = NaN;
		if (o instanceof Line2D.Double) {
			final Line2D.Double other = (Line2D.Double) o;
			t = determineTFromTwoLines(x1, x2, other.x1, other.x2, y1, y2, other.y1, other.y2);
		} else if (o instanceof Line2D.Float) {
			final Line2D.Float other = (Line2D.Float) o;
			t = determineTFromTwoLines(x1, x2, other.x1, other.x2, y1, y2, other.y1, other.y2);
		}
		return calculatePointFromT(t);
	}

	private Point2D calculatePointFromT(double t) {
		if (t <= 0.0) {
			return getP1();
		} else if (t >= 1.0) {
			return getP2();
		} else {
			final double x = x1 + t * (x2 - x1);
			final double y = y1 + t * (y2 - y1);
			return new Point2D.Double(x, y);
		}
	}

	private double determineTFromTwoLines(double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4) {
		return ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Portal)) {
			return false;
		}
		final Portal other = (Portal) obj;
		return x1 == other.x1 && x2 == other.x2 && y1 == other.y1 && y2 == other.y2 || x1 == other.x2 && x2 == other.x1 && y1 == other.y2 && y2 == other.y1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (x1 < x2 || x1 == x2 && y1 < y2) {
			result = prime * result + getP1().hashCode();
			result = prime * result + getP2().hashCode();
		} else {
			result = prime * result + getP2().hashCode();
			result = prime * result + getP1().hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return "|" + getP1() + " <-> " + getP2() + "|";
	}
}