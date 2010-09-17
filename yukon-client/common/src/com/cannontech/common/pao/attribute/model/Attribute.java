package com.cannontech.common.pao.attribute.model;

import com.cannontech.common.util.DatabaseRepresentationSource;


/**
 * Class that represents an attribute of a device
 */
public interface Attribute extends DatabaseRepresentationSource {


    public String getKey();

    public String getDescription();

}
