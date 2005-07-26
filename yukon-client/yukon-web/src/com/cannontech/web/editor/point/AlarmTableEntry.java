package com.cannontech.web.editor.point;

/**
 * @author ryan
 *
 */
public class AlarmTableEntry
{
	private String condition = null;
	private String generate = null;
	private String excludeNotify = null;

	/**
	 * 
	 */
	public AlarmTableEntry()
	{
		super();
	}

	/**
	 * @return
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * @return
	 */
	public String getExcludeNotify()
	{
		return excludeNotify;
	}

	/**
	 * @return
	 */
	public String getGenerate()
	{
		return generate;
	}

	/**
	 * @param string
	 */
	public void setCondition(String string)
	{
		condition = string;
	}

	/**
	 * @param string
	 */
	public void setExcludeNotify(String string)
	{
		excludeNotify = string;
	}

	/**
	 * @param string
	 */
	public void setGenerate(String string)
	{
		generate = string;
	}

}
