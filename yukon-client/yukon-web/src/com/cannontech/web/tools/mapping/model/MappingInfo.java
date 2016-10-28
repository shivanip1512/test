package com.cannontech.web.tools.mapping.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.metadata.CommStatusType;
import com.cannontech.common.rfn.model.RfnDevice;

public abstract class MappingInfo {
    
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.";

    private RfnDevice device;
    private FeatureCollection location;
    private CommStatusType status = CommStatusType.UNKNOWN;
    private double distanceInMiles;
    private double distanceInKm;
    private String statusDisplay;
    private String distanceDisplay;
    private MessageSourceAccessor accessor;

    public MappingInfo(RfnDevice device, FeatureCollection location, MessageSourceAccessor accessor) {
        this.device = device;
        this.location = location;
        this.accessor = accessor;
    }

    public FeatureCollection getLocation() {
        return location;
    }

    public RfnDevice getDevice() {
        return device;
    }

    public double getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(double distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

    public CommStatusType getStatus() {
        return status;
    }

    public void setStatus(CommStatusType status) {
        this.status = status;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("device", device);
        builder.append("status", status);
        builder.append("distanceInMiles", distanceInMiles);
        builder.append("distanceInKm", distanceInKm);
        return builder.toString();
    }

    public String getStatusDisplay() {
        statusDisplay = accessor.getMessage(nameKey + "status." + status.name());
        return statusDisplay;
    }

    public String getDistanceDisplay() {
        distanceDisplay = String.format("%.4f", distanceInMiles) + " " + accessor.getMessage(nameKey + "distance.miles") + " (" + String.format("%.4f", distanceInKm) + " " + accessor.getMessage(nameKey + "distance.kilometers") + ")";
        return distanceDisplay;
    }

    public void setAccessor(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }
}
