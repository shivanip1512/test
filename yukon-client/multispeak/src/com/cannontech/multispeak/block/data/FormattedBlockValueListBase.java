package com.cannontech.multispeak.block.data;

import java.util.List;

import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.data.FormattedBlockValSyntaxBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.multispeak.service.FormattedBlockValueList;

public class FormattedBlockValueListBase extends FormattedBlockValueList{

    public String separator = ",";
    public List<? extends Block> block;
    public FormattedBlockValSyntaxBase valSyntax;

    public FormattedBlockValueListBase(List<? extends Block> block, SyntaxItem[] syntaxItem) {
        this.block = block;
        setValSyntax(syntaxItem);
    }
    
    public String getSeparator() {
        return separator;
    }
    
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    public FormattedBlockValSyntaxBase getValSyntax() {
        return valSyntax;
    }
    
    public void setValSyntax(SyntaxItem[] syntaxItem) {
        valSyntax = new FormattedBlockValSyntaxBase(syntaxItem);
    }
    
    public List<? extends Block> getBlock() {
        return block;
    }
    
    @Override
    public String[] getVal() {
        String[] string = new String[block.size()];
        for (int i = 0; i < string.length; i++) {
            string[i] = getVal(i);
        }
        return string;
    }

    @Override
    public String getVal(int i) {
        return valSyntax.getValue(block.get(i), separator);
    }
}
