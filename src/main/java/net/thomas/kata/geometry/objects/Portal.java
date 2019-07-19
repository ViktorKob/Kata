package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;

public class Portal {

	private final Point2D boundaryLeft;
	private final Point2D boundaryRight;

	public Portal(Point2D boundaryLeft, Point2D boundaryRight) {
		this.boundaryLeft = boundaryLeft;
		this.boundaryRight = boundaryRight;
	}

	public Point2D getCenter() {
		return new Point2D.Double((boundaryLeft.getX() + boundaryRight.getX()) / 2, (boundaryLeft.getY() + boundaryRight.getY()) / 2);
	}
}