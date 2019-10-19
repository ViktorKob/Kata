package net.thomas.kata.ugp.world.vehicle.water;

import static java.util.Arrays.asList;
import static net.thomas.kata.ugp.util.Utils.asSet;
import static net.thomas.kata.ugp.world.terrain.TerrainType.COAST;
import static net.thomas.kata.ugp.world.terrain.TerrainType.RIVER;
import static net.thomas.kata.ugp.world.terrain.TerrainType.SEA;

import net.thomas.kata.ugp.world.vehicle.AbstractVehicle;

public class CargoShip extends AbstractVehicle {
	public CargoShip() {
		super(asSet(SEA, COAST, RIVER), asList());
	}

	@Override
	public double getMaximumSpeed(QueryType type) {
		return 0;
	}

	@Override
	public double getSpeed(QueryType type) {
		return 0;
	}

	@Override
	public double getRange(QueryType type) {
		return 0;
	}

	@Override
	public double getCargoCapacity(QueryType type) {
		return 0;
	}
}