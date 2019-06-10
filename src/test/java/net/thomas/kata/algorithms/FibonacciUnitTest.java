package net.thomas.kata.algorithms;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.thomas.kata.algorithms.dynamic.DynamicFibonacci;
import net.thomas.kata.algorithms.recursive.RecursiveFibonacci;

@RunWith(Parameterized.class)
public class FibonacciUnitTest {

	private static int[] CORRECT_NUMBERS = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144 };

	@Parameters
	public static Collection<Fibonacci> methods() {
		return asList(new RecursiveFibonacci(), new DynamicFibonacci());
	}

	private final Fibonacci algorithm;

	public FibonacciUnitTest(Fibonacci algorithm) {
		this.algorithm = algorithm;
	}

	@Test
	public void shouldReturnOneForInitialCases() {
		assertEquals(1, algorithm.calculateNthFibonacciNumber(1));
		assertEquals(1, algorithm.calculateNthFibonacciNumber(2));
	}

	@Test
	public void shouldReturnCorrectAnswers() {
		for (int i = 1; i < CORRECT_NUMBERS.length; i++) {
			assertEquals(CORRECT_NUMBERS[i], algorithm.calculateNthFibonacciNumber(i));
		}
	}
}
