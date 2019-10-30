package net.thomas.kata.ugp.world.entity.geography;

import java.util.List;

import net.thomas.kata.ugp.world.terrain.TerrainType;

public interface Area {

	AreaType getType();

	boolean isAccessibleFrom(TerrainType terrain);

	List<Area> getNeighbours();
}
