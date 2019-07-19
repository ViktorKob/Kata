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
}