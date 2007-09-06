package com.cannontech.web.stars.service.impl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class TimeConsumingTaskServiceImpl implements TimeConsumingTaskService {

	public String processTask(final TimeConsumingTask task, final HttpServletRequest request,
			String redirect, String referer) {
		
		final HttpSession session = request.getSession(false);
		final long id = ProgressChecker.addTask(task);
		
		//Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			final TimeConsumingTask runningTask = ProgressChecker.getTask(id);
			String redir = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
			
			if (runningTask.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				if (redir != null) redirect = redir;
				return redirect;
			}
			
			if (runningTask.getStatus() == TimeConsumingTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				redirect = referer;
				return redirect;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
		return redirect;
	}

}
