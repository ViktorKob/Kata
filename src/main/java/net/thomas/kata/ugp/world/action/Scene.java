package net.thomas.kata.ugp.world.action;

import net.thomas.kata.ugp.engine.TickableTask;
import net.thomas.kata.ugp.engine.TickingEngine;

public abstract class Scene implements Runnable {
	private static final int HUNDRED_FPS = 10000;
	private static final boolean EXIT_WHEN_DONE = true;
	private final TickingEngine engine;

	public Scene() {
		engine = new TickingEngine(HUNDRED_FPS, EXIT_WHEN_DONE);
	}

	protected void addTasks(TickableTask... tasks) {
		engine.addTasks(tasks);
	}

	@Override
	public void run() {
		engine.run();
	}

	protected void markCompleted() {
		engine.stop();
	}
}
