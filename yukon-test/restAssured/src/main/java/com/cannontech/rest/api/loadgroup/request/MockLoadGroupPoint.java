package com.cannontech.rest.api.loadgroup.request;


import com.cannontech.rest.api.common.model.MockLMDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockLoadGroupPoint extends MockLoadGroupBase {
    private MockLMDto deviceUsage;
    private MockLMDto pointUsage;
    private MockControlRawState startControlRawState;

}
