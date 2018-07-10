package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum UserPageType {
    MONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> monitorUrls = new ArrayList<>();
            monitorUrls.add(compileUrlParam("amr/outageProcessing/process/process", "outageMonitorId"));
            monitorUrls.add(compileUrlParam("amr/deviceDataMonitor/view", "monitorId"));
            monitorUrls.add(compileUrlParam("amr/tamperFlagProcessing/process/process", "tamperFlagMonitorId"));
            monitorUrls.add(compileUrlParam("amr/statusPointMonitoring/viewPage", "statusPointMonitorId"));
            monitorUrls.add(compileUrlParam("amr/statusPointMonitoring/editPage", "statusPointMonitorId"));
            monitorUrls.add(compileUrlParam("amr/porterResponseMonitor/viewPage", "monitorId"));
            monitorUrls.add(Pattern.compile("amr/vee/monitor/(\\d+).*"));
            return monitorUrls;
        }
    },
    DASHBOARD {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> dashboardUrls = new ArrayList<>();
            dashboardUrls.add(Pattern.compile("/dashboards/(\\d+).*"));
            return dashboardUrls;
        }

    },
    POINT {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> pointUrls = new ArrayList<>();
            pointUrls.add(Pattern.compile("/tools/points/(\\d+).*"));
            return pointUrls;
        }
    },
    TREND {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> trendUrls = new ArrayList<>();
            trendUrls.add(Pattern.compile("/tools/trends/(\\d+).*"));
            return trendUrls;
        }
    };

    public abstract List<Pattern> getUrl();
    
    private static Pattern compileUrlParam(String url, String param){
        String regex = url + "\\?.*" + param + "=(\\d+).*";
        return Pattern.compile(regex);
    }
}
