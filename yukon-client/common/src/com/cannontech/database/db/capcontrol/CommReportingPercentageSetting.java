package com.cannontech.database.db.capcontrol;

public class CommReportingPercentageSetting {
    private double banksReportingRatio =
        CommReportingPercentageSettingType.CAPBANK.getDefaultValue();
    private double regulatorReportingRatio =
        CommReportingPercentageSettingType.REGULATOR.getDefaultValue();
    private double voltageMonitorReportingRatio =
        CommReportingPercentageSettingType.VOLTAGE_MONITOR.getDefaultValue();

    public CommReportingPercentageSetting() {}

    public CommReportingPercentageSetting(double banksReportingRatio,
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
