package net.thomas.kata.ugp.engine;

/***
 * 1 tick = 1 microsecond
 */
public abstract class TickableTask {
	protected final int ticksPerTaskIteration;
	protected TickingEngine engine;
	private long nextTick;
	protected boolean shouldRunAgain;
	private float tickScale;

	protected TickableTask(int ticksPerTaskIteration) {
		this.ticksPerTaskIteration = ticksPerTaskIteration;
		shouldRunAgain = true;
	}

	public void initialize(TickingEngine engine, long currentTick, float tickScale) {
		this.engine = engine;
		nextTick = currentTick;
		this.tickScale = tickScale;
	}

	public void setTickScale(float tickScale) {
		this.tickScale = tickScale;
	}

	public long getTimeOfNextTick() {
		return nextTick;
	}

	public boolean shouldRunAgain() {
		return shouldRunAgain;
	}

	public void tick() {
		final int iterationTicks = (int) (ticksPerTaskIteration * tickScale);
		_tick(iterationTicks);
		nextTick += iterationTicks;
	}

	protected abstract void _tick(int iterationTicks);

	public void terminate() {
	}
}