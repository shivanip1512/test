package com.cannontech.web.stars.service;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.stars.util.task.TimeConsumingTask;

public interface TimeConsumingTaskService {

	public String processTask(TimeConsumingTask task, HttpServletRequest request, String redirect, String referer);
	
}
