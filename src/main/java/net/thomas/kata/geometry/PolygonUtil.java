package net.thomas.kata.geometry;

import java.util.Collection;

import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.objects.PolygonVertex;

public interface PolygonUtil {
	public Collection<PolygonVertex> getMonotoneParts(Collection<PolygonVertex> polygons);

	public Collection<PolygonTriangle> triangulateMonotonePolygons(Collection<PolygonVertex> monotonePolygons);
}
