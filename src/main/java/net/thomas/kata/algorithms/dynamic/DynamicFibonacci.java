package net.thomas.kata.algorithms.dynamic;

import net.thomas.kata.algorithms.Fibonacci;

public class DynamicFibonacci implements Fibonacci {

	@Override
	public int calculateNthFibonacciNumber(int n) {
		if (n <= 2) {
			return 1;
		} else {
			final int[] numbers = calculateFibonacciToN(n);
			return numbers[n];
		}
	}

	private int[] calculateFibonacciToN(int n) {
		final int[] numbers = new int[n + 1];
		numbers[1] = 1;
		numbers[2] = 1;
		for (int i = 3; i < numbers.length; i++) {
			numbers[i] = numbers[i - 1] + numbers[i - 2];
		}
		return numbers;
	}
}