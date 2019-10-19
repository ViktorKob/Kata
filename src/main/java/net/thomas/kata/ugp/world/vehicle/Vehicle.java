package net.thomas.kata.ugp.world.vehicle;

import java.util.Collection;

import net.thomas.kata.ugp.world.terrain.TerrainType;

public interface Vehicle {
	public enum QueryType {
		IDEAL,
		CURRENT
	};

	/***
	 * @return Speed in m/s
	 */
	double getMaximumSpeed(QueryType type);

	/***
	 * @return Speed in m/s
	 */
	double getSpeed(QueryType type);

	/***
	 * @return Range in m
	 */
	double getRange(QueryType type);

	boolean canTraverse(TerrainType type);

	/***
	 * @return Cargo capacity in Kg
	 */
	double getCargoCapacity(QueryType type);

	/***
	 * Vehicles transported by this vehicle
	 *
	 * @return
	 */
	Collection<TransportationLink> getVehiclesOnBoard();
}
