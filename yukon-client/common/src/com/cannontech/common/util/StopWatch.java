/*
 * Created on Dec 4, 2003
 */
package com.cannontech.common.util;

/**
 * Convenience class to time code hunks.
 * StopWatch sw = new StopWatch().start();
 * //code to time here
 * System.out.println(sw.stop().getElapsedTime());
 * @author aaron
 */
public class StopWatch {
	
	private long _elapsedTime;
	private long _lastStartTime = Long.MAX_VALUE;

	private static ThreadLocal _stopWatchHolder = new ThreadLocal();
	
	/**
	 * Returns a threadlocal instance of StopWatch, I think this is usually what one wants
	 * @return
	 */
	public static synchronized StopWatch getInstance() {
		StopWatch sw = (StopWatch) _stopWatchHolder.get();
		if(sw == null) {
			sw = new StopWatch();
			_stopWatchHolder.set(sw);
		}
		return sw;
	}

	public StopWatch() { }
	
	/**
	 * Start the stopwatch
	 *
	 */
	public StopWatch start() {
		_lastStartTime = System.currentTimeMillis();
		return this;
	}
	
	/** 
	 * Stop the stopwatch
	 *
	 */
	public StopWatch stop() {
		_elapsedTime += System.currentTimeMillis() - _lastStartTime;
		_lastStartTime = Long.MAX_VALUE;
		return this;
	}
	
	/**
	 * Reset the elapsed time, doesn't start or stop the stopwatch
	 *
	 */
	public StopWatch reset() {
		_elapsedTime = 0L;
		return this;
	}
	
	/**
	 * Return the elapsed time in millis.
	 * @return
	 */
	public long getElapsedTime() {
		return _elapsedTime + Math.max(System.currentTimeMillis() - _lastStartTime, 0);
	}
}
