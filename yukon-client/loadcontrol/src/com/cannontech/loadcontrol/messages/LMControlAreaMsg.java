package com.cannontech.loadcontrol.messages;

/**
 */

import com.cannontech.loadcontrol.data.LMControlArea;

public class LMControlAreaMsg extends LMMessage
{
	private java.lang.Integer msgInfoBitMask;
	private java.util.Vector lmControlAreaVector;
	
	public static final int AREA_ALL = 0x00000001;
	public static final int AREA_DELETE = 0x00000002;


/**
 * LMControlAreaMsg constructor comment.
 */
public LMControlAreaMsg() {
	super();
}

public boolean isDeletedCntrlArea()
{
	return (getMsgInfoBitMask().intValue() & AREA_DELETE) > 0;
}

/**
 * This method was created in VisualAge.
 */
public LMControlArea getLMControlArea(int index) {
	return (LMControlArea)lmControlAreaVector.get(index);
}
/**
 * This method was created in VisualAge.
 */
java.util.Vector getLMControlAreaVector()
{
	return lmControlAreaVector;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfLMControlAreas() {
	return lmControlAreaVector.size();
}
/**
 * This method was created in VisualAge.
 */
void setLMControlAreaVector(java.util.Vector lmContAreas)
{
	lmControlAreaVector = lmContAreas;
}
	/**
	 * @return
	 */
	public java.lang.Integer getMsgInfoBitMask() {
		return msgInfoBitMask;
	}

	/**
	 * @param integer
	 */
	public void setMsgInfoBitMask(java.lang.Integer integer) {
		msgInfoBitMask = integer;
	}

}
