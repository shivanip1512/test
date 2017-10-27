package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display number of queued entries for ports (comm channels)
 */
@Controller
@RequestMapping("/porterQueueCountsWidget")
public class PorterQueueCountsWidget extends AdvancedWidgetControllerBase {
        
    private static final Logger log = YukonLogManager.getLogger(PorterQueueCountsWidget.class);

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        List<Integer> portIds = new ArrayList<Integer>();
        model.addAttribute("maxNumPorts", GlobalSettingType.PORTER_QUEUE_COUNTS_TREND_MAX_NUM_PORTS.getDefaultValue());
        try {
            String csportIdsString = WidgetParameterHelper.getStringParameter(request, "selectPorts");
            if (csportIdsString == null || csportIdsString.trim().isEmpty()) {
                return "porterQueueCountsWidget/render.jsp";
            }
            Pattern pattern = Pattern.compile(",");
            portIds = pattern.splitAsStream(csportIdsString)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        } catch (ServletRequestBindingException e) {
            log.error(e.getMessage());
        }
        if (portIds == null || portIds.size() == 0) { //this should never happen due to the csportIdsString check above, but just in case
            return "porterQueueCountsWidget/render.jsp";
        }
        model.addAttribute("portIds", portIds);
        return "porterQueueCountsWidget/render.jsp";
    }
}
