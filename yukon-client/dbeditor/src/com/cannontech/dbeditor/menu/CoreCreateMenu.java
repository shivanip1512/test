package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import java.util.Set;

import com.cannontech.common.gui.util.CommandableMenuItem;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Sets;

public class CoreCreateMenu extends javax.swing.JMenu {

	//core wizards
	public CommandableMenuItem portMenuItem;
	public CommandableMenuItem deviceMenuItem;
	public CommandableMenuItem routeMenuItem;
	public CommandableMenuItem pointMenuItem;	
	public CommandableMenuItem stateGroupMenuItem;
	public CommandableMenuItem billingGroupMenuItem;
	public CommandableMenuItem config2WayMenuItem;
/**
 * CreateMenu constructor comment.
 */
public CoreCreateMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);
	
	portMenuItem = new CommandableMenuItem("Comm Channel...");
	portMenuItem.setFont( font );
	portMenuItem.setMnemonic('o');

	deviceMenuItem = new CommandableMenuItem("Device...");
	deviceMenuItem.setFont( font );
	deviceMenuItem.setMnemonic('d');

	routeMenuItem = new CommandableMenuItem("Route...");
	routeMenuItem.setFont( font );
	routeMenuItem.setMnemonic('r');

	pointMenuItem = new CommandableMenuItem("Point...");
	pointMenuItem.setFont( font );
	pointMenuItem.setMnemonic('p');

	stateGroupMenuItem = new CommandableMenuItem("State Group...");
	stateGroupMenuItem.setFont( font );
	stateGroupMenuItem.setMnemonic('s');

	billingGroupMenuItem = new CommandableMenuItem("Billing File...");
	billingGroupMenuItem.setFont( font );
	billingGroupMenuItem.setMnemonic('b');
	
	config2WayMenuItem = new CommandableMenuItem("MCT Config...");
	config2WayMenuItem.setFont( font );
	config2WayMenuItem.setMnemonic('m');
    
	setText("Create");
	setFont( font );
	setMnemonic('c');

	//keep and add to these in alphabetical order
	add( billingGroupMenuItem );
	add( portMenuItem );
	add( deviceMenuItem );

	// Build up a set of mct200/300 series definitions that are creatable
	PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
	Set<PaoDefinition> mct200Series = paoDefinitionDao.getPaosThatSupportTag(PaoTag.MCT_200_SERIES);
	Set<PaoDefinition> mct300Series = paoDefinitionDao.getPaosThatSupportTag(PaoTag.MCT_300_SERIES);
	Set<PaoDefinition> mcts = Sets.union(mct200Series, mct300Series);
	Set<PaoDefinition> creatable = paoDefinitionDao.getCreatablePaoDefinitions();
	Set<PaoDefinition> creatableMctSeries = Sets.intersection(mcts, creatable);
	
	if (!creatableMctSeries.isEmpty()) {
	    add( config2WayMenuItem );
    }
	add( pointMenuItem );
	add( routeMenuItem );
	add( stateGroupMenuItem );
}
}
