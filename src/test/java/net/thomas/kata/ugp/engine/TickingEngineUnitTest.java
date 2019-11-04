package net.thomas.kata.ugp.engine;

import static java.lang.System.currentTimeMillis;
import static net.thomas.kata.ugp.engine.TickingEngine.DEFAULT_TICKS_PER_ITERATION;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.CREATED;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.RUNNING;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.TERMINATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.ugp.engine.TickingEngine.EngineState;

public class TickingEngineUnitTest {
	private static final boolean KEEP_RUNNING_WHEN_DONE = false;
	private static final boolean EXIT_WHEN_DONE = true;
	private static final int SOME_TIME_SCALE = 2;
	private static final int EXECUTION_TIMEOUT = 10;
	private static final int SHUTDOWN_TIMEOUT = 1000;
	private static final boolean SHOULD_RUN_FOREVER = true;
	private static final boolean SHOULD_RUN_ONCE = false;
	private static final int MINIMUM_TICK_SIZE_IN_MICROSECONDS = 100;
	private static final int TICKS_BETWEEN_TASK_EXECUTIONS = 100;
	private TickingEngine continuousEngine;
	private TickingEngine onceOffEngine;

	@Before
	public void setUpEngine() {
		continuousEngine = new TickingEngine(MINIMUM_TICK_SIZE_IN_MICROSECONDS, KEEP_RUNNING_WHEN_DONE);
		onceOffEngine = new TickingEngine(MINIMUM_TICK_SIZE_IN_MICROSECONDS, EXIT_WHEN_DONE);
	}

	@Test(timeout = 1000)
	public void shouldDryRunEmptyEngineCorrectly() {
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
	}

