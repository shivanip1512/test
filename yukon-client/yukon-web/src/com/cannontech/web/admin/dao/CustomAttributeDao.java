package com.cannontech.web.admin.dao;

import java.util.List;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface CustomAttributeDao {

    public enum SortBy {
        ATTRIBUTE_NAME("AttributeName"),
        DEVICE_TYPE("DeviceType"),
        POINT_TYPE("PointType"),
        POINT_OFFSET("PointOffset");

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }
    
    /**
     * Returns the list of sorted custom attribute details. 
     * 
     * @param attributeIds - null or empty if all attributes selected
     * @param deviceTypes - null or empty if all device types selected
     * @param sortBy - field to order by
     * @param direction - ascending or descending direction
     */
    List<AttributeAssignment> getCustomAttributeDetails(List<Integer> attributeIds, List<PaoType> deviceTypes, SortBy sortBy,
            Direction direction);

    /**
     * Creates or updates attribute
     * @return CustomAttribute
     */
    public CustomAttribute saveCustomAttribute(CustomAttribute attribute);

    /**
     * Deletes custom attribute
     * 
     * @throws DataDependencyException
     */
    void deleteCustomAttribute(int attributeId) throws DataDependencyException;
    
    /**
     * Creates or updates attribute assignment
     * @return AttributeAssignment 
     */
    AttributeAssignment saveAttributeAssignment(Assignment assignment);

    /**
     * Deletes attribute assignment
     */
    void deleteAttributeAssignment(int attributeAssignmentId);
}
