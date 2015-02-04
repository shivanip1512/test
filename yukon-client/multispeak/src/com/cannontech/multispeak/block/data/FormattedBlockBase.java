package com.cannontech.multispeak.block.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.multispeak.deploy.service.FormattedBlock;

public abstract class FormattedBlockBase extends FormattedBlock{

    public List<? extends Block> block;
    
    public FormattedBlockBase(List<? extends Block> block) {
        this.block = block;
        setSeparator(",");
        setMspSyntaxItems(getSyntaxItems());
    }
   
    public abstract SyntaxItem[] getSyntaxItems();
    
    public List<? extends Block> getBlock() {
        return block;
    }
    
    @Override
    public String[] getValueList() {
        List<String> stringList = new ArrayList<String>(block.size());
        for (Block block : this.block) {
            stringList.add(getValue(block));
        }
        String[] strings = new String[stringList.size()];
        stringList.toArray(strings);
        return strings;
    }

    /**
     * Returns the valSyntax String. 
     * @param Block
     * @param separator
     * @return 
     */
    private String getValue(Block block) {
        
        StringBuffer value = new StringBuffer();
        final SyntaxItem[] syntaxItems = getSyntaxItems(); 
        for (int i = 0; i < syntaxItems.length; i++) {
            if( i == syntaxItems.length-1)   //no separator on last item
                appendField(value, block.getField(syntaxItems[i]), "");
            else
                appendField(value, block.getField(syntaxItems[i]), getSeparator());
        }
        return value.toString();
    }
    
    /**
     * Helper method to append a (String)field and separator to a (StringBuffer)string  
     * @param string
     * @param field
     * @param separator
     * @return
     */
    private StringBuffer appendField(StringBuffer string, String field, String separator){
        string.append(field);
        string.append(separator);
        return string;
    } 
    
    /**
     * Loads the (Msp)SyntaxItem array based on the local syntaxItem object.
     */
    private void setMspSyntaxItems(SyntaxItem[] syntaxItem) {
        com.cannontech.multispeak.deploy.service.SyntaxItem[] mspSyntaxItems = 
            new com.cannontech.multispeak.deploy.service.SyntaxItem[syntaxItem.length];
        for (int i = 0; i < syntaxItem.length; i++)
            mspSyntaxItems[i] = buildSyntaxItem(syntaxItem[i], i);
        setValSyntax(mspSyntaxItems);
    }
    
    /**
     * Helper method to translate a (Yukon) SyntaxItem into an (Msp) SyntaxItem 
     * @param syntaxItem
     * @param position
     * @return
     */
    private com.cannontech.multispeak.deploy.service.SyntaxItem buildSyntaxItem (
            SyntaxItem syntaxItem, int position) {
        
        com.cannontech.multispeak.deploy.service.SyntaxItem mspSyntaxItem = 
            new com.cannontech.multispeak.deploy.service.SyntaxItem();
        
        mspSyntaxItem.setFieldName(syntaxItem.getMspFieldName());
        mspSyntaxItem.setPosition(BigInteger.valueOf(position));
        mspSyntaxItem.setUom(syntaxItem.getMspUom());
        return mspSyntaxItem;
    }
    
      /**
      * Helper method to construct a MultiSpeak FormattedBlock object
      * @param blockBase
      * @return
      */
     public static FormattedBlock createMspFormattedBlock(FormattedBlockBase blockBase) {
         FormattedBlock mspFormattedBlock = new FormattedBlock();
         mspFormattedBlock.setValueList(blockBase.getValueList());
         mspFormattedBlock.setSeparator(blockBase.getSeparator());
         mspFormattedBlock.setValSyntax(blockBase.getValSyntax());
         return mspFormattedBlock;
     }
}
