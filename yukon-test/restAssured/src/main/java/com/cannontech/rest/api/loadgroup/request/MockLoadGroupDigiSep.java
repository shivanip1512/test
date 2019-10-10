package com.cannontech.rest.api.loadgroup.request;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockLoadGroupDigiSep extends MockLoadGroupBase {

    private Integer utilityEnrollmentGroup;
    private List<MockSepDeviceClass> deviceClassSet;
    private Integer rampInMinutes;
    private Integer rampOutMinutes;

}
