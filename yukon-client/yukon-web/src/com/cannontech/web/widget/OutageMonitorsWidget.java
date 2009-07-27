package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.web.widget.support.WidgetControllerBase;

public class OutageMonitorsWidget extends WidgetControllerBase {

	private OutageMonitorDao outageMonitorDao;
	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private OutageMonitorService outageMonitorService;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("outageMonitorsWidget/render.jsp");
		
		List<OutageMonitor> rawMonitors = outageMonitorDao.getAll();
		
		ObjectMapper<OutageMonitor, OutageMonitorWrapper> objectMapper = new ObjectMapper<OutageMonitor, OutageMonitorWrapper>() {
            public OutageMonitorWrapper map(OutageMonitor from) throws ObjectMappingException {
                return new OutageMonitorWrapper(from);
            }
        };
		
		MappingList<OutageMonitor, OutageMonitorWrapper> monitors = new MappingList<OutageMonitor, OutageMonitorWrapper>(rawMonitors, objectMapper);
		
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorsWidget_deleteOutageMonitorId");
        
        String outageMonitorsWidgetError = null;
        
        try {
        	outageMonitorService.deleteOutageMonitor(outageMonitorId);
        } catch (OutageMonitorNotFoundException e) {
        	outageMonitorsWidgetError = e.getMessage();
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("outageMonitorsWidgetError", outageMonitorsWidgetError);
        
        return mav;
	}

	public class OutageMonitorWrapper {
		
		private OutageMonitor monitor;
		private int jobId = 0;
		private CommandRequestExecution latestCommandRequestExecution = null;
		
		public OutageMonitorWrapper(OutageMonitor monitor) {
			
			this.monitor = monitor;
			
			int jobId = monitor.getScheduledCommandJobId();
			if (jobId > 0) {
				
				this.jobId = jobId;
				
				this.latestCommandRequestExecution = scheduledGroupRequestExecutionDao.getLatestCommandRequestExecutionForJobId(jobId, null);
			}
		}
		
		public OutageMonitor getMonitor() {
			return monitor;
		}
		public int getJobId() {
			return jobId;
		}
		public CommandRequestExecution getLatestCommandRequestExecution() {
			return latestCommandRequestExecution;
		}
	}
	
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setOutageMonitorService(OutageMonitorService outageMonitorService) {
		this.outageMonitorService = outageMonitorService;
	}
}
