package net.thomas.kata.ugp.world.entity.vehicle;

import net.thomas.kata.ugp.world.entity.Entity;

public interface TransportationLink {
	Vehicle getVehicle();

	Entity getTransporter();

	boolean getReadyForUse();
}
