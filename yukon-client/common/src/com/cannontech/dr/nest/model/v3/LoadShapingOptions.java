package com.cannontech.dr.nest.model.v3;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LoadShapingOptions implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private PrepLoadShape prepLoadShape;
    private PeakLoadShape peakLoadShape;
    private PostLoadShape postLoadShape;
    
    @JsonCreator
    public LoadShapingOptions(PrepLoadShape prepLoadShape, PeakLoadShape peakLoadShape, PostLoadShape postLoadShape) {
        this.prepLoadShape = prepLoadShape;
        this.peakLoadShape = peakLoadShape;
        this.postLoadShape = postLoadShape;
    }

    public PrepLoadShape getPrepLoadShape() {
        return prepLoadShape;
    }

    public PeakLoadShape getPeakLoadShape() {
        return peakLoadShape;
    }

    public PostLoadShape getPostLoadShape() {
        return postLoadShape;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
