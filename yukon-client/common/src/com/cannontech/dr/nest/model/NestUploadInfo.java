package com.cannontech.dr.nest.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NestUploadInfo {
    private int numOfGroupChanges;
    private int numOfDissolved;
    private List<NestUploadError> errors;

    @JsonCreator
    public NestUploadInfo(@JsonProperty("num_group_changes") int numOfGroupChanges,
            @JsonProperty("num_dissolved") int numOfDissolved,
            @JsonProperty("errors") List<NestUploadError> errors) {
        this.numOfGroupChanges = numOfGroupChanges;
        this.numOfDissolved = numOfDissolved;
        this.errors = errors;
    }

    public boolean isGroupChangeSuccessful() {
        return numOfGroupChanges == 1;
    }
    
    public boolean isAccountDissolved() {
        return numOfDissolved == 1;
    }
    
    public List<String> getNestErrors(){
        return errors.stream()
                .map(e -> e.getErrors())
                .collect(ArrayList::new, List::addAll, List::addAll);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }  
}
