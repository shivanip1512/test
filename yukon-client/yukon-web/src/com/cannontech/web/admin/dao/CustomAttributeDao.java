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
        attributeName("AttributeName"),
        paoType("PaoType"),
        pointType("PointType"),
        pointOffset("PointOffset");

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
     * Deletes custom attribute
     * 
     * @throws DataDependencyException
     */
    void deleteCustomAttribute(int attributeId) throws DataDependencyException;
    
    /**
     * Deletes attribute assignment
     */
    void deleteAttributeAssignment(int attributeAssignmentId);

    /**
     * Creates attribute assignment
     * @return AttributeAssignment 
     */
    AttributeAssignment updateAttributeAssignment(Assignment assignment);
    
    /**
     * Updates attribute assignment
     * @return AttributeAssignment 
     */
    AttributeAssignment createAttributeAssignment(Assignment assignment);

    /**
     * Creates attribute
     * @return CustomAttribute
     */
    CustomAttribute createCustomAttribute(CustomAttribute attribute);
    
    /**
     * Updates attribute
     * @return CustomAttribute
     */
    CustomAttribute updateCustomAttribute(CustomAttribute attribute);
}
