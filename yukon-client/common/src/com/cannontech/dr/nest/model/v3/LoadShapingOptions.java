package com.cannontech.dr.nest.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LoadShapingOptions {
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
}
