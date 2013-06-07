package com.cannontech.loadcontrol.gui.manualentry;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

/**
 * @author rneuharth
 *
 * Holder for each constraint violation.
 * 
 */
public class ResponseProg
{
	private List<ConstraintContainer> violations = new ArrayList<ConstraintContainer>(8);
	private String noViolationsMessage = null;
	private String action = NONE_ACTION;
	private int status = ServerResponseMessage.STATUS_UNINIT;
	private Boolean override = Boolean.FALSE;

	private Program lmProgramBase = null;

	//the request messages that created this response
	private ManualControlRequestMessage lmRequest = null;


	public static final String NONE_ACTION = "";
	public static final String NO_VIOLATION_ACTION = "No Violation";

	/**
	 * 
	 */
	public ResponseProg()
	{
		this( null, null );
	}

	public ResponseProg( ManualControlRequestMessage req, Program lmProg )
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
	public void setViolations( List<ConstraintContainer> violations) {
		this.violations = violations;
	}

	/**
	 * @return
	 */
	public void addViolation( ConstraintContainer constraintContainer )
	{
		violations.add( constraintContainer );
	}

	/**
	 * @return
	 */
	public List<ConstraintContainer> getViolations()
	{
		return violations;
	}

	/**
	 * @return
	 */
	public List<String> getViolationsAsString()
	{
		TemplateProcessorFactory processorFactory = YukonSpringHook.getBean(TemplateProcessorFactory.class);
		YukonUserContext userContext = ClientSession.getUserContext();
		
		List<String> violationStrings = Lists.newArrayList();
		for( int i = 0; i < getViolations().size(); i++ ) {
			violationStrings.add(processorFactory.processResolvableTemplate(getViolations().get(i).getConstraintTemplate(), 
														   userContext));
		}
		return violationStrings;
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
	public ManualControlRequestMessage getLmRequest()
	{
		return lmRequest;
	}

	/**
	 * @param request
	 */
	public void setLmRequest(ManualControlRequestMessage request)
	{
		lmRequest = request;
	}

	/**
	 * @return
	 */
	public Program getLmProgramBase()
	{
		return lmProgramBase;
	}

	/**
	 * @param base
	 */
	public void setLmProgramBase(Program base)
	{
		lmProgramBase = base;
	}

	public String getNoViolationsMessage() {
		return noViolationsMessage;
	}

	public void setNoViolationsMessage(String noViolationsMessage) {
		this.noViolationsMessage = noViolationsMessage;
	}

}
