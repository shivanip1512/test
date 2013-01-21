package com.cannontech.development.model;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.point.PointQuality;
import com.google.common.collect.Lists;

public class BulkFakePointInjectionDto {
    private List<YukonPao> yukonPaos;
    private String groupName = SystemGroupEnum.ROOT.getFullPath();
    private Attribute attribute;
    private boolean archive = true;
    private Instant start = new Instant().minus(Duration.standardDays(30));
    private Instant stop = new Instant();
    private Period period = Period.days(1);
    private Period periodWindow = Period.minutes(5);
    private List<PointQuality> pointQualities = Lists.newArrayList(PointQuality.Normal);
    private String algorithm = "normal";
    private boolean incremental = false;
    private double valueLow;
    private double valueHigh;
    private int decimalPlaces;
    private int injectionCount = 0;

    public List<YukonPao> getYukonPaos() {
        return yukonPaos;
    }
    public void setYukonPaos(List<YukonPao> yukonPaos) {
        this.yukonPaos = yukonPaos;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    public boolean isArchive() {
        return archive;
    }
    public void setArchive(boolean archive) {
        this.archive = archive;
    }
    public Instant getStart() {
        return start;
    }
    public void setStart(Instant start) {
        this.start = start;
    }
    public Instant getStop() {
        return stop;
    }
    public void setStop(Instant stop) {
        this.stop = stop;
    }
    public Period getPeriod() {
        return period;
    }
    public void setPeriod(Period period) {
        this.period = period;
    }
    public void setPeriodWindow(Period periodWindow) {
        this.periodWindow = periodWindow;
    }
    public Period getPeriodWindow() {
        return periodWindow;
    }
    public List<PointQuality> getPointQualities() {
        return pointQualities;
    }
    public void setPointQualities(List<PointQuality> pointQualities) {
        this.pointQualities = pointQualities;
    }
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public boolean isIncremental() {
        return incremental;
    }
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }
    public double getValueLow() {
        return valueLow;
    }
    public void setValueLow(double valueLow) {
        this.valueLow = valueLow;
    }
    public double getValueHigh() {
        return valueHigh;
    }
    public void setValueHigh(double valueHigh) {
        this.valueHigh = valueHigh;
    }
    public int getDecimalPlaces() {
        return decimalPlaces;
    }
    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
    public double getMean() {
        return (this.valueHigh + this.valueLow)/2;
    }
    public void setInjectionCount(int injectionCount) {
        this.injectionCount = injectionCount;
    }
    public int getInjectionCount() {
        return injectionCount;
    }
    public void incrementInjectionCount() {
        injectionCount++;
    }
}
