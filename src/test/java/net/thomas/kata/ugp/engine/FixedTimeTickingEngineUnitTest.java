package net.thomas.kata.ugp.engine;

import static java.lang.System.currentTimeMillis;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.RUNNING;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.TERMINATED;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState;

public class FixedTimeTickingEngineUnitTest {
	private static final int EXECUTION_TIMEOUT = 10;
	private static final int SHUTDOWN_TIMEOUT = 1000;
	private static final boolean SHOULD_RUN_FOREVER = true;
	private static final boolean SHOULD_RUN_ONCE = false;
	private static final int MINIMUM_TICK_SIZE_IN_MICROSECONDS = 100;
	private static final int TICKS_BETWEEN_TASK_EXECUTIONS = 100;
	private FixedTimeTickingEngine engine;

	@Before
	public void setUpEngine() {
		engine = new FixedTimeTickingEngine(MINIMUM_TICK_SIZE_IN_MICROSECONDS);
	}

	@Test(timeout = 1000)
	public void shouldDryRunEmptyEngineCorrectly() {
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
	}

	@Test(timeout = 1000)
	public void shouldRunTaskAtLeastTwice() {
		final FakeFixedTimeTickableTask task = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected at least 2 ticks, got " + task.getTimesRun(), task.getTimesRun() >= 2);
	}

	@Test(timeout = 1000)
	public void shouldRunTaskExactlyOnce() {
		final FakeFixedTimeTickableTask task = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		engine.addTasks(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 100);
		assertTrue("Expected 1 tick, got " + task.getTimesRun(), task.getTimesRun() == 1);
	}

	@Test(timeout = 1000)
	public void shouldRunMultipleTasksAtLeastTwiceEach() {
		final FakeFixedTimeTickableTask task1 = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final FakeFixedTimeTickableTask task2 = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
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
		final FakeFixedTimeTickableTask task1 = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		final FakeFixedTimeTickableTask task2 = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS * 2, SHOULD_RUN_FOREVER);
		final FakeFixedTimeTickableTask task3 = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS * 3, SHOULD_RUN_FOREVER);
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
		System.out.println(task1.getTimesRun());
		System.out.println(task2.getTimesRun());
		System.out.println(task3.getTimesRun());
	}

	@After
	public void shutDownEngine() {
		engine.stop();
		waitForEngineState(TERMINATED, SHUTDOWN_TIMEOUT);
	}

	private void waitForEngineState(EngineState state, int timeoutInMilliseconds) {
		final long stamp = currentTimeMillis();
		while (engine.getCurrentState() != state) {
			if (stamp + timeoutInMilliseconds < currentTimeMillis()) {
				System.err.println("Reached timeout (" + timeoutInMilliseconds + " ms) waiting for state " + state + " in "
						+ FixedTimeTickableTask.class.getSimpleName() + " during test");
				break;
			} else {
				sleepSilently(0);
			}
		}
	}

	private static class FakeFixedTimeTickableTask extends FixedTimeTickableTask {
		private int timesRun;

		public FakeFixedTimeTickableTask(int timeInTicksBetweenIterations, boolean shouldRunAgain) {
			super(timeInTicksBetweenIterations);
			this.shouldRunAgain = shouldRunAgain;
			timesRun = 0;
		}

		@Override
		protected void _tick() {
			timesRun++;
		}

		public int getTimesRun() {
			return timesRun;
		}
	}

	private void sleepSilently(int timeInMicroseconds) {
		try {
			Thread.sleep(timeInMicroseconds / 1000, timeInMicroseconds % 1000 * 1000);
		} catch (final InterruptedException e) {
		}
	}
}