package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum UserPageType {
    OUTAGEMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> outageMonitorUrls = new ArrayList<>();
            outageMonitorUrls.add(compileUrlParam("amr/outageProcessing/process/process", "outageMonitorId"));
            return outageMonitorUrls;
        }
    },
    DEVICEDATAMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> deviceDataMonitorUrls = new ArrayList<>();
            deviceDataMonitorUrls.add(compileUrlParam("amr/deviceDataMonitor/view", "monitorId"));
            return deviceDataMonitorUrls;
        }
    },
    TAMPERFLAGMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> tamperFlagUrls = new ArrayList<>();
            tamperFlagUrls.add(compileUrlParam("amr/tamperFlagProcessing/process/process", "tamperFlagMonitorId"));
            return tamperFlagUrls;
        }
    },
    STATUSPOINTMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> statusPointUrls = new ArrayList<>();
            statusPointUrls.add(compileUrlParam("amr/statusPointMonitoring/viewPage", "statusPointMonitorId"));
            statusPointUrls.add(compileUrlParam("amr/statusPointMonitoring/editPage", "statusPointMonitorId"));
            return statusPointUrls;
        }
    },
    PORTERRESPONSEMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> porterResponseUrls = new ArrayList<>();
            porterResponseUrls.add(compileUrlParam("amr/porterResponseMonitor/viewPage", "monitorId"));
            return porterResponseUrls;
        }
    },
    VALIDATIONMONITOR {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> validationUrls = new ArrayList<>();
            validationUrls.add(Pattern.compile("amr/vee/monitor/(\\d+).*"));
            return validationUrls;
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
    },
    DEMANDRESPONSE {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> demandResponseUrl = new ArrayList<>();
            demandResponseUrl.add(compileUrlParam("/dr/program/detail", "programId"));
            demandResponseUrl.add(compileUrlParam("/dr/scenario/detail", "scenarioId"));
            demandResponseUrl.add(compileUrlParam("/dr/loadGroup/detail", "loadGroupId"));
            demandResponseUrl.add(compileUrlParam("/dr/controlArea/detail", "controlAreaId"));
            return demandResponseUrl;
        }
    },
    PAO {
        @Override
        public List<Pattern> getUrl() {
            List<Pattern> paoUrls = new ArrayList<>();
            // Cap control url
            paoUrls.add(Pattern.compile("/capcontrol/areas/(\\d+)"));
            paoUrls.add(compileUrlParam("/capcontrol/tier/feeders", "substationId"));
            paoUrls.add(compileUrlParam("/capcontrol/ivvc/bus/detail", "subBusId"));
            paoUrls.add(Pattern.compile("/capcontrol/regulators/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/feeders/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/substations/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/buses/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/cbc/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/capbanks/(\\d+)"));
            paoUrls.add(Pattern.compile("/capcontrol/strategies/(\\d+)"));
            
            // Meter url
            paoUrls.add(Pattern.compile("/common/pao/(\\d+).*"));
            paoUrls.add(compileUrlParam("/meter/home", "deviceId"));
            paoUrls.add(compileUrlParam("/meter/moveIn", "deviceId"));
            paoUrls.add(compileUrlParam("/meter/moveOut", "deviceId"));
            paoUrls.add(compileUrlParam("/meter/highBill/view", "deviceId"));
            paoUrls.add(compileUrlParam("/amr/profile/home", "deviceId"));
            paoUrls.add(compileUrlParam("/amr/voltageAndTou/home", "deviceId"));
            paoUrls.add(compileUrlParam("/amr/manualCommand/home", "deviceId"));
            paoUrls.add(Pattern.compile("/bulk/routeLocate/home\\?.*?idList.ids=(\\d+(?:,\\d+)).*?"));
            
            // Other devices url
            paoUrls.add(compileUrlParam("/stars/relay/home", "deviceId"));
            paoUrls.add(Pattern.compile("/stars/gateways/(\\d+)"));
            paoUrls.add(Pattern.compile("/stars/rtu/(\\d+)"));
            
            // Demand reponse url
            paoUrls.addAll(UserPageType.DEMANDRESPONSE.getUrl());
            
            return paoUrls;
        }
    };

    public abstract List<Pattern> getUrl();
    
    private static Pattern compileUrlParam(String url, String param){
        String regex = url + "\\?.*" + param + "=(\\d+).*";
        return Pattern.compile(regex);
    }
}
