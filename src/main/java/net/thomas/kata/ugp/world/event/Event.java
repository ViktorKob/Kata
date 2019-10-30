package net.thomas.kata.ugp.world.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thomas.kata.ugp.world.entity.geography.Location;
import net.thomas.kata.ugp.world.entity.person.Person;

@Getter
@AllArgsConstructor
public class Event {
	public final Location location;
	public final List<Person> participants;
}