	@Test(timeout = 1000)
	public void shouldRunTaskAtLeastTwice() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected at least 2 ticks, got " + task.getTimesRun(), task.getTimesRun() >= 2);
	}

	@Test(timeout = 2000)
	public void shouldTerminateTaskAfterEngineTermination() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS);
		continuousEngine.stop();
		waitForEngineState(continuousEngine, TERMINATED, SHUTDOWN_TIMEOUT);
		assertTrue("Task failed to terminate after engine shutdown", task.isTerminated());
	}

	@Test(timeout = 1000)
	public void shouldTerminateTaskAfterExecution() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		continuousEngine.addTasks(task);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS);
		assertTrue("Task failed to terminate after task execution", task.isTerminated());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskExactlyOnce() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		continuousEngine.addTasks(task);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected 1 tick, got " + task.getTimesRun(), task.getTimesRun() == 1);
	}

	@Test(timeout = 1000)
	public void shouldExecuteCountdownTaskFullyThenShutDown() {
		new Thread(onceOffEngine).start();
		waitForEngineState(onceOffEngine, RUNNING, EXECUTION_TIMEOUT);
		final CountDownTask task = new CountDownTask(2);
		onceOffEngine.addTasks(task);
		waitForEngineState(onceOffEngine, TERMINATED, SHUTDOWN_TIMEOUT);
		assertEquals(0, task.getCount());
	}

	@Test(timeout = 1000)
	public void shouldExecuteSubTaskFully() {
		new Thread(onceOffEngine).start();
		waitForEngineState(onceOffEngine, RUNNING, EXECUTION_TIMEOUT);
		final TaskSpawningTask task = new TaskSpawningTask(2);
		onceOffEngine.addTasks(task);
		waitForEngineState(onceOffEngine, TERMINATED, SHUTDOWN_TIMEOUT);
		assertEquals(0, task.getCount());
	}

	@Test(timeout = 1000)
	public void shouldRunMultipleTasksAtLeastTwiceEach() {
		final TimeTickableTask task1 = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task2 = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task1);
		continuousEngine.addTasks(task2);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 200);
		assertTrue("Expected at least 2 ticks for task 1, got " + task1.getTimesRun(), task1.getTimesRun() >= 2);
		assertTrue("Expected at least 2 ticks for task 2, got " + task2.getTimesRun(), task2.getTimesRun() >= 2);
	}

	@Test(timeout = 1000)
	public void shouldRunMultipleTasksAtDifferentRates() {
		final TimeTickableTask task1 = new TimeTickableTask(1 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task2 = new TimeTickableTask(2 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task3 = new TimeTickableTask(3 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task1);
		continuousEngine.addTasks(task2);
		continuousEngine.addTasks(task3);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Task 1 should have been run more often than task 2, was (" + task1.getTimesRun() + ", " + task2.getTimesRun() + ")",
				task1.getTimesRun() > task2.getTimesRun());
		assertTrue("Task 2 should have been run more often than task 3, was (" + task2.getTimesRun() + ", " + task3.getTimesRun() + ")",
				task2.getTimesRun() > task3.getTimesRun());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskForCorrectNumberOfTicks() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertEquals(task.getTimesRun() * TICKS_BETWEEN_TASK_EXECUTIONS, task.getTotalTicks());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskForCorrectNumberOfTicksWhenScaled() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		continuousEngine.addTasks(task);
		continuousEngine.setTickScale(SOME_TIME_SCALE);
		new Thread(continuousEngine).start();
		waitForEngineState(continuousEngine, RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertEquals(task.getTimesRun() * TICKS_BETWEEN_TASK_EXECUTIONS * SOME_TIME_SCALE, task.getTotalTicks());
	}

	@After
	public void shutDownEngines() {
		shutDownEngineIfRunning(continuousEngine);
		shutDownEngineIfRunning(onceOffEngine);
	}

	private void shutDownEngineIfRunning(TickingEngine engine) {
		if (engine.getCurrentState() != CREATED && engine.getCurrentState() != TERMINATED) {
			engine.stop();
			waitForEngineState(engine, TERMINATED, SHUTDOWN_TIMEOUT);
		}
	}

	private void waitForEngineState(TickingEngine engine, EngineState state, int timeoutInMilliseconds) {
		final long stamp = currentTimeMillis();
		while (engine.getCurrentState() != state) {
			if (stamp + timeoutInMilliseconds < currentTimeMillis()) {
				System.err.println("Reached timeout (" + timeoutInMilliseconds + " ms) waiting for state " + state + " in " + TickableTask.class.getSimpleName()
						+ " during test");
				break;
			} else {
				sleepSilently(0);
			}
		}
	}

	private static class TimeTickableTask extends TickableTask {
		private int timesRun;
		private int totalTicks;
		private boolean terminated;

		public TimeTickableTask(int timeInTicksBetweenIterations, boolean shouldRunAgain) {
			super(timeInTicksBetweenIterations);
			this.shouldRunAgain = shouldRunAgain;
			timesRun = 0;
			totalTicks = 0;
			terminated = false;
		}

		@Override
		protected void _tick(int ticks) {
			timesRun++;
			totalTicks += ticks;
		}

		public int getTimesRun() {
			return timesRun;
		}

		public int getTotalTicks() {
			return totalTicks;
		}

		@Override
		public void terminate() {
			terminated = true;
		}

		public boolean isTerminated() {
			return terminated;
		}
	}

	private static class CountDownTask extends TickableTask {
		private int count;

		public CountDownTask(int count) {
			super(DEFAULT_TICKS_PER_ITERATION);
			this.count = count;
		}

		@Override
		public boolean shouldRunAgain() {
			return count > 0;
		}

		@Override
		protected void _tick(int iterationTicks) {
			count--;
		}

		public int getCount() {
			return count;
		}
	}

	private static class TaskSpawningTask extends TickableTask {
		private final CountDownTask task;
		private boolean hasSpawnedTask;

		public TaskSpawningTask(int count) {
			super(DEFAULT_TICKS_PER_ITERATION);
			task = new CountDownTask(count);
			hasSpawnedTask = false;
		}

		@Override
		public boolean shouldRunAgain() {
			return !hasSpawnedTask;
		}

		@Override
		protected void _tick(int iterationTicks) {
			engine.addTasks(task);
			hasSpawnedTask = true;
		}

		public int getCount() {
			return task.getCount();
		}
	}

	private void sleepSilently(int timeInMicroseconds) {
		try {
			Thread.sleep(timeInMicroseconds / 1000, timeInMicroseconds % 1000 * 1000);
		} catch (final InterruptedException e) {
		}
	}
}