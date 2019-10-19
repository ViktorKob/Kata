package net.thomas.kata.ugp.world.vehicle;

public interface TransportationLink {
	Vehicle getVehicle();

	Vehicle getCarrier();

	boolean getReadyForUse();
}
