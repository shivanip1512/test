package com.cannontech.web.amr.voltagetou;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.bean.YCBean;

/**
 * Spring controller class for voltage and tou
 */
public class VoltageAndTouController extends MultiActionController {

	private PaoDao paoDao = null;

	public VoltageAndTouController() {
		super();
	}

	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}

	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("voltageAndTou.jsp");

		int deviceId = ServletRequestUtils.getRequiredIntParameter(request,
				"deviceId");
		mav.addObject("deviceId", deviceId);

		// Get or create the YCBean and put it into the session
		YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
		if (ycBean == null) {
			ycBean = new YCBean();
		}
		request.getSession().setAttribute("YC_BEAN", ycBean);

		LiteYukonUser user = ServletUtil.getYukonUser(request);

		ycBean.setUserID(user.getUserID());
		ycBean.setDeviceID(deviceId);

		LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
		mav.addObject("device", device);

		// Get the point data for the page
		PointData data = ycBean.getRecentPointData(deviceId, 4,
				PointTypes.DEMAND_ACCUMULATOR_POINT);
		String lastIntervalTime = ycBean.getFormattedTimestamp(data, "---");
		mav.addObject("lastIntervalTime", lastIntervalTime);
		String lastIntervalValue = ycBean.getFormattedValue(data, "#0.000",
				"&nbsp;");
		mav.addObject("lastIntervalValue", lastIntervalValue);

		data = (PointData) ycBean.getRecentPointData(deviceId, 15,
				PointTypes.DEMAND_ACCUMULATOR_POINT);
		String minimumTime = ycBean.getFormattedTimestamp(data, "---");
		mav.addObject("minimumTime", minimumTime);
		String minimumValue = ycBean
				.getFormattedValue(data, "#0.000", "&nbsp;");
		mav.addObject("minimumValue", minimumValue);

		data = (PointData) ycBean.getRecentPointData(deviceId, 14,
				PointTypes.DEMAND_ACCUMULATOR_POINT);
		String maximumTime = ycBean.getFormattedTimestamp(data, "---");
		mav.addObject("maximumTime", maximumTime);
		String maximumValue = ycBean
				.getFormattedValue(data, "#0.000", "&nbsp;");
		mav.addObject("maximumValue", maximumValue);

		List<LiteTOUSchedule> schedules = DefaultDatabaseCache.getInstance()
				.getAllTOUSchedules();
		mav.addObject("schedules", schedules);

		String errorMsg = ServletRequestUtils.getStringParameter(request,
				"errorMsg");
		if (errorMsg == null && ycBean.getErrorMsg() != null) {
			errorMsg = ycBean.getErrorMsg();
			ycBean.setErrorMsg("");
		}

		mav.addObject("errorMsg", errorMsg);

		return mav;
	}

}
