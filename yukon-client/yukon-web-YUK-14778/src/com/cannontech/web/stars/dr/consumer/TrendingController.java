package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.graph.GraphBean;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.consumer.model.GraphViewType;

@CheckRole(YukonRole.TRENDING)
@Controller
@RequestMapping("/consumer/trending/*")
public class TrendingController extends AbstractConsumerController {

    @Autowired private GraphDao graphDao;
    @Autowired private AccountCheckerService accountCheckerService;

	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(LiteYukonUser user, HttpSession session, ModelMap map, Integer gdefid) {

		accountCheckerService.checkGraph(user, gdefid);
		
		LiteGraphDefinition graphDefinition = graphDao.getLiteGraphDefinition(gdefid);
		map.addAttribute("graphDefinition", graphDefinition);

		// Get graph bean from session if exists
		Object sessionBean = session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);

		GraphBean bean = null;
		if (sessionBean != null) {
			bean = (GraphBean) sessionBean;
		} else {
			bean = new GraphBean();
		}
		
		bean.setGdefid(gdefid);
		bean.setPage(1);

		// Add graph bean to session
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, bean);

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
