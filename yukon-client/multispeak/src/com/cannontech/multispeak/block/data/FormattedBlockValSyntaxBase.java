package com.cannontech.multispeak.block.data;

import java.math.BigInteger;

import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.multispeak.service.FormattedBlockValSyntax;

public class FormattedBlockValSyntaxBase extends FormattedBlockValSyntax{

    //The order of this array specifies the position for the FormattedBlockValSyntax
    public SyntaxItem[] syntaxItem;

    public FormattedBlockValSyntaxBase(SyntaxItem[] syntaxItem) {
        super();
        setSyntaxItem(syntaxItem);
    }
    
    /**
     * Returns the valSyntax String. 
     * @param Block
     * @param separator
     * @return 
     */
    public String getValue(Block block, String separator) {
        
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < syntaxItem.length; i++) {
        	if( i == syntaxItem.length-1)	//no separator on last item
        		appendField(value, block.getField(syntaxItem[i]), "");
        	else
        		appendField(value, block.getField(syntaxItem[i]), separator);
        }
        return value.toString();
    }
    
    /**
     * Sets the (yukon)syntaxItem and loads the (Msp)SyntaxItem array based on the local syntaxItem object.
     */
    public void setSyntaxItem(SyntaxItem[] syntaxItem) {
        this.syntaxItem = syntaxItem;
        setSyntaxItem(new com.cannontech.multispeak.service.SyntaxItem[syntaxItem.length]);
        for (int i = 0; i < syntaxItem.length; i++)
            setSyntaxItem(i, getSyntaxItem(syntaxItem[i], i));
    }
    
    /**
     * Helper method to translate a (Yukon) SyntaxItem into an (Msp) SyntaxItem 
     * @param syntaxItem
     * @param position
     * @return
     */
    public com.cannontech.multispeak.service.SyntaxItem getSyntaxItem (
            SyntaxItem syntaxItem, int position) {
        
        com.cannontech.multispeak.service.SyntaxItem mspSyntaxItem = 
            new com.cannontech.multispeak.service.SyntaxItem();
        
        mspSyntaxItem.setFieldName(syntaxItem.getMspFieldName());
        mspSyntaxItem.setPosition(BigInteger.valueOf(position));
        mspSyntaxItem.setUom(syntaxItem.getMspUom());
        return mspSyntaxItem;
    }
    
    /**
     * Helper method to append a (String)field and separator to a (StringBuffer)string  
     * @param string
     * @param field
     * @param separator
     * @return
     */
    public StringBuffer appendField(StringBuffer string, String field, String separator){
        string.append(field);
        string.append(separator);
        return string;
    }    
}

