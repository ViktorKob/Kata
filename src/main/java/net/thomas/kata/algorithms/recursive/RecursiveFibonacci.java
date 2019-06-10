package net.thomas.kata.algorithms.recursive;

import net.thomas.kata.algorithms.Fibonacci;

public class RecursiveFibonacci implements Fibonacci {
	@Override
	public int calculateNthFibonacciNumber(int n) {
		if (n <= 2) {
			return 1;
		} else {
			return calculateNthFibonacciNumber(n - 1) + calculateNthFibonacciNumber(n - 2);
		}
	}
}