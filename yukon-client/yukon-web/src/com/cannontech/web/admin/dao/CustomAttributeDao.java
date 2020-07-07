package com.cannontech.web.admin.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.web.admin.model.CustomAttributeDetail;

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
    List<CustomAttributeDetail> getCustomAttributeDetails(List<Integer> attributeIds, List<PaoType> deviceTypes, SortBy sortBy,
            Direction direction);
}
