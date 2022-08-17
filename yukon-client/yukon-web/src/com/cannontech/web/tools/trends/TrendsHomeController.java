package com.cannontech.web.tools.trends;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.Graph;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@CheckRole(YukonRole.TRENDING)
public class TrendsHomeController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GraphDao graphDao;
    
    @RequestMapping({"/trends", "/trends/"})
    public String trends(ModelMap model, YukonUserContext userContext) throws JsonProcessingException {
        
        List<LiteGraphDefinition> trends = graphDao.getGraphDefinitions();
        model.addAttribute("trends", trends);
        
        if (!trends.isEmpty()) {
            model.addAttribute("trendId", trends.get(0).getGraphDefinitionID());
            model.addAttribute("trendName", trends.get(0).getName());
            model.addAttribute("pageName", "trend");
        } else {
            model.addAttribute("pageName", "trends");
        }
        model.addAttribute("labels", TrendUtils.getLabels(userContext, messageResolver));
        
        return "trends/trends.jsp";
    }
    
    @RequestMapping("/trends/{id}")
    public String trend(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash)
            throws JsonProcessingException {

        List<LiteGraphDefinition> trends = graphDao.getGraphDefinitions();
        model.addAttribute("trends", trends);

        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        if (null == trend) {
            return "redirect:/tools/trends";
        } else {
            model.addAttribute("trendId", id);
            model.addAttribute("trendName", trend.getName());
            model.addAttribute("pageName", "trend");
            model.addAttribute("labels", TrendUtils.getLabels(userContext, messageResolver));

            return "trends/trends.jsp";
        }
    }
    
    @RequestMapping("/trends/{id}/csv")
    public void csv(HttpServletResponse resp, @PathVariable int id, long from, long to) throws IOException {
        
        Instant start = new Instant(from);
        Instant stop = new Instant(to);
        
        Graph graph = new Graph();
        graph.setStartDate(start.toDate());
        graph.setGraphDefinition(graphDao.getLiteGraphDefinition(id));
        graph.setViewType(GraphRenderers.LINE);
        graph.setUpdateTrend(true);
        TrendProperties props = new TrendProperties();
        props.setOptionsMaskSettings(GraphRenderers.BASIC_MASK);
        graph.setTrendProperties(props);
        graph.update(start, stop);

        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        String fileName = graph.getTrendModel().getChartName().toString() + "_" + now +  ".csv";

        ServletOutputStream out = resp.getOutputStream();
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setContentType("text/x-comma-separated-values");
        graph.encodeCSV(out);
        
        out.flush();
    }
}