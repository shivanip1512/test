package com.cannontech.multispeak.block;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.syntax.SyntaxItem;

public interface Block {
    
    /**
     * Returns the Block field associated with syntaxItem
     * @param syntaxItem
     * @return
     */
    public String getField(SyntaxItem syntaxItem);
    
    /**
     * Populate the Block object with meter and pointValue data.
     * @param meter
     * @param pointValue
     */
    public void populate(Meter meter, PointValueHolder pointValue);
    
    /** 
     * Helper method to (reverse) populate the block by parsing a string. 
     * @param string
     * @param separator
     */
    public void populate(String string, char separator);
    
    /**
     * Returns the objectId value to be used for the Block data object.
     * @return
     */
    public String getObjectId();
    
    /**
     * Returns true when Block has any data populated.
     * @return
     */
    public boolean hasData();
    
	/**
     * Helper method to load the fields based on the attribute.
     * This method assumes the pointValue matches the attribute provided.
	 * @param meter
	 * @param pointValue
	 * @param attribute
	 */
    public void populate(Meter meter, PointValueHolder pointValue, BuiltInAttribute attribute);
}
