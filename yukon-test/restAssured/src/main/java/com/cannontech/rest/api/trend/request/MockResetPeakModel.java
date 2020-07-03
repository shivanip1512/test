package com.cannontech.rest.api.trend.request;

import org.joda.time.DateTime;

import com.cannontech.rest.api.trend.helper.DateDeserializer;
import com.cannontech.rest.api.trend.helper.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
public class MockResetPeakModel {
    @JsonSerialize(using = DateSerializer.class) @JsonDeserialize(using = DateDeserializer.class) private DateTime startDate;
}
