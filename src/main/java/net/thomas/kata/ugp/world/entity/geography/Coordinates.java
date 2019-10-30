package net.thomas.kata.ugp.world.entity.geography;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinates {
	private final double longitude;
	private final double latitude;
	private final double altitude;
}