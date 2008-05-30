package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.graph.GraphBean;
import com.cannontech.roles.application.TrendingRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.consumer.model.GraphViewType;

@Controller
public class TrendingController extends AbstractConsumerController {

	private GraphDao graphDao;

	@Autowired
	public void setGraphDao(GraphDao graphDao) {
		this.graphDao = graphDao;
	}

	@CheckRole(TrendingRole.ROLEID)
	@RequestMapping(value = "/consumer/trending/view", method = RequestMethod.GET)
	public String view(HttpServletRequest request, HttpServletResponse response, ModelMap map, Integer gdefid) {

		YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
		accountCheckerService.checkGraph(user, gdefid);
		
		LiteGraphDefinition graphDefinition = graphDao
				.getLiteGraphDefinition(gdefid);
		map.addAttribute("graphDefinition", graphDefinition);

		// Get graph bean from session if exists
		Object sessionBean = request.getSession().getAttribute(
				ServletUtil.ATT_GRAPH_BEAN);

		GraphBean bean = null;
		if (sessionBean != null) {
			bean = (GraphBean) sessionBean;
		} else {
			bean = new GraphBean();
		}
		
		bean.setGdefid(gdefid);
		bean.setPage(1);

		// Add graph bean to session
		request.getSession().setAttribute(ServletUtil.ATT_GRAPH_BEAN, bean);

		int viewTypeInt = bean.getViewType();
		GraphViewType viewType = GraphViewType.valueOf(viewTypeInt);
		map.addAttribute("viewType", viewType);

		if(GraphViewType.SUMMARY.equals(viewType)) {
			bean.updateCurrentPane();
		}

		StringBuffer stringBuffer = bean.getHtmlString();
		if (stringBuffer != null) {
			map.addAttribute("graphHtml", stringBuffer.toString());
		}

		return "consumer/trending.jsp";
	}

}
