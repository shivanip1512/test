package com.cannontech.web.capcontrol.ivvc.models;

import com.cannontech.common.model.Phase;

public class VfPoint {

    private Integer pointId;
	private Integer feederId;
    private String description;
	private String zoneName;
	private Phase phase;
	private boolean regulator;
    private Double x;
    private Double y;
    private Integer seriesId;
    private boolean ignore;
    private boolean overrideStrategy;
    private double lowerLimit;
    private double upperLimit;
    
    public VfPoint(Double x, Double y, Integer seriesId) {
        this(null, null, null, null, false, x, y, seriesId, false);
    }
    
    public VfPoint(Integer pointId, String description, String zoneName, Phase phase, boolean regulator, Double x, Double y, boolean ignore) {
        this(pointId, description, zoneName, phase, regulator, x, y, null, ignore);
    }
    
    public VfPoint(Integer pointId, String description, String zoneName, Phase phase, boolean regulator, Double x, Double y, Integer seriesId, boolean ignore) {
        this.pointId = pointId;
        this.description = description;
        this.zoneName = zoneName;
        this.phase = phase;
        this.regulator = regulator;
        this.x = x;
        this.y = y;
        this.seriesId = seriesId;
        this.ignore = ignore;
    }
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isRegulator() {
        return regulator;
    }

    public void setRegulator(boolean regulator) {
        this.regulator = regulator;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isOverrideStrategy() {
        return overrideStrategy;
    }

    public void setOverrideStrategy(boolean overrideStrategy) {
        this.overrideStrategy = overrideStrategy;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public Integer getFeederId() {
        return feederId;
    }

    public void setFeederId(Integer feederId) {
        this.feederId = feederId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((feederId == null) ? 0 : feederId.hashCode());
        result = prime * result + (ignore ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(lowerLimit);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (overrideStrategy ? 1231 : 1237);
        result = prime * result + ((phase == null) ? 0 : phase.hashCode());
        result = prime * result + ((pointId == null) ? 0 : pointId.hashCode());
        result = prime * result + (regulator ? 1231 : 1237);
        result = prime * result + ((seriesId == null) ? 0 : seriesId.hashCode());
        temp = Double.doubleToLongBits(upperLimit);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        result = prime * result + ((zoneName == null) ? 0 : zoneName.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VfPoint other = (VfPoint) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (feederId == null) {
            if (other.feederId != null)
                return false;
        } else if (!feederId.equals(other.feederId))
            return false;
        if (ignore != other.ignore)
            return false;
        if (Double.doubleToLongBits(lowerLimit) != Double.doubleToLongBits(other.lowerLimit))
            return false;
        if (overrideStrategy != other.overrideStrategy)
            return false;
        if (phase != other.phase)
            return false;
        if (pointId == null) {
            if (other.pointId != null)
                return false;
        } else if (!pointId.equals(other.pointId))
            return false;
        if (regulator != other.regulator)
            return false;
        if (seriesId == null) {
            if (other.seriesId != null)
                return false;
        } else if (!seriesId.equals(other.seriesId))
            return false;
        if (Double.doubleToLongBits(upperLimit) != Double.doubleToLongBits(other.upperLimit))
            return false;
        if (x == null) {
            if (other.x != null)
                return false;
        } else if (!x.equals(other.x))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        if (zoneName == null) {
            if (other.zoneName != null)
                return false;
        } else if (!zoneName.equals(other.zoneName))
            return false;
        return true;
    }

}
