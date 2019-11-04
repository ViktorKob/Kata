package net.thomas.kata.ugp.engine;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.CREATED;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.EXITING;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.PAUSED;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.RUNNING;
import static net.thomas.kata.ugp.engine.TickingEngine.EngineState.TERMINATED;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/***
 * 1 tick = 1 microsecond
 ***/
public class TickingEngine implements Runnable {
	public enum EngineState {
		CREATED,
		RUNNING,
		PAUSED,
		EXITING,
		TERMINATED
	};

	public static final boolean MAY_SLEEP_BETWEEN_ITERATIONS = true;
	public static final int DEFAULT_TICKS_PER_ITERATION = 10000;

	private final int ticksPerIteration;
	private EngineState state;
	private final PriorityQueue<TickableTask> tasks;
	private final List<TickableTask> newTasks;
	private long stamp;
	private long currentEngineTick;
	private float currentTickScale;

	private boolean tickScaleIsDirty;
	private final boolean exitWhenDone;

	public TickingEngine(int ticksPerIteration, boolean exitWhenDone) {
		this.ticksPerIteration = ticksPerIteration;
		this.exitWhenDone = exitWhenDone;
		tasks = new PriorityQueue<>((left, right) -> {
			return (int) (left.getTimeOfNextTick() - right.getTimeOfNextTick());
		});
		newTasks = new LinkedList<>();
		currentTickScale = 1;
		tickScaleIsDirty = true;
		state = CREATED;
	}

	public synchronized void addTasks(TickableTask... tasks) {
		synchronized (newTasks) {
			for (final TickableTask task : tasks) {
				newTasks.add(task);
			}
		}
	}

	public void setTickScale(float tickScale) {
		currentTickScale = tickScale;
		tickScaleIsDirty = true;
	}

	@Override
	public void run() {
		if (state == TERMINATED) {
			return;
		}
		state = RUNNING;
		currentEngineTick = 0;
		stamp = now();
		while (state != EXITING) {
			while (state == RUNNING) {
				final long now = now();
				final long deltaTicks = now - stamp;
				if (deltaTicks > ticksPerIteration) {
					currentEngineTick += ticksPerIteration;
					updateTickScaleIfNecessary(currentTickScale);
					includeNewTasksIfAny(currentEngineTick, currentTickScale);
					iterateTasks();
					stamp += ticksPerIteration;
				} else {
					if (MAY_SLEEP_BETWEEN_ITERATIONS) {
						sleepSilently();
					}
				}
			}
			while (state == PAUSED) {
				sleepSilently();
				stamp = now();
			}
		}
		terminateTasks();
		state = TERMINATED;
	}

	private void updateTickScaleIfNecessary(float currentTickScale) {
		if (tickScaleIsDirty) {
			for (final TickableTask task : tasks) {
				task.setTickScale(currentTickScale);
			}
		}
	}

	private void includeNewTasksIfAny(long currentEngineTick, float tickScale) {
		if (newTasks.size() > 0) {
			synchronized (newTasks) {
				newTasks.stream().forEachOrdered(task -> {
					task.initialize(this, currentEngineTick, tickScale);
					tasks.add(task);
				});
				newTasks.clear();
			}
		}
	}

	private void iterateTasks() {
		while (tasks.size() > 0 && tasks.peek().getTimeOfNextTick() <= currentEngineTick) {
			final TickableTask task = tasks.poll();
			task.tick();
			if (task.shouldRunAgain()) {
				tasks.add(task);
			} else {
				task.terminate();
				if (exitWhenDone && tasks.size() == 0 && newTasks.size() == 0) {
					stop();
				}
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
		for (final TickableTask task : tasks) {
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