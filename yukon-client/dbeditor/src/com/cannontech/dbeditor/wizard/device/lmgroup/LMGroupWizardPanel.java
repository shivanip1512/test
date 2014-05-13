package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ClientRights;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.pao.PAOGroups;
/**
 * This type was created in VisualAge.
 */

public class LMGroupWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{ 
    private GroupTypePanel groupTypePanel;
    private SwitchTypePanel switchTypePanel;
    
    private LMGroupBaseWizardPanel lmGroupBasePanel = null;
    private GroupMacroLoadGroupsPanel groupMacroLoadGroupsPanel;
    
    private LMGroupVersacomEditorPanel lmGroupVersacomEditorPanel = null;
    private LMGroupEmetconPanel lmGroupEmetconPanel = null;    
    private RippleMessageShedPanel rippleMessageShedPanel = null;
    private SpecialRippleMessagePanel specialRippleMessagePanel = null;
    private LMGroupExpressComEditorPanel lmGroupExpressComEditorPanel = null;
    private LMGroupDigiSepPanel lmGroupDigiSepEditorPanel = null;
    private LMGroupPointEditorPanel lmGroupPointEditorPanel = null;
    
    private LMGroupMCTEditorPanel lmGroupMCTEditorPanel = null;

    private GolayEditorPanel golayEditorPanel = null;
    private SA305EditorPanel aSA305EditorPanel = null;
    private SA205EditorPanel aSA205EditorPanel = null;
    private SADigitalEditorPanel aSADigitalEditorPanel = null;
    
