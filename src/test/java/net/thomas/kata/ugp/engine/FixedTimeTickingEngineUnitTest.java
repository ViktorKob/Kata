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
	private static final int TICKS_BETWEEN_TASK_EXECUTIONS = 5;
	private FixedTimeTickingEngine engine;

	@Before
	public void setUpEngine() {
		engine = new FixedTimeTickingEngine(MINIMUM_TICK_SIZE_IN_MICROSECONDS);
	}

	@Test(timeout = 100000)
	public void shouldDryRunEmptyEngineCorrectly() {
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
	}

	@Test(timeout = 100000)
	public void shouldRunTaskAtLeastTwice() {
		final FakeFixedTimeTickableTask task = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_FOREVER);
		engine.addTask(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 10);
		System.out.println(task.getTimesRun());
		assertTrue("Expected 2 ticks, got " + task.getTimesRun(), task.getTimesRun() >= 2);
	}

	@Test(timeout = 100000)
	public void shouldRunTaskExactlyOnce() {
		final FakeFixedTimeTickableTask task = new FakeFixedTimeTickableTask(TICKS_BETWEEN_TASK_EXECUTIONS, SHOULD_RUN_ONCE);
		engine.addTask(task);
		new Thread(engine).start();
		waitForEngineState(RUNNING, EXECUTION_TIMEOUT);
		sleepSilently(TICKS_BETWEEN_TASK_EXECUTIONS * 10);
		System.out.println(task.getTimesRun());
		assertTrue("Expected 1 tick, got " + task.getTimesRun(), task.getTimesRun() == 1);
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