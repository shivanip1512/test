package com.cannontech.common.util;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface Command {


	void execute() throws com.cannontech.common.util.CommandExecutionException;
}
