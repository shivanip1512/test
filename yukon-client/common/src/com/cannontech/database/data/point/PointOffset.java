package com.cannontech.database.data.point;

import com.cannontech.database.data.pao.PAOGroups;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointOffset implements IPointOffsets
{
	private int paoType = PAOGroups.INVALID;
	private int pointType = PointTypes.INVALID_POINT;
	
	//parallel arrays of the value and its corresponding description
	private int[] values = null;
	private String[] descriptions = null;

	/**
	 * 
	 */
	public PointOffset( int paoType_, int pointType_, int values_[], String descriptions_[] )
	{
		super();

		setPaoType( paoType_ );
		setPointType( pointType_ );
		setValues( values_ );
		setDescriptions( descriptions_ );
	}

	/**
	 * @return
	 */
	public int getPaoType()
	{
		return paoType;
	}

	/**
	 * @param i
	 */
	public void setPaoType(int i)
	{
		paoType = i;
	}

	/**
	 * @return
	 */
	public int getPointType()
	{
		return pointType;
	}

	/**
	 * @param i
	 */
	public void setPointType(int i)
	{
		pointType = i;
	}

	/**
	 * @return
	 */
	public String[] getDescriptions()
	{
		return descriptions;
	}

	/**
	 * @return
	 */
	public int[] getValues()
	{
		return values;
	}

	/**
	 * @param strings
	 */
	public void setDescriptions(String[] strings)
	{
		descriptions = strings;
	}

	/**
	 * @param is
	 */
	public void setValues(int[] is)
	{
		values = is;
	}

}
