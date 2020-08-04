package com.cannontech.rest.api.attributeAssignment.helper;

import com.cannontech.rest.api.attributeAssignment.request.MockAssignmentModel;

public class AssignmentHelper {

    public static MockAssignmentModel buildDevice() {
        MockAssignmentModel assignment = MockAssignmentModel.builder()
                .build();
        return assignment;
    }

}
