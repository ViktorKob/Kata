package net.thomas.kata.patterns.concurrency;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class MonitorExample {

	public static void main(String[] args) throws InterruptedException {
		final ExampleRunner runner = new ExampleRunner();
		final Example raceConditionExample = new RaceConditionExample();
		final Example correctExample = new CorrectExample();
		for (int i = 0; i < 100; i++) {
			runner.addExampleTask(i, raceConditionExample);
			runner.addExampleTask(i, correctExample);
		}
		runner.execute();
		System.out.println("Hello, Incorrect Sum: " + ((RaceConditionExample) raceConditionExample).getValue());
		System.out.println("Hello, Correct Sum: " + ((CorrectExample) correctExample).getValue());
	}

	static class ExampleRunner {
		private final ExecutorService executor;
		private final CountDownLatch startLatch;
		private final CountDownLatch endLatch;

		public ExampleRunner() {
			executor = newFixedThreadPool(200);
			startLatch = new CountDownLatch(200);
			endLatch = new CountDownLatch(200);
		}

		public void execute() throws InterruptedException {
			endLatch.await();
		}

		public void addExampleTask(Integer value, Example example) {
			executor.execute(() -> {
				startLatch.countDown();
				try {
					startLatch.await();
					example.add(value);
				} catch (final InterruptedException | IllegalArgumentException e) {
					e.printStackTrace();
				}
				endLatch.countDown();
			});
		}
	}

	@FunctionalInterface
	static interface Example {
		void add(Integer value) throws InterruptedException;
	}

	static class RaceConditionExample implements Example {
		private int value;

		public RaceConditionExample() {
			value = 0;
		}

		@Override
		public void add(Integer value) throws InterruptedException {
			final int sum = this.value;
			Thread.sleep(1);
			this.value = sum + value;
		}

		public int getValue() {
			return value;
		}
	}

	static class CorrectExample extends RaceConditionExample {
		@Override
		public synchronized void add(Integer value) throws InterruptedException {
			super.add(value);
		}
	}
}
