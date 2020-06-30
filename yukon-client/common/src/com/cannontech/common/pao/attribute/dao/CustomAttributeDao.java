package com.cannontech.common.pao.attribute.dao;

import java.util.List;

import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface CustomAttributeDao {

    /**
     * Creates or updates attribute
     * @throws DuplicateException - if attribute name already exists
     */
    void saveCustomAttribute(CustomAttribute attribute);

    /**
     * Deletes custom  attribute
     */
    void deleteCustomAttribute(int attributeId);

    /**
     * Returns the list of attributes
     */
    List<CustomAttribute> getCustomAttributes();

}
