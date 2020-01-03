package com.cannontech.multispeak.block.data.v5;

import java.math.BigInteger;
import java.util.List;

import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.cannontech.msp.beans.v5.enumerations.FieldNameType;
import com.cannontech.msp.beans.v5.enumerations.Uom;
import com.cannontech.msp.beans.v5.multispeak.ContentModelGroup;
import com.cannontech.msp.beans.v5.multispeak.FormatChoice;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlockMetaData;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeChoice;
import com.cannontech.msp.beans.v5.multispeak.ValSyntax;
import com.cannontech.msp.beans.v5.multispeak.ValueList;
import com.cannontech.multispeak.block.syntax.v5.SyntaxItem;
import com.cannontech.multispeak.block.v5.Block;

public abstract class FormattedBlockBase extends FormattedBlock {

    public List<? extends Block> block;
    private String separator;
    private ValSyntax valSyntax;

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
    public ValueList getValueList() {
        ValueList valueList = new ValueList();
        List<String> stringList = valueList.getVal();
        for (Block block : this.block) {
            stringList.add(getValue(block));
        }
        return valueList;
    }

    /**
     * Returns the valSyntax String.
     * 
     * @param Block
     * @param separator
     * @return
     */
    private String getValue(Block block) {

        StringBuffer value = new StringBuffer();
        final SyntaxItem[] syntaxItems = getSyntaxItems();
        for (int i = 0; i < syntaxItems.length; i++) {
            if (i == syntaxItems.length - 1) // no separator on last item
                appendField(value, block.getField(syntaxItems[i]), "");
            else
                appendField(value, block.getField(syntaxItems[i]), getSeparator());
        }
        return value.toString();
    }

    /**
     * Helper method to append a (String)field and separator to a (StringBuffer)string
     * 
     * @param string
     * @param field
     * @param separator
     * @return
     */
    private StringBuffer appendField(StringBuffer string, String field, String separator) {
        string.append(field);
        string.append(separator);
        return string;
    }

    /**
     * Loads the (Msp)SyntaxItem array based on the local syntaxItem object.
     */
    private void setMspSyntaxItems(SyntaxItem[] syntaxItem) {
        ValSyntax valSyntax = new ValSyntax();
        List<com.cannontech.msp.beans.v5.multispeak.SyntaxItem> mspSyntaxItems = valSyntax.getSyntaxItem();
        for (int i = 0; i < syntaxItem.length; i++)
            mspSyntaxItems.add(i, buildSyntaxItem(syntaxItem[i], i));

        setValSyntax(valSyntax);
    }

    /**
     * Helper method to translate a (Yukon) SyntaxItem into an (Msp) SyntaxItem
     * 
     * @param syntaxItem
     * @param position
     * @return
     */
    private com.cannontech.msp.beans.v5.multispeak.SyntaxItem buildSyntaxItem(SyntaxItem syntaxItem, int position) {

        com.cannontech.msp.beans.v5.multispeak.SyntaxItem mspSyntaxItem =
            new com.cannontech.msp.beans.v5.multispeak.SyntaxItem();

        ReadingTypeChoice readingTypeChoice = new ReadingTypeChoice();
        FieldNameType fieldNameType = new FieldNameType();
        // TODO confirm this change set the fieldName in fieldKind if field present in SyntaxItem otherwise
        // set in otherKind
        
        FieldNameKind fieldNameKind = syntaxItem.getMspFieldNameKind();
        fieldNameType.setValue(fieldNameKind);
        if (fieldNameKind == FieldNameKind.OTHER) {
            fieldNameType.setOtherKind(syntaxItem.getMspFieldName());
        }

        readingTypeChoice.setFieldName(fieldNameType);
        mspSyntaxItem.setReadingTypeChoice(readingTypeChoice);
        mspSyntaxItem.setPosition(BigInteger.valueOf(position));

        Uom uom = new Uom();
        uom.setValue(syntaxItem.getMspUom());
        mspSyntaxItem.setUom(uom);
        return mspSyntaxItem;
    }

    /**
     * Helper method to construct a MultiSpeak FormattedBlock object
     * @param blockBase
     * @return
     */
    public static FormattedBlock createMspFormattedBlock(FormattedBlockBase blockBase, String nounType) {
        FormattedBlock mspFormattedBlock = new FormattedBlock();
        FormatChoice formatChoice = new FormatChoice();
        FormattedBlockMetaData formattedBlockMetaData = new FormattedBlockMetaData();
        ContentModelGroup modelGroup = new ContentModelGroup();
        
        // TODO The noun describes the type of object or data that is to be included in the payload
        modelGroup.setContentNounType(nounType);
        formattedBlockMetaData.setSeparator(blockBase.getSeparator());
        formattedBlockMetaData.setValSyntax(blockBase.getValSyntax());
        formatChoice.setFormattedBlockMetaData(formattedBlockMetaData);
        mspFormattedBlock.setFormatChoice(formatChoice);
        mspFormattedBlock.setValueList(blockBase.getValueList());
        mspFormattedBlock.setContentModelGroup(modelGroup);
        return mspFormattedBlock;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String value) {
        this.separator = value;
    }

    public ValSyntax getValSyntax() {
        return valSyntax;
    }

    public void setValSyntax(ValSyntax value) {
        this.valSyntax = value;
    }
}
