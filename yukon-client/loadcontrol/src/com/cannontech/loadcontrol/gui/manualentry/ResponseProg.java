package com.cannontech.loadcontrol.gui.manualentry;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

/**
 * @author rneuharth
 *
 * Holder for each constraint violation.
 * 
 */
public class ResponseProg
{
	private ArrayList violations = new ArrayList(8);
	private String action = null;
	private int status = 0;
	private Boolean override = Boolean.FALSE;

	private LMProgramBase lmProgramBase = null;

	//the request messages that created this response
	private LMManualControlRequest lmRequest = null;


	/**
	 * 
	 */
	public ResponseProg()
	{
		this( null, null );
	}

	public ResponseProg( LMManualControlRequest req, LMProgramBase lmProg )
	{
		super();
		setLmRequest( req );
		setLmProgramBase( lmProg );
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
	 * @return
	 */
	public String getViolationsAsString()
	{
		StringBuffer buff = new StringBuffer();
		for( int i = 0; i < getViolations().size(); i++ )
			buff.append(
				(i > 0 ? ". " : "") +
				getViolations().get(i).toString() );
		
		return buff.toString();
	}

	/**
	 * @param string
	 */
	public void setAction(String string)
	{
		action = string;
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
	public LMManualControlRequest getLmRequest()
	{
		return lmRequest;
	}

	/**
	 * @param request
	 */
	public void setLmRequest(LMManualControlRequest request)
	{
		lmRequest = request;
	}

	/**
	 * @return
	 */
	public LMProgramBase getLmProgramBase()
	{
		return lmProgramBase;
	}

	/**
	 * @param base
	 */
	public void setLmProgramBase(LMProgramBase base)
	{
		lmProgramBase = base;
	}

}
