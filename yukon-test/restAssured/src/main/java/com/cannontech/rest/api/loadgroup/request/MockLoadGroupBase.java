package com.cannontech.rest.api.loadgroup.request;

import com.cannontech.rest.api.common.model.MockPaoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ 
    @JsonSubTypes.Type(value = MockLoadGroupExpresscom.class, name = "LM_GROUP_EXPRESSCOMM"),
    @JsonSubTypes.Type(value = MockLoadGroupItron.class, name = "LM_GROUP_ITRON"),
    @JsonSubTypes.Type(value = MockLoadGroupDigiSep.class, name = "LM_GROUP_DIGI_SEP"),
    @JsonSubTypes.Type(value = MockLoadGroupEmetcon.class, name = "LM_GROUP_EMETCON"),
    @JsonSubTypes.Type(value = MockLoadGroupVersacom.class, name = "LM_GROUP_VERSACOM"),
    @JsonSubTypes.Type(value = MockLoadGroupEcobee.class, name = "LM_GROUP_ECOBEE"),
    @JsonSubTypes.Type(value = MockLoadGroupHoneywell.class, name = "LM_GROUP_HONEYWELL"),
    @JsonSubTypes.Type(value = MockLoadGroupNest.class, name = "LM_GROUP_NEST"),
    @JsonSubTypes.Type(value = MockLoadGroupDisconnect.class, name = "LM_GROUP_METER_DISCONNECT")
    })
@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class MockLoadGroupBase {
    private Integer id;
    private String name;
    private MockPaoType type;
    @JsonProperty("kWCapacity")
    private Double kWCapacity;
    private boolean disableGroup;
    private boolean disableControl;

}
