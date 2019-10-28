package net.thomas.kata.ugp.engine;

/***
 * 1 tick = 1 microsecond
 */
public abstract class FixedTimeTickableTask {
	protected final int timeBetweenTicks;
	private long nextTick;
	protected boolean shouldRunAgain;

	protected FixedTimeTickableTask(int timeBetweenTicks) {
		this.timeBetweenTicks = timeBetweenTicks;
		shouldRunAgain = true;
	}

	public void initialize(long currentTick) {
		nextTick = currentTick;
	}

	public long getTimeOfNextTick() {
		return nextTick;
	}

	public boolean shouldRunAgain() {
		return shouldRunAgain;
	}

	public void tick() {
		_tick();
		nextTick += timeBetweenTicks;
	}

	protected abstract void _tick();

	public void terminate() {
	}
}