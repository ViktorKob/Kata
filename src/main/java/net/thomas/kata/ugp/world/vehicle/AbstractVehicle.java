package net.thomas.kata.ugp.world.vehicle;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import net.thomas.kata.ugp.world.room.Room;
import net.thomas.kata.ugp.world.terrain.TerrainType;

public abstract class AbstractVehicle implements Vehicle {
	private final Set<TerrainType> passableTerrain;
	protected final Collection<Room> rooms;
	protected final Collection<TransportationLink> vehiclesOnBoard;

	public AbstractVehicle(Set<TerrainType> passableTerrain, Collection<Room> rooms) {
		this.passableTerrain = passableTerrain;
		this.rooms = rooms;
		vehiclesOnBoard = new LinkedList<>();
	}

	@Override
	public boolean canTraverse(TerrainType type) {
		return passableTerrain.contains(type);
	}

	@Override
	public Collection<TransportationLink> getVehiclesOnBoard() {
		return unmodifiableCollection(vehiclesOnBoard);
	}
}