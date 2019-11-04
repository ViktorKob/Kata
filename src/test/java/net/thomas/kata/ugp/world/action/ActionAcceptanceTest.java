package net.thomas.kata.ugp.world.action;

import static net.thomas.kata.ugp.engine.TickingEngine.DEFAULT_TICKS_PER_ITERATION;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import net.thomas.kata.ugp.engine.TickableTask;

public class ActionAcceptanceTest {

	@Test(timeout = 1000)
	public void shouldExecuteTaskFully() {
		final CountingTask task = new CountingTask(2);
		final FakeAction action = new FakeAction(task);
		action.run();
		assertEquals(2, task.getCount());
	}

	@Ignore
	@Test(timeout = 1000)
	public void shouldExecuteSubTaskFully() {
		final TaskSpawningTask task = new TaskSpawningTask(2);
		final FakeAction action = new FakeAction(task);
		action.run();
		assertEquals(2, task.getCount());
	}

	private static class FakeAction extends Action {
		public FakeAction(TickableTask... tasks) {
			addTasks(tasks);
		}
	}

	private static class CountingTask extends TickableTask {
		private final int targetCount;
		private int count;

		public CountingTask(int targetCount) {
			super(DEFAULT_TICKS_PER_ITERATION);
			this.targetCount = targetCount;
			count = 0;
		}

		@Override
		public boolean shouldRunAgain() {
			return count < targetCount;
		}

		@Override
		protected void _tick(int iterationTicks) {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	private static class TaskSpawningTask extends TickableTask {
		private final CountingTask task;
		private boolean hasSpawnedTask;

		public TaskSpawningTask(int targetCount) {
			super(DEFAULT_TICKS_PER_ITERATION);
			task = new CountingTask(targetCount);
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
}
