package net.thomas.kata.ugp.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

public class Utils {
	public static <T> Set<T> asSet(@SuppressWarnings("unchecked") T... elements) {
		return stream(elements).collect(toSet());
	}
}
