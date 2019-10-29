package net.thomas.kata.ugp.engine;

import static java.lang.System.currentTimeMillis;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.RUNNING;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.TERMINATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.ugp.engine.TickingEngine.EngineState;

public class TickingEngineUnitTest {
	private static final int SOME_TIME_SCALE = 2;
	private static final int EXECUTION_TIMEOUT = 10;
	private static final int SHUTDOWN_TIMEOUT = 1000;
	private static final boolean SHOULD_RUN_FOREVER = true;
	private static final boolean SHOULD_RUN_ONCE = false;
	private static final int MINIMUM_TICK_SIZE_IN_MICROSECONDS = 100;
	private static final int TICKS_BETWEEN_TASK_EXECUTIONS = 100;
	private TickingEngine engine;

	@Before
	public void setUpEngine() {
		engine = new TickingEngine(MINIMUM_TICK_SIZE_IN_MICROSECONDS);
	}

	@Test(timeout = 1000)
	public void shouldDryRunEmptyEngineCorrectly() {
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
	}

	@Test(timeout = 1000)
	public void shouldRunTaskAtLeastTwice() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected at least 2 ticks, got " + task.getTimesRun(), task.getTimesRun() >= 2);
	}

	@Test(timeout = 2000)
	public void shouldTerminateTaskAfterEngineTermination() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS);
		engine.stop();
		waitForEngineState(TERMINATED, SHUTDOWN_TIMEOUT);
		assertTrue("Task failed to terminate after engine shutdown", task.isTerminated());
	}

	@Test(timeout = 1000)
	public void shouldTerminateTaskAfterExecution() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS);
		assertTrue("Task failed to terminate after task execution", task.isTerminated());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskExactlyOnce() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected 1 tick, got " + task.getTimesRun(), task.getTimesRun() == 1);
	}

	@Test(timeout = 1000)
	public void shouldRunMultipleTasksAtLeastTwiceEach() {
		final TimeTickableTask task1 = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task2 = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task1);
		engine.addTasks(task2);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected at least 2 ticks for task 1, got " + task1.getTimesRun(), task1.getTimesRun() >= 2);
		assertTrue("Expected at least 2 ticks for task 2, got " + task2.getTimesRun(), task2.getTimesRun() >= 2);
	}

	@Test(timeout = 1000)
	public void shouldRunMultipleTasksAtDifferentRates() {
		final TimeTickableTask task1 = new TimeTickableTask(1 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task2 = new TimeTickableTask(2 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final TimeTickableTask task3 = new TimeTickableTask(3 * TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task1);
		engine.addTasks(task2);
		engine.addTasks(task3);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Task 1 should have been run more often than task 2, was (" + task1.getTimesRun() + ", " + task2.getTimesRun() + ")",
				task1.getTimesRun() > task2.getTimesRun());
		assertTrue("Task 2 should have been run more often than task 3, was (" + task2.getTimesRun() + ", " + task3.getTimesRun() + ")",
				task2.getTimesRun() > task3.getTimesRun());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskForCorrectNumberOfTicks() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertEquals(task.getTimesRun() * TICKS_BETWEEN_TASK_EXECUTIONS, task.getTotalTicks());
	}

	@Test(timeout = 1000)
	public void shouldRunTaskForCorrectNumberOfTicksWhenScaled() {
		final TimeTickableTask task = new TimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task);
		engine.setTickScale(SOME_TIME_SCALE);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertEquals(task.getTimesRun() * TICKS_BETWEEN_TASK_EXECUTIONS * SOME_TIME_SCALE, task.getTotalTicks());
	}

	@After
	public void shutDownEngine() {
		if (engine.getCurrentState() != TERMINATED) {
			engine.stop();
			waitForEngineState(TERMINATED, SHUTDOWN_TIMEOUT);
		}
	}

	private void waitForEngineState(EngineState state, int timeoutInMilliseconds) {
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

	private void sleepSilently(int timeInMicroseconds) {
		try {
			Thread.sleep(timeInMicroseconds / 1000, timeInMicroseconds % 1000 * 1000);
		} catch (final InterruptedException e) {
		}
	}
}