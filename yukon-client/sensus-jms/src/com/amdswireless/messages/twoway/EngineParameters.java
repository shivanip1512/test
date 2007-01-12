package com.amdswireless.messages.twoway;

import java.io.Serializable;

public class EngineParameters implements Serializable {
	private transient static final long serialVersionUID = 1L;
	private int triesPerRoute;
	private int routesToTry;
	private int finalWaitSeconds;
	private int waitBetweenRouteSeconds;
	private int waitBetweenIterationSeconds;
	private String requestor;
	
	public EngineParameters() {
		this.triesPerRoute=6;
		this.routesToTry=5;
		this.finalWaitSeconds=150;
		this.waitBetweenRouteSeconds=6;
		this.waitBetweenIterationSeconds=6;
		this.requestor="anonymous";
	}
	
	public int getFinalWaitSeconds() {
		return finalWaitSeconds;
	}
	public void setFinalWaitSeconds(int finalWaitSeconds) {
		this.finalWaitSeconds = finalWaitSeconds;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public int getRoutesToTry() {
		return routesToTry;
	}
	public void setRoutesToTry(int routesToTry) {
		this.routesToTry = routesToTry;
	}
	public int getTriesPerRoute() {
		return triesPerRoute;
	}
	public void setTriesPerRoute(int triesPerRoute) {
		this.triesPerRoute = triesPerRoute;
	}
	public int getWaitBetweenRouteSeconds() {
		return waitBetweenRouteSeconds;
	}
	public void setWaitBetweenRouteSeconds(int waitBetweenRouteSeconds) {
		this.waitBetweenRouteSeconds = waitBetweenRouteSeconds;
	}

	public int getWaitBetweenIterationSeconds() {
		return waitBetweenIterationSeconds;
	}

	public void setWaitBetweenIterationSeconds(int waitBetweenIterationSeconds) {
		this.waitBetweenIterationSeconds = waitBetweenIterationSeconds;
	}
}
