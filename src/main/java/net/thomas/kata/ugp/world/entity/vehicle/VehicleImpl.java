package net.thomas.kata.ugp.world.entity.vehicle;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.thomas.kata.ugp.world.entity.room.Room;
import net.thomas.kata.ugp.world.terrain.TerrainType;

public class VehicleImpl implements Vehicle {
	private final Set<TerrainType> passableTerrain;
	protected final Collection<Room> rooms;
	protected final Collection<TransportationLink> vehicles;
	private final VehicleStats stats;
	private final List<Engine> engines;

	public VehicleImpl(VehicleStats stats, Set<TerrainType> passableTerrain, List<Engine> engines) {
		this.stats = stats;
		this.passableTerrain = passableTerrain;
		this.engines = engines;
		rooms = new LinkedList<>();
		vehicles = new LinkedList<>();
	}

	@Override
	public boolean canTraverse(TerrainType type) {
		return passableTerrain.contains(type);
	}

	@Override
	public Collection<TransportationLink> getTransportedVehicles() {
		return unmodifiableCollection(vehicles);
	}

	@Override
	public double getMaximumSpeed(TerrainType terrain, QueryType type) {
		return 0;
	}

	@Override
	public double getSpeed(QueryType type) {
		return 0;
	}

	@Override
	public double getRange(TerrainType terrain, QueryType type) {
		return 0;
	}

	@Override
	public double getCargoCapacity(QueryType type) {
		return 0;
	}
}