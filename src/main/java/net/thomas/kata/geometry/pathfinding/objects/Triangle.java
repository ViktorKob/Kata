package net.thomas.kata.geometry.pathfinding.objects;

import static java.awt.geom.Path2D.WIND_NON_ZERO;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class Triangle {
	private final Point2D p1;
	private final Point2D p2;
	private final Point2D p3;
	private final GeneralPath trianglePath;

	public Triangle(Point2D p1, Point2D p2, Point2D p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		trianglePath = buildTrianglePath(p1, p2, p3);
	}

	public Point2D getP1() {
		return p1;
	}

	public Point2D getP2() {
		return p2;
	}

	public Point2D getP3() {
		return p3;
	}

	private GeneralPath buildTrianglePath(Point2D p1, Point2D p2, Point2D p3) {
		final GeneralPath trianglePath = new GeneralPath(WIND_NON_ZERO, 3);
		trianglePath.moveTo(p1.getX(), p1.getY());
		trianglePath.lineTo(p2.getX(), p2.getY());
		trianglePath.lineTo(p3.getX(), p3.getY());
		trianglePath.closePath();
		return trianglePath;
	}

	public boolean contains(Point2D point) {
		return trianglePath.contains(point);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (p1 == null ? 0 : p1.hashCode());
		result = prime * result + (p2 == null ? 0 : p2.hashCode());
		result = prime * result + (p3 == null ? 0 : p3.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Triangle)) {
			return false;
		}
		final Triangle other = (Triangle) obj;
		if (p1 == null) {
			if (other.p1 != null) {
				return false;
			}
		} else if (!p1.equals(other.p1)) {
			return false;
		}
		if (p2 == null) {
			if (other.p2 != null) {
				return false;
			}
		} else if (!p2.equals(other.p2)) {
			return false;
		}
		if (p3 == null) {
			if (other.p3 != null) {
				return false;
			}
		} else if (!p3.equals(other.p3)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[" + p1 + ", " + p2 + ", " + p3 + "]";
	}
}
