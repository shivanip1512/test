package com.cannontech.rest.api.loadgroup.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ @JsonSubTypes.Type(value = MockLoadGroupCopy.class, name = "LOAD_GROUP_COPY") })
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MockLMCopy {
    private String name;
}
