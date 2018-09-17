package com.cannontech.dr.nest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadShaping {

    private LoadShapingPreparation preparation;
    private LoadShapingPeak peak;
    private LoadShapingPost post;

    @JsonCreator
    public LoadShaping(@JsonProperty("preparation_load_shaping") LoadShapingPreparation preparation,
            @JsonProperty("peak_load_shaping") LoadShapingPeak peak,
            @JsonProperty("post_peak_load_shaping") LoadShapingPost post) {
        this.preparation = preparation;
        this.peak = peak;
        this.post = post;
    }

    @JsonProperty("preparation_load_shaping")
    public LoadShapingPreparation getPreparation() {
        return preparation;
    }

    @JsonProperty("peak_load_shaping")
    public LoadShapingPeak getPeak() {
        return peak;
    }

    @JsonProperty("post_peak_load_shaping")
    public LoadShapingPost getPost() {
        return post;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
