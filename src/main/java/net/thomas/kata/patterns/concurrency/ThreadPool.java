package net.thomas.kata.patterns.concurrency;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class ThreadPool {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final ExecutorService executor = newFixedThreadPool(10);
		executor.execute(() -> {
			System.out.println("Hello, Runnable World!");
		});
		System.out.println(executor.submit(() -> {
			return "Hello, Callable World!";
		}).get());
		executor.shutdown();
	}
}
