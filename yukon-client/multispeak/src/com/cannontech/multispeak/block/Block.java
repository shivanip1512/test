package com.cannontech.multispeak.block;

import com.cannontech.multispeak.block.syntax.SyntaxItem;

public interface Block {
    
    /**
     * Returns the Block field associated with syntaxItem
     * @param syntaxItem
     * @return
     */
    public String getField(SyntaxItem syntaxItem);
    
   /**
     * Returns the objectId value to be used for the Block data object.
     * @return
     */
    public String getObjectId();
    
    /**
     * Returns true when Block has any reading data populated.
     * @return
     */
    public boolean hasData();
    
}
