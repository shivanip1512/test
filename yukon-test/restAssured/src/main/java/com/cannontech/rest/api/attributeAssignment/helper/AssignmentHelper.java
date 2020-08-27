package com.cannontech.rest.api.attributeAssignment.helper;

import com.cannontech.rest.api.attributeAssignment.request.MockAssignmentModel;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.common.model.MockPointType;

public class AssignmentHelper {

    public static MockAssignmentModel buildDevice() {
        MockAssignmentModel assignment = MockAssignmentModel.builder()
                .attributeId(Integer.valueOf(ApiCallHelper.getProperty("customAttributeId")))
                .paoType(MockPaoType.RFN420FL)
                .offset(100)
                .pointType(MockPointType.Analog)
                .build();
        return assignment;
    }

}
