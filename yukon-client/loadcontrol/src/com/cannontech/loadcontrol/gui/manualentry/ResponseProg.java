package com.cannontech.loadcontrol.gui.manualentry;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.loadcontrol.data.LMProgramBase;

/**
 * @author rneuharth
 *
 * Holder for each constraint violation.
 * 
 */
public class ResponseProg
{
	private LMProgramBase program = null;
	private Integer gearNum = null;
	
	private ArrayList violations = new ArrayList(8);
	private String action = null;
	private int status = 0;
	private Boolean override = Boolean.FALSE;


	/**
	 * 
	 */
	public ResponseProg()
	{
		this( null, null );
	}

	public ResponseProg( LMProgramBase prog, Integer gearNumber )
	{
		super();
		setProgram( prog );
		setGearNum( gearNumber );
	}

	/**
	 * @return
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @return
	 */
	public LMProgramBase getProgram()
	{
		return program;
	}

	/**
	 * @return
	 */
	public void addViolation( String v )
	{
		violations.add( v );
	}

	/**
	 * @return
	 */
	public List getViolations()
	{
		return violations;
	}

	/**
	 * @param string
	 */
	public void setAction(String string)
	{
		action = string;
	}

	/**
	 * @param base
	 */
	public void setProgram(LMProgramBase base)
	{
		program = base;
	}


	/**
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * @param string
	 */
	public void setStatus(int string)
	{
		status = string;
	}

	/**
	 * @return
	 */
	public Boolean getOverride()
	{
		return override;
	}

	/**
	 * @param boolean1
	 */
	public void setOverride(Boolean boolean1)
	{
		override = boolean1;
	}

	/**
	 * @return
	 */
	public Integer getGearNum()
	{
		return gearNum;
	}

	/**
	 * @param integer
	 */
	public void setGearNum(Integer integer)
	{
		gearNum = integer;
	}

}
