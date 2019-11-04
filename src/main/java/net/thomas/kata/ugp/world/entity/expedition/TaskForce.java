package net.thomas.kata.ugp.world.entity.expedition;

import java.util.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thomas.kata.ugp.world.entity.vehicle.Vehicle;

@Setter
@Getter
@NoArgsConstructor
public class TaskForce {
	Collection<Vehicle> vehicles;
	
	public static void main(String[] args) {
		float health = 122525.0f;
		float repairAmount = 0.95f;
		System.out.println(health + repairAmount);
	}
}