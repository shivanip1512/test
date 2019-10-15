package com.cannontech.rest.api.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class MockLMPaoDto extends MockLMDto {

    private MockPaoType type;
}
