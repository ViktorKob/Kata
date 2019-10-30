package net.thomas.kata.ugp.world.entity.geography;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Location {
	private final Coordinates center;
	private final List<Area> areas;
}