package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum UserPageType {
    OUTAGE_MONITOR {
        List<Pattern> outageMonitorUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (outageMonitorUrls.isEmpty()) {
                outageMonitorUrls.add(compileUrlParam("amr/outageProcessing/process/process", "outageMonitorId"));
            }
            return outageMonitorUrls;
        }
    },
    DEVICE_DATA_MONITOR {
        List<Pattern> deviceDataMonitorUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (deviceDataMonitorUrls.isEmpty()) {
                deviceDataMonitorUrls.add(compileUrlParam("amr/deviceDataMonitor/view", "monitorId"));
            }
            return deviceDataMonitorUrls;
        }
    },
    TAMPER_FLAG_MONITOR {
        List<Pattern> tamperFlagUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (tamperFlagUrls.isEmpty()) {
                tamperFlagUrls.add(compileUrlParam("amr/tamperFlagProcessing/process/process", "tamperFlagMonitorId"));
            }
            return tamperFlagUrls;
        }
    },
    STATUS_POINT_MONITOR {
        List<Pattern> statusPointUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (statusPointUrls.isEmpty()) {
                statusPointUrls.add(compileUrlParam("amr/statusPointMonitoring/viewPage", "statusPointMonitorId"));
                statusPointUrls.add(compileUrlParam("amr/statusPointMonitoring/editPage", "statusPointMonitorId"));
            }
            return statusPointUrls;
        }
    },
    PORTER_RESPONSE_MONITOR {
        List<Pattern> porterResponseUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (porterResponseUrls.isEmpty()) {
                porterResponseUrls.add(compileUrlParam("amr/porterResponseMonitor/viewPage", "monitorId"));
            }
            return porterResponseUrls;
        }
    },
    VALIDATION_MONITOR {
        List<Pattern> validationUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (validationUrls.isEmpty()) {
                validationUrls.add(Pattern.compile("amr/vee/monitor/(\\d+).*"));
            }
            return validationUrls;
        }
    },
    DASHBOARD {
        List<Pattern> dashboardUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (dashboardUrls.isEmpty()) {
                dashboardUrls.add(Pattern.compile("/dashboards/(\\d+).*"));
            }
            return dashboardUrls;
        }

    },
    POINT {
        List<Pattern> pointUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (pointUrls.isEmpty()) {
                pointUrls.add(Pattern.compile("/tools/points/(\\d+).*"));
            }
            return pointUrls;
        }
    },
    TREND {
        List<Pattern> trendUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (trendUrls.isEmpty()) {
                trendUrls.add(Pattern.compile("/tools/trends/(\\d+).*"));
            }
            return trendUrls;
        }
    },
    DEMAND_RESPONSE {
        List<Pattern> demandResponseUrl = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (demandResponseUrl.isEmpty()) {
                demandResponseUrl.add(compileUrlParam("/dr/program/detail", "programId"));
                demandResponseUrl.add(compileUrlParam("/dr/scenario/detail", "scenarioId"));
                demandResponseUrl.add(compileUrlParam("/dr/loadGroup/detail", "loadGroupId"));
                demandResponseUrl.add(compileUrlParam("/dr/controlArea/detail", "controlAreaId"));
            }
            return demandResponseUrl;
        }
    },
    PAO {
        List<Pattern> paoUrls = new ArrayList<>();
        @Override
        public List<Pattern> getUrl() {
            if (paoUrls.isEmpty()) {
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

                // Demand Reponse url
                paoUrls.addAll(UserPageType.DEMAND_RESPONSE.getUrl());
            }

            return paoUrls;
        }
    };

    public abstract List<Pattern> getUrl();

    private static Pattern compileUrlParam(String url, String param) {
        String regex = url + "\\?.*" + param + "=(\\d+).*";
        return Pattern.compile(regex);
    }
}
