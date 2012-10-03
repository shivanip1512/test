package com.cannontech.dbeditor.editor.user;

import java.util.ArrayList;
import java.util.Iterator;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SelectableGroupRow
{
	private LiteYukonGroup liteYukonGroup = null;
	private Boolean isSelected = new Boolean(false);
	
	private ArrayList conflicts = new ArrayList(8);
	

	/**
	 * 
	 */
	public SelectableGroupRow()
	{
		super();
	}

	public void addConflict( LiteYukonRole role_ )
	{
		conflicts.add( role_ );
	}

	public Iterator getConflictIter()
	{
		return conflicts.iterator();		
	}
	
	public void clearConflicts()
	{
		conflicts.clear();
	}

	/**
	 * @return
	 */
	public Boolean isSelected()
	{
		return isSelected;
	}

	/**
	 * @return
	 */
	public LiteYukonGroup getLiteYukonGroup()
	{
		return liteYukonGroup;
	}

	/**
	 * @param boolean1
	 */
	public void selected(Boolean boolean1)
	{
		isSelected = boolean1;
	}

	/**
	 * @param group
	 */
	public void setLiteYukonGroup(LiteYukonGroup group)
	{
		liteYukonGroup = group;
	}


	/**
	 * @return
	 */
	public boolean hasConflict()
	{
		return !conflicts.isEmpty();
	}


}
