package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.AlarmCategoryCheckBoxTreeModel;
import com.cannontech.database.model.DeviceCheckBoxTreeModel;
import com.cannontech.spring.YukonSpringHook;


/**
 * Insert the type's description here.
 * Creation date: (12/23/2002 11:36:50 AM)
 * @author: 
 */
public class CurrentAlarmsTableEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private com.cannontech.esub.element.CurrentAlarmsTable alarmsTable;
	
	private JLabel ivjDeviceListLabel = null;
    private JLabel ivjAlarmCategoryListLabel = null;
	private JPanel ivjJPanel1 = null;
	private JPanel checkboxJPanel = null;	
    private JTree selectionJTreeDevices = null;
    private JTree selectionJTreeAlarms = null;
    private JScrollPane ivjJScrollPaneDevices = null;
    private JScrollPane ivjJScrollPaneAlarms = null;
    private CheckNodeSelectionListener deviceNodeListener = null;
    private CheckNodeSelectionListener alarmNodeListener = null;
    private JCheckBox hideInactiveCheckBox = null;
    private JCheckBox hideEventsCheckBox = null;
    private JCheckBox hideAcknowledgedCheckBox = null;
    
/**
 * CurrentAlarmsTableEditorPanel constructor comment.
 */
public CurrentAlarmsTableEditorPanel() {
	super();
	initialize();
}

/**
 * Return the AlarmCategoriesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceListLabel() {
	if (ivjDeviceListLabel == null) {
		try {
            ivjDeviceListLabel = new javax.swing.JLabel();
            ivjDeviceListLabel.setName("DeviceListLabel");
            ivjDeviceListLabel.setText("Devices:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceListLabel;
}


/**
 * Return the AlarmCategoriesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmCategoryListLabel() {
    if (ivjAlarmCategoryListLabel == null) {
        try {
            ivjAlarmCategoryListLabel = new javax.swing.JLabel();
            ivjAlarmCategoryListLabel.setName("AlarmCategoryListLabel");
            ivjAlarmCategoryListLabel.setText("Alarm Categories:");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAlarmCategoryListLabel;
}

private javax.swing.JCheckBox getHideActiveCheckbox() {
    if (hideInactiveCheckBox == null) {
        try {
        	hideInactiveCheckBox= new javax.swing.JCheckBox();
        	hideInactiveCheckBox.setText("Hide Inactive Alarms");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return hideInactiveCheckBox;
}

private javax.swing.JCheckBox getHideEventsCheckbox() {
    if (hideEventsCheckBox == null) {
        try {
        	hideEventsCheckBox = new javax.swing.JCheckBox();
        	hideEventsCheckBox.setText("Hide Event Conditions");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return hideEventsCheckBox;
}

private javax.swing.JCheckBox getHideAcknowledgedCheckbox() {
    if (hideAcknowledgedCheckBox == null) {
        try {
        	hideAcknowledgedCheckBox= new javax.swing.JCheckBox();
        	hideAcknowledgedCheckBox.setText("Hide Acknowledged Alarms");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return hideAcknowledgedCheckBox;
}

/**
 * Return the JScrollPaneAlarms property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAlarms() {
    if ( ivjJScrollPaneAlarms == null ) 
    {
        try 
        {
            ivjJScrollPaneAlarms = new javax.swing.JScrollPane();
            ivjJScrollPaneAlarms.setName( "JScrollPaneAlarms" );
            getJScrollPaneAlarms().setViewportView(getJTreeAlarms());
            ivjJScrollPaneAlarms.setPreferredSize(new Dimension (100, 250));
            // user code begin {1}
            // user code end
        } catch ( java.lang.Throwable ivjExc ) 
        {
            // user code begin {2}
            // user code end
            handleException( ivjExc );
        }
    }
    return ivjJScrollPaneAlarms;
}


/**
 * Return the JScrollPaneDevices property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDevices() {
    if ( ivjJScrollPaneDevices == null ) 
    {
        try 
        {
            ivjJScrollPaneDevices = new javax.swing.JScrollPane();
            ivjJScrollPaneDevices.setName( "JScrollPaneDevices" );
            getJScrollPaneDevices().setViewportView(getJTreeDevices());
            ivjJScrollPaneDevices.setPreferredSize(new Dimension (100, 250));
            // user code begin {1}
            // user code end
        } catch ( java.lang.Throwable ivjExc ) 
        {
            // user code begin {2}
            // user code end
            handleException( ivjExc );
        }
    }
    return ivjJScrollPaneDevices;
}


/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private DeviceCheckBoxTreeModel getDeviceJTreeModel() 
{
    return (DeviceCheckBoxTreeModel)getJTreeDevices().getModel();
}

/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private AlarmCategoryCheckBoxTreeModel getAlarmJTreeModel() 
{
    return (AlarmCategoryCheckBoxTreeModel)getJTreeAlarms().getModel();
}

/**
 * Return the JTreeAlarms property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTreeAlarms() 
{
    if (selectionJTreeAlarms == null) {
        try {
            selectionJTreeAlarms = new javax.swing.JTree();
            selectionJTreeAlarms.setName("JTreeNodes");
            selectionJTreeAlarms.setBounds(0, 0, 300, 400);
            // user code begin {1}
            
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Alarm Categories");
            selectionJTreeAlarms.setModel( new AlarmCategoryCheckBoxTreeModel() );
            selectionJTreeAlarms.setCellRenderer( new CheckRenderer() );
            selectionJTreeAlarms.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            getAlarmJTreeModel().update();
            
            //expand the root
            selectionJTreeAlarms.expandPath( new TreePath(root.getPath()) );
            
            selectionJTreeAlarms.addMouseListener( getAlarmNodeListener() );
            
            selectionJTreeAlarms.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    alarmValueChanged( null );
                }
            });
            
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return selectionJTreeAlarms;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTreeDevices() {
    if (selectionJTreeDevices == null) {
        try {
            selectionJTreeDevices = new javax.swing.JTree();
            selectionJTreeDevices.setName("JTreeNodes");
            selectionJTreeDevices.setBounds(0, 0, 300, 400);
            // user code begin {1}
            
            selectionJTreeDevices.setModel(new DeviceCheckBoxTreeModel(true));
            selectionJTreeDevices.setCellRenderer( new CheckRenderer() );
            selectionJTreeDevices.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            getDeviceJTreeModel().update();
            
            // Load the child nodes on expansion
            selectionJTreeDevices.addTreeWillExpandListener(new TreeWillExpandListener() {
                public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                    getDeviceJTreeModel().treePathWillExpand(event.getPath());
                }
                public void treeWillCollapse(TreeExpansionEvent event)
                throws ExpandVetoException {}
            });

            selectionJTreeDevices.addMouseListener( getDeviceNodeListener());
            
            selectionJTreeDevices.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    deviceValueChanged( null );
                }
            });

            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return selectionJTreeDevices;
}
/**
 * Return the CheckNodeSelectionListener property value.
 * @return com.cannontech.common.gui.tree.CheckNodeSelectionListener
 */
