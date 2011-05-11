package com.cannontech.common.constants;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class YukonListEntry 
{
	private int entryID = 0;
	private int listID = 0;
	private int entryOrder = 0;
	private String entryText = null;
	private int yukonDefID = 0;

	public static final String TABLE_NAME = "YukonListEntry";

	/**
	 * Constructor for YukonListEntry.
	 */
	public YukonListEntry() {
		super();
	}

	/**
	 * Returns the entryID.
	 * @return int
	 */
	public int getEntryID() {
		return entryID;
	}

	/**
	 * Returns the raw entryText.  If you want to display this in the UI, you should use
	 * getEntryTextMsr() instead.
	 * @see #getEntryTextMsr()
	 */
	public String getEntryText() {
		return entryText;
	}

	/**
	 * Get the entry text as a MessageSourceResolvable for use in the UI.
	 */
	public MessageSourceResolvable getEntryTextMsr() {
        String code = MessageCodeGenerator.generateCode("yukon.common.list.entry", entryText);
        return YukonMessageSourceResolvable.createDefault(code, entryText);
	}

	/**
	 * Returns the listID.
	 * @return int
	 */
	public int getListID() {
		return listID;
	}

	/**
	 * Returns the listOrder.
	 * @return int
	 */
	public int getEntryOrder() {
		return entryOrder;
	}

	/**
	 * Returns the yukonDefID.
	 * @return int
	 */
	public int getYukonDefID() {
		return yukonDefID;
	}

	public YukonDefinition getDefinition() {
	    return YukonDefinition.getById(yukonDefID);
	}

	/**
	 * Sets the entryID.
	 * @param entryID The entryID to set
	 */
	public void setEntryID(int entryID) {
		this.entryID = entryID;
	}

	/**
	 * Sets the entryText.
	 * @param entryText The entryText to set
	 */
	public void setEntryText(String entryText) {
		this.entryText = entryText;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(int listID) {
		this.listID = listID;
	}

	/**
	 * Sets the listOrder.
	 * @param listOrder The listOrder to set
	 */
	public void setEntryOrder(int entryOrder) {
		this.entryOrder = entryOrder;
	}

	/**
	 * Sets the yukonDefID.
	 * @param yukonDefID The yukonDefID to set
	 */
	public void setYukonDefID(int yukonDefID) {
		this.yukonDefID = yukonDefID;
	}

	/**
	 * Returns the toSting() call
	 * @return String
	 */
	public String toString() {
		return getEntryText();
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + entryID;
        result = prime * result + entryOrder;
        result = prime * result + ((entryText == null) ? 0 : entryText.hashCode());
        result = prime * result + listID;
        result = prime * result + yukonDefID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        YukonListEntry other = (YukonListEntry) obj;
        if (entryID != other.entryID)
            return false;
        if (entryOrder != other.entryOrder)
            return false;
        if (entryText == null) {
            if (other.entryText != null)
                return false;
        } else if (!entryText.equals(other.entryText))
            return false;
        if (listID != other.listID)
            return false;
        if (yukonDefID != other.yukonDefID)
            return false;
        return true;
    }

	
	
}