package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.cannontech.common.util.Command;
import com.cannontech.common.util.CommandExecutionException;

public class CommandableTreeSelectionModel extends javax.swing.tree.DefaultTreeSelectionModel implements TreeSelectionListener{

	private Command commandToExecute;
/**
 * CommandableTreeSelectionModel constructor comment.
 */
public CommandableTreeSelectionModel() {
	super();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	addTreeSelectionListener(this);
}
/**
 * This method was created in VisualAge.
 * @param command com.cannontech.common.util.Command
 */
public void setCommand(Command command) {

	this.commandToExecute = command;
}
/**
 * This method was created in VisualAge.
 * @param event TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent event) {

	try
	{
		this.commandToExecute.execute();
	}
	catch( CommandExecutionException e )
	{
		e.printStackTrace();
	}
		
}
}