    //hex value representing some privileges of the user on this machine
    public static final long SPECIAL_RIPPLE = Long.parseLong( 
        ClientSession.getInstance().getRolePropertyValue(
        YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV), 16 );

/**
 * LMDeviceWizardPanel constructor comment.
 */
public LMGroupWizardPanel() {
    super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getActualSize() 
{
    setPreferredSize( new java.awt.Dimension(410, 515) );

    return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return LMGroupBasePanel
 */


public GolayEditorPanel getGolayEditorPanel() 
{
    if( golayEditorPanel == null )
        golayEditorPanel = new GolayEditorPanel();
    
    return golayEditorPanel;
}

public SA305EditorPanel getSA305EditorPanel() 
{
    if( aSA305EditorPanel == null )
        aSA305EditorPanel = new SA305EditorPanel();
    
    return aSA305EditorPanel;
}

public LMGroupDigiSepPanel getlmGroupDigiSepEditorPanel()
{
    if( lmGroupDigiSepEditorPanel == null )
        lmGroupDigiSepEditorPanel = new LMGroupDigiSepPanel();
    
    return lmGroupDigiSepEditorPanel;
}

public SA205EditorPanel getSA205EditorPanel() 
{
    if( aSA205EditorPanel == null )
        aSA205EditorPanel = new SA205EditorPanel();
    
    return aSA205EditorPanel;
}

public SADigitalEditorPanel getSADigitalEditorPanel() 
{
    if( aSADigitalEditorPanel == null )
        aSADigitalEditorPanel = new SADigitalEditorPanel();
    
    return aSADigitalEditorPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 4:56:10 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.GroupMacroLoadGroupsPanel
 */
public GroupMacroLoadGroupsPanel getGroupMacroLoadGroupsPanel()
{
    if (groupMacroLoadGroupsPanel == null)
        groupMacroLoadGroupsPanel = new GroupMacroLoadGroupsPanel();
    return groupMacroLoadGroupsPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 4:27:39 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.GroupTypePanel
 */
public GroupTypePanel getGroupTypePanel() {
    if (groupTypePanel == null)
    groupTypePanel = new GroupTypePanel();
    return groupTypePanel;
}


/**
 * getHeaderText method comment.
 */
@Override
protected String getHeaderText() {
    return "LM Group Setup";
}


/**
 * This method was created in VisualAge.
 * @return LMGroupBasePanel
 */
public LMGroupBaseWizardPanel getLMGroupBasePanel() 
{
    if( lmGroupBasePanel == null )
        lmGroupBasePanel = new LMGroupBaseWizardPanel( true );
    
    return lmGroupBasePanel;
}


/**
 * This method was created in VisualAge.
 * @return     LMGroupEmetconPanel
 */
public LMGroupEmetconPanel getLMGroupEmetconPanel() 
{
    if (lmGroupEmetconPanel== null)
        lmGroupEmetconPanel = new LMGroupEmetconPanel();
        
    return lmGroupEmetconPanel;
}


/**
 * Insert the method's description here.
 * @return LMGroupExpressComEditorPanel
 */
public LMGroupExpressComEditorPanel getLMGroupExpressComEditorPanel()
{
    if( lmGroupExpressComEditorPanel == null)
        lmGroupExpressComEditorPanel= new LMGroupExpressComEditorPanel();

    return lmGroupExpressComEditorPanel;
}


/**
 * This method was created in VisualAge.
 * @return LMGroupBasePanel
 */
public LMGroupMCTEditorPanel getLMGroupMCTEditorPanel() 
{
    if( lmGroupMCTEditorPanel == null )
        lmGroupMCTEditorPanel = new LMGroupMCTEditorPanel();
    
    return lmGroupMCTEditorPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 3:40:50 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupPointEditorPanel
 */
public LMGroupPointEditorPanel getLmGroupPointEditorPanel() 
{
    if( lmGroupPointEditorPanel == null )
        lmGroupPointEditorPanel = new LMGroupPointEditorPanel();

    return lmGroupPointEditorPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2001 9:30:58 AM)
 * @return LMGroupVersacomEditorPanel
 */
public LMGroupVersacomEditorPanel getLmGroupVersacomEditorPanel() 
{
    if( lmGroupVersacomEditorPanel == null )
        lmGroupVersacomEditorPanel = new LMGroupVersacomEditorPanel();

    return lmGroupVersacomEditorPanel;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getMinimumSize() 
{
    return getPreferredSize();
}


/**
 * getNextInputPanel method comment.
 */
@Override
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
    com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{

    if (currentInputPanel == null)
    {
        getGroupTypePanel().setFirstFocus();
        return getGroupTypePanel();
    }
    else if (currentInputPanel == getGroupTypePanel())
    {
        if (getGroupTypePanel().isGroupMacro())
        {
            getLMGroupBasePanel().setSwitchType( 
                PaoType.getPaoTypeString(PAOGroups.MACRO_GROUP ));
            getLMGroupBasePanel().setFirstFocus();
            return getLMGroupBasePanel();
        }
        else
            getSwitchTypePanel().setFirstFocus();
            return getSwitchTypePanel();
    }
    else if (currentInputPanel == getLMGroupBasePanel() && getGroupTypePanel().isGroupMacro())
    {
        getGroupMacroLoadGroupsPanel().setFirstFocus();
        return getGroupMacroLoadGroupsPanel();
    }
    else if (currentInputPanel == getSwitchTypePanel() )
    {
        getLMGroupBasePanel().setSwitchType( getSwitchTypePanel().getTypeOfSwitchSelectedString() );
        int type = getSwitchTypePanel().getTypeOfSwitchSelected();
        getLMGroupBasePanel().setFirstFocus();
        return getLMGroupBasePanel();
    }
    //Start Ripple specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_RIPPLE )
    {
        //this is specifically for Minnkota
        if((SPECIAL_RIPPLE & ClientRights.SHOW_SPECIAL_RIPPLE) != 0)
        {
            getSpecialRippleMessagePanel().setFirstFocus();
            return getSpecialRippleMessagePanel();
        }
        else
        {
            getRippleMessageShedPanel().setFirstFocus();
            return getRippleMessageShedPanel();
        }
    }

    //Start emetcon specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON )
    {
        getLMGroupEmetconPanel().setFirstFocus();
        return getLMGroupEmetconPanel();//getGoldOrSilverAddressPanel();
    }
    //Start Versacom specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM )
    {
        getLmGroupVersacomEditorPanel().setFirstFocus();
        return getLmGroupVersacomEditorPanel();
    }
    //Start Expresscom specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                && (getSwitchTypePanel().getTypeOfSwitchSelected() == PAOGroups.LM_GROUP_EXPRESSCOMM ||
                    getSwitchTypePanel().getTypeOfSwitchSelected() == PAOGroups.LM_GROUP_RFN_EXPRESSCOMM) )
    {
        getLMGroupExpressComEditorPanel().setFirstFocus();
        return getLMGroupExpressComEditorPanel();
    }
    //Start LMGroupPoint specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT )
    {
        getLmGroupPointEditorPanel().setFirstFocus();
        return getLmGroupPointEditorPanel();
    }
    //Start LMGroupMCT specific
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_MCT )
    {
        getLMGroupMCTEditorPanel().setFirstFocus();
        return getLMGroupMCTEditorPanel();
    }
    //Start LMGroupMCT specific
    //This is currently just demo GUI
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SA305 )
    {
        getSA305EditorPanel().setFirstFocus();
        return getSA305EditorPanel();
    }
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SA205 )
    {
        getSA205EditorPanel().setFirstFocus();
        return getSA205EditorPanel();
    }
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SADIGITAL )
    {
        getSADigitalEditorPanel().setFirstFocus();
        return getSADigitalEditorPanel();
    }
    else if ( currentInputPanel == getLMGroupBasePanel()
                 && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY )
    {
        getGolayEditorPanel().setFirstFocus();
        return getGolayEditorPanel();
    }
    else if ( currentInputPanel == getLMGroupBasePanel()
            && getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_DIGI_SEP )
    {
        getlmGroupDigiSepEditorPanel().setFirstFocus();
       return getlmGroupDigiSepEditorPanel();
    }
    
    System.err.println(getClass() + "::getNextInputPanel() - currentInputPanel was not recognized.");
    return null;
    
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.SwitchTypePanel
 */
public RippleMessageShedPanel getRippleMessageShedPanel() {
    if( rippleMessageShedPanel == null )
    {
        rippleMessageShedPanel = new RippleMessageShedPanel();
    }
    
    return rippleMessageShedPanel;
}

public SpecialRippleMessagePanel getSpecialRippleMessagePanel() {
    if( specialRippleMessagePanel == null )
    {
        specialRippleMessagePanel = new SpecialRippleMessagePanel();
    }
    
    return specialRippleMessagePanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.SwitchTypePanel
 */
public SwitchTypePanel getSwitchTypePanel() {
    if( switchTypePanel == null )
    {
        switchTypePanel = new SwitchTypePanel();
    }
    
    return switchTypePanel;
}


/**
 * isLastInputPanel method comment.
 */
@Override
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
    //we dont use the getters for each panel here since this call creates new instances of each
    return( currentPanel == lmGroupEmetconPanel
               || currentPanel == lmGroupVersacomEditorPanel
               || currentPanel == rippleMessageShedPanel
               || currentPanel == specialRippleMessagePanel
              || currentPanel == groupMacroLoadGroupsPanel
              || currentPanel == lmGroupExpressComEditorPanel
              || currentPanel == lmGroupPointEditorPanel
              || currentPanel == aSA305EditorPanel
              || currentPanel == aSA205EditorPanel
              || currentPanel == aSADigitalEditorPanel
              || currentPanel == golayEditorPanel
              || currentPanel == lmGroupMCTEditorPanel
              || currentPanel == lmGroupDigiSepEditorPanel
              || (currentPanel == getLMGroupBasePanel() &&
              getSwitchTypePanel().getTypeOfSwitchSelected() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_ECOBEE));
}
}