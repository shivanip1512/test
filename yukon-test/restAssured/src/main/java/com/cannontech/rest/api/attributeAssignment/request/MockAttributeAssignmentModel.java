package com.cannontech.rest.api.attributeAssignment.request;

import com.cannontech.rest.api.customAttribute.request.MockCustomAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MockAttributeAssignmentModel extends MockAssignmentModel{
    private MockCustomAttribute customAttribute;
}
