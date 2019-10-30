package net.thomas.kata.ugp.world.entity.vehicle;

import java.util.Collection;

import net.thomas.kata.ugp.world.entity.Entity;
import net.thomas.kata.ugp.world.terrain.TerrainType;

public interface Vehicle extends Entity {
	public enum QueryType {
		IDEAL,
		CURRENT
	};

	/***
	 * @return Speed in m/s traversing current terrain
	 */
	double getMaximumSpeed(TerrainType terrain, QueryType type);

	/***
	 * @return Speed in m/s
	 */
	double getSpeed(QueryType type);

	/***
	 * @return Range in m traversing current terrain
	 */
	double getRange(TerrainType terrain, QueryType type);

	boolean canTraverse(TerrainType type);

	/***
	 * @return Cargo capacity in Kg
	 */
	double getCargoCapacity(QueryType type);

	/***
	 * @return Vehicles transported by this vehicle
	 */
	Collection<TransportationLink> getTransportedVehicles();
}
