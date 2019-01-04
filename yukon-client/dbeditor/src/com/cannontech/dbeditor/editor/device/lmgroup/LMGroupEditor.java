package com.cannontech.dbeditor.editor.device.lmgroup;

import java.util.Vector;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ClientRights;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.device.lm.LMGroup;

/**
 * This type was created in VisualAge.
 */
public class LMGroupEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
    private DataInputPanel[] inputPanels;
    private String[] inputPanelTabNames;
    
    //hex value representing some privileges of the user on this machine
    public static final long SPECIAL_RIPPLE = Long.parseLong( 
        ClientSession.getInstance().getRolePropertyValue(
        YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV), 16 );
        
    private static final PaoType[][] EDITOR_TYPES =
    {
        //com.cannontech.dbeditor.editor.device.lmgroup.LMGroupBasePanel
        { PaoType.LM_GROUP_EMETCON, PaoType.LM_GROUP_RIPPLE, 
          PaoType.LM_GROUP_VERSACOM, PaoType.MACRO_GROUP, PaoType.LM_GROUP_MCT,
          PaoType.LM_GROUP_POINT, PaoType.LM_GROUP_EXPRESSCOMM, PaoType.LM_GROUP_RFN_EXPRESSCOMM,
          PaoType.LM_GROUP_SA305, PaoType.LM_GROUP_SA205,
          PaoType.LM_GROUP_SADIGITAL, PaoType.LM_GROUP_GOLAY,
          PaoType.LM_GROUP_DIGI_SEP,
          PaoType.LM_GROUP_ECOBEE,
          PaoType.LM_GROUP_HONEYWELL,
          PaoType.LM_GROUP_ITRON,
          PaoType.LM_GROUP_NEST},

        //com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupEmetconPanel
        { PaoType.LM_GROUP_EMETCON },
        
        //com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel
        { PaoType.LM_GROUP_VERSACOM },

        //com.cannontech.dbeditor.wizard.device.lmgroup.RippleMessageShedPanel
        { PaoType.LM_GROUP_RIPPLE },

        //LMGroupPointEditorPanel
        { PaoType.LM_GROUP_POINT },

        //com.cannontech.dbeditor.wizard.device.lmgroup.GroupMacroLoadGroupsPanel
        { PaoType.MACRO_GROUP },

        //com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressComEditorPanel
        { PaoType.LM_GROUP_EXPRESSCOMM, PaoType.LM_GROUP_RFN_EXPRESSCOMM },

        //com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupMCTEditorPanel
        { PaoType.LM_GROUP_MCT },
        
        //com.cannontech.dbeditor.wizard.device.lmgroup.SA305EditorPanel
        { PaoType.LM_GROUP_SA305 },
        
        //com.cannontech.dbeditor.wizard.device.lmgroup.SA205EditorPanel
        { PaoType.LM_GROUP_SA205 },
        
        //com.cannontech.dbeditor.wizard.device.lmgroup.SADigitalEditorPanel
        { PaoType.LM_GROUP_SADIGITAL },
        
        //com.cannontech.dbeditor.wizard.device.lmgroup.GolayEditorPanel
        { PaoType.LM_GROUP_GOLAY },
        
        { PaoType.LM_GROUP_DIGI_SEP}
    };

    
    private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
public LMGroupEditor() {
    super();
    initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 1:17:24 PM)
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
@Override
public Object[] createNewPanel(int panelIndex)
{
    Object[] objs = new Object[2];
    
    switch( panelIndex )
    {
        case 0: 
            objs[0] = new com.cannontech.dbeditor.editor.device.lmgroup.LMGroupBasePanel(false);
            objs[1] = "General";
            break;

        case 1:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupEmetconPanel();
            objs[1] = "Addressing";
            break;

        case 2:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel();
            objs[1] = "Addressing";
            break;

        case 3:
            if((SPECIAL_RIPPLE & ClientRights.SHOW_SPECIAL_RIPPLE) != 0) {
                objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SpecialRippleMessagePanel();
            } else {
                objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.RippleMessageShedPanel();
            }
            objs[1] = "Message";
            break;

        case 4:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupPointEditorPanel();
            objs[1] = "Point Group";
            break;
            
        case 5:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.GroupMacroLoadGroupsPanel();
            objs[1] = "Macro Group";
            break;

        case 6:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressComEditorPanel();
            objs[1] = "Addressing";
            break;

        case 7:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupMCTEditorPanel();
            objs[1] = "Addressing";
            break;
            
        case 8:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SA305EditorPanel();
            objs[1] = "Settings";
            break;
            
        case 9:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SA205EditorPanel();
            objs[1] = "Settings";
            break;
            
        case 10:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SADigitalEditorPanel();
            objs[1] = "Settings";
            break;
            
        case 11:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.GolayEditorPanel();
            objs[1] = "Settings";
            break;
            
        case 12:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupDigiSepPanel();
            objs[1] = "Addressing";
            break;
    }
        
    return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
@Override
public DataInputPanel[] getInputPanels() {
    //At least guarantee a non-null array if not a meaningful one
    if( this.inputPanels == null ) {
        this.inputPanels = new DataInputPanel[0];
    }
        
    return this.inputPanels;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getPreferredSize() {
    return new java.awt.Dimension( 400, 450 );
}
/**
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getStateEditorTabbedPane() {
    if (ivjStateEditorTabbedPane == null) {
        try {
            ivjStateEditorTabbedPane = new javax.swing.JTabbedPane();
            ivjStateEditorTabbedPane.setName("StateEditorTabbedPane");
            ivjStateEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
            ivjStateEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
            ivjStateEditorTabbedPane.setBounds(0, 0, 400, 350);
            ivjStateEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjStateEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
@Override
public String[] getTabNames() {
    if( this.inputPanelTabNames == null ) {
        this.inputPanelTabNames = new String[0];
    }
        
    return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

    /* Uncomment the following lines to print uncaught exceptions to stdout */
    com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
    com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        // user code end
        setName("RouteEditorPanel");
        setPreferredSize(new java.awt.Dimension(400, 350));
        setLayout(null);
        setSize(400, 350);
        setMaximumSize(new java.awt.Dimension(400, 350));
        add(getStateEditorTabbedPane(), getStateEditorTabbedPane().getName());
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
    // user code begin {2}
    // user code end
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) 
{
    //Vector to hold the panels temporarily
    Vector<DataInputPanel> panels = new Vector<>(EDITOR_TYPES.length);
    Vector<Object> tabs = new Vector<>(EDITOR_TYPES.length);
    
    DataInputPanel tempPanel;
    PaoType type = ((LMGroup)val).getPaoType();

     for( int i = 0; i < EDITOR_TYPES.length; i++ )
     {
         for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
         {
             if( type == EDITOR_TYPES[i][j] )
            {
                Object[] panelTabs = createNewPanel(i);

                tempPanel = (DataInputPanel)panelTabs[0];
                panels.addElement( tempPanel );
                tabs.addElement( panelTabs[1] );
                break;                
            }
         }

     }
    
    this.inputPanels = new DataInputPanel[panels.size()];
    panels.copyInto( this.inputPanels );

    this.inputPanelTabNames = new String[tabs.size()];
    tabs.copyInto( this.inputPanelTabNames );
    
    //Allow super to do whatever it needs to
    super.setValue( val );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@Override
public String toString() {
    return "LMGroup Editor";
}

}
