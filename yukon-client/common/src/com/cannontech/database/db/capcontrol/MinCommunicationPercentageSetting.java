package com.cannontech.database.db.capcontrol;

public class MinCommunicationPercentageSetting {
    private double banksReportingRatio =
        MinCommunicationPercentageSettingType.IVVC_BANKS_REPORTING_RATIO.getDefaultValue();
    private double regulatorReportingRatio =
        MinCommunicationPercentageSettingType.IVVC_REGULATOR_REPORTING_RATIO.getDefaultValue();
    private double voltageMonitorReportingRatio =
        MinCommunicationPercentageSettingType.IVVC_VOLTAGEMONITOR_REPORTING_RATIO.getDefaultValue();

    public MinCommunicationPercentageSetting() {}

    public MinCommunicationPercentageSetting(double banksReportingRatio,
                                              double regulatorReportingRatio,
                                              double voltageMonitorReportingRatio) {
        this.banksReportingRatio = banksReportingRatio;
        this.regulatorReportingRatio = regulatorReportingRatio;
        this.voltageMonitorReportingRatio = voltageMonitorReportingRatio;
    }

    public double getBanksReportingRatio() {
        return banksReportingRatio;
    }

    public void setBanksReportingRatio(double banksReportingRatio) {
        this.banksReportingRatio = banksReportingRatio;
    }

    public double getRegulatorReportingRatio() {
        return regulatorReportingRatio;
    }

    public void setRegulatorReportingRatio(double regulatorReportingRatio) {
        this.regulatorReportingRatio = regulatorReportingRatio;
    }

    public double getVoltageMonitorReportingRatio() {
        return voltageMonitorReportingRatio;
    }

    public void setVoltageMonitorReportingRatio(double voltageMonitorReportingRatio) {
        this.voltageMonitorReportingRatio = voltageMonitorReportingRatio;
    }

}
