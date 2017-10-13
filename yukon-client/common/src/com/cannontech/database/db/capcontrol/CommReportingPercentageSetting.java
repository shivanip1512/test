package com.cannontech.database.db.capcontrol;

public class CommReportingPercentageSetting {
    private double banksReportingRatio =
        CommReportingPercentageSettingType.CAPBANK.getDefaultValue();
    private double regulatorReportingRatio =
        CommReportingPercentageSettingType.REGULATOR.getDefaultValue();
    private double voltageMonitorReportingRatio =
        CommReportingPercentageSettingType.VOLTAGE_MONITOR.getDefaultValue();
    private boolean considerPhase = true;

    public CommReportingPercentageSetting() {}

    public CommReportingPercentageSetting(double banksReportingRatio,
                                              double regulatorReportingRatio,
                                              double voltageMonitorReportingRatio,
                                              boolean considerPhase) {
        this.banksReportingRatio = banksReportingRatio;
        this.regulatorReportingRatio = regulatorReportingRatio;
        this.voltageMonitorReportingRatio = voltageMonitorReportingRatio;
        this.considerPhase = considerPhase;
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

    public boolean isConsiderPhase() {
        return considerPhase;
    }

    public void setConsiderPhase(boolean considerPhase) {
        this.considerPhase = considerPhase;
    }

}
