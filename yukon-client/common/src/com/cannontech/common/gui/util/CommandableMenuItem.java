package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.util.Command;
import com.cannontech.common.util.CommandExecutionException;

public class CommandableMenuItem extends javax.swing.JMenuItem implements java.awt.event.ActionListener {
	private Command commandToExecute;	
/**
 * CommandableMenuItem constructor comment.
 */
public CommandableMenuItem() {
	super();
	initialize();
}
/**
 * CommandableMenuItem constructor comment.
 * @param text java.lang.String
 */
public CommandableMenuItem(String text) {
	super(text);
	initialize();
}
/**
 * CommandableMenuItem constructor comment.
 * @param text java.lang.String
 * @param mnemonic int
 */
public CommandableMenuItem(String text, int mnemonic) {
	super(text, mnemonic);
	initialize();
}
/**
 * CommandableMenuItem constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 */
public CommandableMenuItem(String text, javax.swing.Icon icon) {
	super(text, icon);
	initialize();
}
/**
 * CommandableMenuItem constructor comment.
 * @param icon javax.swing.Icon
 */
public CommandableMenuItem(javax.swing.Icon icon) {
	super(icon);
	initialize();
}
/**
 * This method execute the command associated with this menu item if any.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event) {

	//Make sure its our event and we have a Command to execute
	if( event.getSource() == this && commandToExecute != null)
	{
		try
		{
			this.commandToExecute.execute();
		}
		catch(CommandExecutionException c )
		{
			c.printStackTrace();
		}
	}
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	addActionListener( this );
}
/**
 * This method was created in VisualAge.
 * @param command com.cannontech.common.util.Command
 */
public void setCommand(Command command) {
	this.commandToExecute = command;
}
}
