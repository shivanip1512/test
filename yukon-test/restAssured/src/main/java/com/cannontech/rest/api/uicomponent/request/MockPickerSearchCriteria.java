package com.cannontech.rest.api.uicomponent.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)

public class MockPickerSearchCriteria {
    private String type;
    private String queryString;
    private Integer startCount;
    private Integer count;
    private String extraArgs;
    
}