private CheckNodeSelectionListener getDeviceNodeListener()
{
    if( deviceNodeListener == null )
    {
        deviceNodeListener = new CheckNodeSelectionListener( getJTreeDevices(), true);
    }
    return deviceNodeListener;
}

/**
 * Return the CheckNodeSelectionListener property value.
 * @return com.cannontech.common.gui.tree.CheckNodeSelectionListener
 */
private CheckNodeSelectionListener getAlarmNodeListener()
{
    if( alarmNodeListener == null )
    {
        alarmNodeListener = new CheckNodeSelectionListener( getJTreeAlarms() );
    }
    return alarmNodeListener;
}

/**
 * This method checks for extra work to do like checking or unchecking parents after a tree
 * selection and then updates the panel.
 */
public void deviceValueChanged(TreeSelectionEvent e) 
{
    int selRow = getJTreeDevices().getMaxSelectionRow();
    if( selRow != -1) 
    {
        CheckNode node = ( CheckNode )getJTreeDevices().getPathForRow( selRow ).getLastPathComponent();
        if( !node.isSelected( )) // we are doing an uncheck
        {
            CheckNode parent = (CheckNode)node.getParent();
            
            // only uncheck parents if they infact checked currently
            if( parent != null  && parent.isSelected() )  
            {
                //uncheck our parents until we hit the root
                while( node.getParent() != null ) 
                {
                    getDeviceNodeListener().uncheckParent(node);
                    if( parent.getLevel() == 0 )
                    {
                        break;
                    }
                    node = (CheckNode)node.getParent();
                }
            }
            
        }else if ( (CheckNode)node.getParent() != null ) // we don't care if the root got clicked
        {
            //  Here we check to see if we need to set our parent as checked and if we do, we continue to check are 
            //  parent's parent until we either find an unchecked child or we get to the root, confusing as hell.
            
            boolean cont = true;
            while(cont)
            {
                cont = checkParent(node);
                node = (CheckNode)node.getParent();
            }
            
        }
        
    }
    
    fireInputUpdate();
}

