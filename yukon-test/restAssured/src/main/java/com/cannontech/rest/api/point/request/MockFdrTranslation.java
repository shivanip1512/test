package com.cannontech.rest.api.point.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockFdrTranslation {
    private MockFdrDirection direction;
    private MockFdrInterfaceType fdrInterfaceType;
    private String translation;
}
