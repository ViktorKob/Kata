package net.thomas.kata.geometry;

import java.util.Collection;

import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.objects.PolygonVertex;
import net.thomas.kata.geometry.objects.PortalGraphNodeV2;

public interface PolygonUtil {
	public Collection<PolygonVertex> getMonotoneParts(Collection<PolygonVertex> polygons);

	public Collection<PolygonTriangle> triangulateMonotonePolygons(Collection<PolygonVertex> monotonePolygons);

	public Collection<PortalGraphNodeV2> buildPortalGraphs(Collection<PolygonTriangle> triangleGraphs);
}