/**
 * This methdod looks at all the siblings of "node" to see whether we need to set node's parent as checked,
 * returning true if we another round of parent checking is needed, false if our parent is actually the root.
 * Return the ret property value.
 * @return boolean ret
 */
private boolean checkParent(CheckNode node)
{
    boolean ret = true;
    
    //  since we're doing a set checked on this guy, see if all our siblings are also checked, if so check the parent
    int children = node.getSiblingCount();
    CheckNode parent = (CheckNode)node.getParent();
    CheckNode check = (CheckNode)parent.getFirstChild();
    
    for ( int i = 0; i < children; i++ )
    {
        if ( !check.isSelected() )
        {
            // at least one of our siblings isn't checked so we don't care anymore
            return false;
        }else 
        {
            if ( check.getNextSibling() == null )
            {
                // we are the last node and we are checked so now we can set the parent as checked
                parent.setSelected( true );
                if( parent.getLevel() == 0 )                
                {
                    // the parent is the root and we are done
                    return false;
                }else
                {
                    // we've set our parent and we can return for more checking fun
                    break;
                }
                
            }
        }
        check = (CheckNode) check.getNextSibling();
        
    }
    
    return ret;
}

public void alarmValueChanged(TreeSelectionEvent e) 
{
    int selRow = getJTreeAlarms().getMaxSelectionRow();
    if( selRow != -1 ) 
    {
        TreeNode node = ( TreeNode )getJTreeAlarms().getPathForRow( selRow ).getLastPathComponent();
    }
    fireInputUpdate();
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCheckboxJPanel() {
	if (checkboxJPanel == null) {
		try {
			checkboxJPanel = new javax.swing.JPanel();
			checkboxJPanel.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints constraintHideActiveCheckbox = new java.awt.GridBagConstraints();
            constraintHideActiveCheckbox.gridx = 0; constraintHideActiveCheckbox.gridy = 0;
            constraintHideActiveCheckbox.gridwidth = 1;
            constraintHideActiveCheckbox.anchor = GridBagConstraints.WEST;           
            checkboxJPanel.add(getHideActiveCheckbox(), constraintHideActiveCheckbox);
            
            java.awt.GridBagConstraints constraintHideEventsCheckbox = new java.awt.GridBagConstraints();
            constraintHideEventsCheckbox.gridx = 1; constraintHideEventsCheckbox.gridy = 0;
            constraintHideEventsCheckbox.gridwidth = 1;
            constraintHideEventsCheckbox.anchor = GridBagConstraints.CENTER;
            constraintHideEventsCheckbox.weightx = 1.0;
            checkboxJPanel.add(getHideEventsCheckbox(), constraintHideEventsCheckbox);
            
            java.awt.GridBagConstraints constraintHideAckCheckbox = new java.awt.GridBagConstraints();
            constraintHideAckCheckbox.gridx = 2; constraintHideAckCheckbox .gridy = 0;
            constraintHideAckCheckbox.gridwidth = 1;
            constraintHideAckCheckbox.anchor = GridBagConstraints.WEST;           
            checkboxJPanel.add(getHideAcknowledgedCheckbox(), constraintHideAckCheckbox );
            
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return checkboxJPanel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Display Alarms ");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
            
            GridBagConstraints constraintsDeviceListLabel = new java.awt.GridBagConstraints();
            constraintsDeviceListLabel.gridx = 0; constraintsDeviceListLabel.gridy = 0;
            constraintsDeviceListLabel.gridwidth = 1;
            constraintsDeviceListLabel.anchor = GridBagConstraints.WEST;
            constraintsDeviceListLabel.insets = new java.awt.Insets(5, 5, 0, 5);
            ivjJPanel1.add(getDeviceListLabel(), constraintsDeviceListLabel);
            
            java.awt.GridBagConstraints constraintsAlarmCategoryListLabel = new java.awt.GridBagConstraints();
            constraintsAlarmCategoryListLabel.gridx = 1; constraintsAlarmCategoryListLabel.gridy = 0;
            constraintsAlarmCategoryListLabel.gridwidth = 1;
            constraintsAlarmCategoryListLabel.anchor = GridBagConstraints.WEST;
            constraintsAlarmCategoryListLabel.insets = new java.awt.Insets(5, 5, 0, 5);
            ivjJPanel1.add(getAlarmCategoryListLabel(), constraintsAlarmCategoryListLabel);

            java.awt.GridBagConstraints constraintsJScrollPaneDevices = new java.awt.GridBagConstraints();
            constraintsJScrollPaneDevices.gridx = 0; constraintsJScrollPaneDevices.gridy = 1;
            constraintsJScrollPaneDevices.gridwidth = 1;
            constraintsJScrollPaneDevices.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJScrollPaneDevices.weightx = 1.0; 
            constraintsJScrollPaneDevices.weighty = 1.0;
            constraintsJScrollPaneDevices.insets = new java.awt.Insets(0, 5, 5, 5);
            ivjJPanel1.add(getJScrollPaneDevices(), constraintsJScrollPaneDevices);
            
            java.awt.GridBagConstraints constraintsJScrollPaneAlarms = new java.awt.GridBagConstraints();
            constraintsJScrollPaneAlarms.gridx = 1; constraintsJScrollPaneAlarms.gridy = 1;
            constraintsJScrollPaneAlarms.gridwidth = 1;
            constraintsJScrollPaneAlarms.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJScrollPaneAlarms.weightx = 1.0;
            constraintsJScrollPaneAlarms.weighty = 1.0;
            constraintsJScrollPaneAlarms.insets = new java.awt.Insets(0, 5, 5, 5);
            ivjJPanel1.add(getJScrollPaneAlarms(), constraintsJScrollPaneAlarms);

            java.awt.GridBagConstraints constraintsBox = new java.awt.GridBagConstraints();
            constraintsBox.gridx = 0; constraintsBox.gridy = 2;
            constraintsBox.gridwidth = 2;
            constraintsBox.fill = java.awt.GridBagConstraints.BOTH;
            constraintsBox.insets = new java.awt.Insets(0, 5, 5, 5);
            constraintsBox.anchor = GridBagConstraints.WEST;           
            ivjJPanel1.add(getCheckboxJPanel(), constraintsBox);

			//getJPanel1().add(getJComboBox1(), getJComboBox1().getName());
            ivjJPanel1.setPreferredSize(new Dimension ( 300, 400 ));
            
            
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    // create our arrays of selected devices and points
    
    CheckNode deviceRoot = (CheckNode)getJTreeDevices().getModel().getRoot();
    CheckNode alarmRoot = (CheckNode)getJTreeAlarms().getModel().getRoot();
    Enumeration deviceChildren = deviceRoot.children();
    Enumeration alarmChildren = alarmRoot.children();
    
    ArrayList<Integer> pointids = new ArrayList<Integer>();
    ArrayList<Integer> deviceids = new ArrayList<Integer>();
    ArrayList<Integer> alarmcatids = new ArrayList<Integer>();
    
    while( deviceChildren.hasMoreElements())
    {
        CheckNode currentDeviceNode = (CheckNode)deviceChildren.nextElement();
        if(currentDeviceNode.isSelected())
        {
            LiteYukonPAObject device = (LiteYukonPAObject) currentDeviceNode.getUserObject();
            deviceids.add(device.getYukonId());
            
        }else
        {
            Enumeration categories = currentDeviceNode.children();
            while( categories.hasMoreElements() )
            {
                CheckNode category = (CheckNode)categories.nextElement();
                Enumeration points = category.children();
                while(points.hasMoreElements())
                {
                    CheckNode currentPointNode = (CheckNode)points.nextElement();
                    if(currentPointNode.isSelected())
                    {
                        LitePoint point = (LitePoint) currentPointNode.getUserObject();
                        
                        pointids.add( point.getLiteID());
                        
                    }
                }
            }
        }
    }
    
    while( alarmChildren.hasMoreElements())
    {
        CheckNode currentAlarmNode = (CheckNode)alarmChildren.nextElement();
        if(currentAlarmNode.isSelected())
        {
            LiteAlarmCategory alarmcat = (LiteAlarmCategory) currentAlarmNode.getUserObject();
            alarmcatids.add(alarmcat.getLiteID());
        }
    }
    
    //  we have to do this since we need to set an array of actual primitive ints
    int[] pointarray = new int[pointids.size()];
    int[] alarmarray = new int[alarmcatids.size()];
    int[] devicearray = new int[deviceids.size()];
    
    for(int j = 0; j < pointids.size(); j++)
    {
        pointarray[j] = pointids.get(j);
    }
    
    for(int j = 0; j < alarmcatids.size(); j++)
    {
        alarmarray[j] = alarmcatids.get(j);
    }
    
    for(int j = 0; j < deviceids.size(); j++)
    {
        devicearray[j] = deviceids.get(j);
    }
    
    alarmsTable.setAlarmCategoryIds(alarmarray);
    alarmsTable.setPointIds(pointarray);
    alarmsTable.setDeviceIds(devicearray);
    alarmsTable.setHideInactive(getHideActiveCheckbox().isSelected());
    alarmsTable.setHideEvents(getHideEventsCheckbox().isSelected());
    alarmsTable.setHideAcknowledged(getHideAcknowledgedCheckbox().isSelected());
    
	return alarmsTable;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CurrentAlarmsTableEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		//setSize(386, 256);
        setSize(300, 400);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CurrentAlarmsTableEditorPanel aCurrentAlarmsTableEditorPanel;
		aCurrentAlarmsTableEditorPanel = new CurrentAlarmsTableEditorPanel();
		frame.setContentPane(aCurrentAlarmsTableEditorPanel);
		frame.setSize(aCurrentAlarmsTableEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	alarmsTable = (com.cannontech.esub.element.CurrentAlarmsTable) o;
	int[] deviceids = alarmsTable.getDeviceIds();
    int[] pointids = alarmsTable.getPointIds();
    int[] alarmcatids = alarmsTable.getAlarmCategoryIds();
    
    boolean use_active = alarmsTable.isHideInactive();
    boolean use_alarmsOnly = alarmsTable.isHideEvents();
    boolean use_unack = alarmsTable.isHideAcknowledged();
    
    getHideActiveCheckbox().setSelected(use_active);
    getHideEventsCheckbox().setSelected(use_alarmsOnly);
    getHideAcknowledgedCheckbox().setSelected(use_unack);
    
    for( int i = 0; i < alarmcatids.length; i++ )
    {
        CheckNode currentAlarmNode = getAlarmJTreeModel().getAlarmCategorybyID(alarmcatids[i]);
        if(currentAlarmNode != null)
        {
            currentAlarmNode.setSelected(true);
        }
        
    }
    
    for( int i = 0; i < pointids.length; i++ )
    {
        // Find the device for this point and load the device's tree node.
        LitePoint litePoint = null;
        try {
            litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(pointids[i]);
        }catch(NotFoundException nfe) {
            CTILogger.error("The point (pointId:"+ pointids[i] + ") for this AlarmTable might have been deleted!", nfe);
            continue;
        }
        if(litePoint != null) {
            int deviceId = litePoint.getPaobjectID();
            CheckNode currentDeviceNode = getDeviceJTreeModel().getDevicebyID(deviceId);
            // Only load children if not already loaded
            if(currentDeviceNode != null) {
                if (currentDeviceNode.getChildCount() == 0) {
                    getDeviceJTreeModel().treePathWillExpand(new TreePath(currentDeviceNode.getPath()));
                }
                
                CheckNode currentPointNode = getDeviceJTreeModel().getPointbyID(pointids[i]);
                if(currentPointNode != null)
                {
                    currentPointNode.setSelected(true);
                    getJTreeDevices().expandPath(new TreePath(((CheckNode)currentPointNode.getParent()).getPath()));
                }
            }
        }
        
    }
    
    for( int i = 0; i < deviceids.length; i++ )
    {
        CheckNode currentDeviceNode = getDeviceJTreeModel().getDevicebyID(deviceids[i]);
        if( currentDeviceNode != null)
        {
        	// Load the device's child nodes
            getDeviceJTreeModel().treePathWillExpand(new TreePath(currentDeviceNode.getPath()));
            currentDeviceNode.setSelected(true);
            getJTreeDevices().expandPath(new TreePath(((CheckNode)currentDeviceNode.getFirstChild()).getPath()));
        }
        
    }
}
}
