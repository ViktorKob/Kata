package net.thomas.kata.ugp.engine;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.CREATED;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.EXITING;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.INITIALIZING;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.RUNNING;
import static net.thomas.kata.ugp.engine.FixedTimeTickingEngine.EngineState.TERMINATED;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/***
 * 1 tick = 1 microsecond
 ***/
public class FixedTimeTickingEngine implements Runnable {
	public enum EngineState {
		CREATED,
		INITIALIZING,
		RUNNING,
		PAUSED,
		EXITING,
		TERMINATED
	};

	public static final boolean MAY_SLEEP_BETWEEN_ITERATIONS = true;

	private final int ticksPerIteration;
	private EngineState state;
	private final PriorityQueue<FixedTimeTickableTask> tasks;
	private final List<FixedTimeTickableTask> newTasks;
	private long stamp;
	private long currentEngineTick;

	public FixedTimeTickingEngine(int ticksPerIteration) {
		this.ticksPerIteration = ticksPerIteration;
		tasks = new PriorityQueue<>((left, right) -> {
			return (int) (left.getTimeOfNextTick() - right.getTimeOfNextTick());
		});
		newTasks = new LinkedList<>();
		state = CREATED;
	}

	public synchronized void addTask(FixedTimeTickableTask task) {
		synchronized (newTasks) {
			newTasks.add(task);
		}
	}

	@Override
	public void run() {
		if (state == TERMINATED) {
			return;
		}
		state = INITIALIZING;
		currentEngineTick = 0;
		initializeTasks(currentEngineTick);
		state = RUNNING;
		stamp = now();
		while (state != EXITING) {
			while (state == RUNNING) {
				final long now = now();
				final long deltaTicks = now - stamp;
				if (deltaTicks > ticksPerIteration) {
					currentEngineTick += ticksPerIteration;
					includeNewTasksIfAny(currentEngineTick);
					iterateTasks();
				} else {
					if (MAY_SLEEP_BETWEEN_ITERATIONS) {
						sleepSilently();
					}
				}
				stamp = now;
			}
			sleepSilently();
		}
		terminateTasks();
		state = TERMINATED;
	}

	private void initializeTasks(long currentEngineTick) {
		for (final FixedTimeTickableTask task : tasks) {
			task.initialize(currentEngineTick);
		}
	}

	private void includeNewTasksIfAny(long currentEngineTick) {
		if (newTasks.size() > 0) {
			synchronized (newTasks) {
				for (final FixedTimeTickableTask task : newTasks) {
					task.initialize(currentEngineTick);
					tasks.add(task);
				}
				newTasks.clear();
			}
		}
	}

	private void iterateTasks() {
		while (tasks.size() > 0 && tasks.peek().getTimeOfNextTick() <= currentEngineTick) {
			final FixedTimeTickableTask task = tasks.poll();
			task.tick();
			if (task.shouldRunAgain()) {
				tasks.add(task);
			} else {
				task.terminate();
			}
		}
	}

	private void sleepSilently() {
		try {
			sleep(0, 0);
		} catch (final InterruptedException e) {
		}
	}

	private void terminateTasks() {
		for (final FixedTimeTickableTask task : tasks) {
			task.terminate();
		}
	}

	public EngineState getCurrentState() {
		return state;
	}

	private long now() {
		return nanoTime() / 1000;
	}

	public void stop() {
		state = EXITING;
	}
}