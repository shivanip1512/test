package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.table.CheckBoxColorRenderer;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.UniqueSet;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.yukon.IDatabaseCache;

public class UserGroupRoleEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener, PopupMenuListener
{
	private UserGroupTableModel tableModel = null;
	private javax.swing.JPanel ivjJPanelDescription = null;
	private javax.swing.JScrollPane ivjJScrollPaneRoleGroup = null;
	private javax.swing.JTable ivjJTableRoleGroup = null;
	private javax.swing.JScrollPane ivjJScrollPaneTextPane = null;
	private javax.swing.JTextPane ivjJTextPaneDescription = null;
	
	private JPopupMenu jPopupMenu = null;
	private JMenuItem jMenuItemRoles = null;
	private JMenuItem jMenuItemConflicts = null;


	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public UserGroupRoleEditorPanel() {
		super();
		initialize();
	}

	public void actionPerformed(ActionEvent e)
	{
		
		if( e.getSource() == getJMenuItemRoles() )
		{
			int row = getJTableRoleGroup().getSelectedRow();
			if( row >= 0 )
				showRoles( row );
		}

		if( e.getSource() == getJMenuItemConflicts() )
		{
			//not sure
			int row = getJTableRoleGroup().getSelectedRow();
			if( row >= 0 )
			{
				StringBuffer buf = new StringBuffer();
				buf.append( "Role Category -> Role Name " + System.getProperty("line.separator") );
				buf.append( "________________________________" + System.getProperty("line.separator") );
				
				SelectableGroupRow rowVal = getTableModel().getRowAt( row );
				Iterator it = rowVal.getConflictIter();
				while( it.hasNext() )
				{
					LiteYukonRole role = (LiteYukonRole)it.next();
					buf.append( role.getCategory() );
					buf.append( " -> " + role.getRoleName() + System.getProperty("line.separator") );					
				}
				
				
				
				JOptionPane.showMessageDialog(
						this, buf.toString(), "Role Conflicts", JOptionPane.WARNING_MESSAGE );
			}
			
		}

	}
	
	/**
	 * Return the ConfigurationPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelDescription() {
		if (ivjJPanelDescription == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
				ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder1.setTitle("Description");
				ivjJPanelDescription = new javax.swing.JPanel();
				ivjJPanelDescription.setName("JPanelDescription");
				ivjJPanelDescription.setBorder(ivjLocalBorder1);
				ivjJPanelDescription.setLayout(new java.awt.BorderLayout());
				getJPanelDescription().add(getJScrollPaneTextPane(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanelDescription;
	}


	/**
	 * Return the JScrollPaneAlarmStates property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPaneRoleGroup() {
		if (ivjJScrollPaneRoleGroup == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
				ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder.setTitle("Role Groups");
				ivjJScrollPaneRoleGroup = new javax.swing.JScrollPane();
				ivjJScrollPaneRoleGroup.setName("JScrollPaneRoleGroup");
				ivjJScrollPaneRoleGroup.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPaneRoleGroup.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				ivjJScrollPaneRoleGroup.setBorder(ivjLocalBorder);
				getJScrollPaneRoleGroup().setViewportView(getJTableRoleGroup());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneRoleGroup;
	}

	
	public boolean isShowingUserRoles()
	{
		return true;
	}

	/**
	 * Return the JScrollPaneTextPane property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPaneTextPane() {
		if (ivjJScrollPaneTextPane == null) {
			try {
				ivjJScrollPaneTextPane = new javax.swing.JScrollPane();
				ivjJScrollPaneTextPane.setName("JScrollPaneTextPane");
				getJScrollPaneTextPane().setViewportView(getJTextPaneDescription());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneTextPane;
	}


	/**
	 * Return the JTableAlarmStates property value.
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTableRoleGroup() {
	if (ivjJTableRoleGroup == null) {
		try {
			ivjJTableRoleGroup = new javax.swing.JTable();
			ivjJTableRoleGroup.setName("JTableRoleGroup");
			getJScrollPaneRoleGroup().setColumnHeaderView(ivjJTableRoleGroup.getTableHeader());

			ivjJTableRoleGroup.setBounds(0, 0, 200, 200);
			ivjJTableRoleGroup.setAutoCreateColumnsFromModel(true);
			ivjJTableRoleGroup.setModel( getTableModel() );
			ivjJTableRoleGroup.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableRoleGroup.setShowVerticalLines(false);
			ivjJTableRoleGroup.setShowHorizontalLines(false);
			ivjJTableRoleGroup.setIntercellSpacing( new Dimension(0, 0) );
			
			
			ivjJTableRoleGroup.setDefaultRenderer(Object.class, new RolePropertyRenderer() );
			ivjJTableRoleGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableRoleGroup.setRowHeight(20);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTableRoleGroup;
}

	/**
	 * Return the JTextPaneDescription property value.
	 * @return javax.swing.JTextPane
	 */
	private javax.swing.JTextPane getJTextPaneDescription() {
		if (ivjJTextPaneDescription == null) {
			try {
				ivjJTextPaneDescription = new javax.swing.JTextPane();
				ivjJTextPaneDescription.setName("JTextPaneDescription");
				ivjJTextPaneDescription.setBounds(0, 0, 100, 59);
				ivjJTextPaneDescription.setEnabled( false );
				ivjJTextPaneDescription.setBackground( getJPanelDescription().getBackground() );
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextPaneDescription;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/00 4:58:59 PM)
	 */
	private UserGroupTableModel getTableModel() 
	{
		if( tableModel == null ) {
			tableModel = new UserGroupTableModel();
		}
		return tableModel;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 */
	public Object getValue(Object o) 
	{
		YukonUser login = (YukonUser)o;

		login.getYukonGroups().removeAllElements();

		for( int i = 0; i < getTableModel().getRowCount(); i++ )
		{
			if( getTableModel().getRowAt(i).isSelected().booleanValue() )
			{
				LiteYukonGroup group = getTableModel().getRowAt(i).getLiteYukonGroup(); 
				
				//add a new DBPersistant YukonGroup to our Login
				YukonGroup grp = new YukonGroup( new Integer(group.getGroupID()), group.getGroupName() );
				grp.setGroupDescription( group.getGroupDescription() );
				
				login.getYukonGroups().add( grp );
			}

		}		
	
		return login;
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) 
	{	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		CTILogger.error( exception.getMessage(), exception );;
	}


	private JPopupMenu getJPopupMenu() 
	{
		if( jPopupMenu == null) 
		{
			try 
			{
				jPopupMenu = new JPopupMenu();
				jPopupMenu.setName("JPopupMenu");

				jPopupMenu.add( getJMenuItemRoles() );
				jPopupMenu.add( getJMenuItemConflicts() );
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jPopupMenu;
	}

	/**
	 * Returns the jMenuItemRoles property value.
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemRoles() 
	{
		if( jMenuItemRoles == null ) 
		{
			try 
			{
				jMenuItemRoles = new JMenuItem();
				jMenuItemRoles.setName("jMenuItemRoles");
				jMenuItemRoles.setMnemonic('r');
				jMenuItemRoles.setText("Roles...");
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jMenuItemRoles;
	}

	/**
	 * Returns the jMenuItemRoles property value.
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemConflicts() 
	{
		if( jMenuItemConflicts == null ) 
		{
			try 
			{
				jMenuItemConflicts = new JMenuItem();
				jMenuItemConflicts.setName("jMenuItemConflicts");
				jMenuItemConflicts.setMnemonic('c');
				jMenuItemConflicts.setText("Conflicts...");
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jMenuItemConflicts;
	}
		
	private void showRoles( int row )
	{
		UserRolePanel rolePanel = new UserRolePanel();
		DBPersistent dbObj = 
				LiteFactory.createDBPersistent( getTableModel().getRowAt(row).getLiteYukonGroup() );

		try
		{
			dbObj = Transaction.createTransaction( 
							Transaction.RETRIEVE, dbObj ).execute();
	
			rolePanel.setValue( dbObj );
			rolePanel.setRoleTabledEnabled( false );
						
			OkCancelDialog diag = new OkCancelDialog(
					CtiUtilities.getParentFrame(UserGroupRoleEditorPanel.this), 
					"Group Roles : " + getTableModel().getRowAt(row).getLiteYukonGroup().getGroupName() + "  (Read-only)", 
					true, rolePanel );
	
			diag.setCancelButtonVisible( false );
			diag.setSize( 520, 610 );
			diag.setLocationRelativeTo( CtiUtilities.getParentFrame(UserGroupRoleEditorPanel.this) );
						
			diag.show();
		}
		catch( TransactionException te )
		{
			CTILogger.error( "Unabel to get the role data from the database", te );					
		}
	
	}
	
	/**
	 * Initializes connections
	 */
	private void initConnections() 
	{

		getJMenuItemRoles().addActionListener( this );
		getJMenuItemConflicts().addActionListener( this );

		getJPopupMenu().addPopupMenuListener( this );



		java.awt.event.MouseListener listener = 
				new com.cannontech.clientutils.popup.PopUpMenuShower( getJPopupMenu() );
		getJTableRoleGroup().addMouseListener( listener );


		//add the mouse listener to our table	
		final MouseAdapter ma = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) 
			{
				int row = getJTableRoleGroup().rowAtPoint( e.getPoint() );

				if( row >= 0 && row <= getTableModel().getRowCount() )
				{
					getJTextPaneDescription().setText(
							getTableModel().getRowAt(row).getLiteYukonGroup().getGroupDescription() );

					if( e.getClickCount() == 2 )
					{
						showRoles( row );
					}

				}

			}
			
			public void mousePressed(MouseEvent event) 
			{
				int rLoc = getJTableRoleGroup().rowAtPoint( event.getPoint() );
	
				getJTableRoleGroup().getSelectionModel().setSelectionInterval( rLoc, rLoc );
			}
		
		};		
		getJTableRoleGroup().addMouseListener( ma );
	}


	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("UserGroupRoleEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 366);

		java.awt.GridBagConstraints constraintsJScrollPaneRoleGroup = new java.awt.GridBagConstraints();
		constraintsJScrollPaneRoleGroup.gridx = 0; constraintsJScrollPaneRoleGroup.gridy = 1;
		constraintsJScrollPaneRoleGroup.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneRoleGroup.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneRoleGroup.weightx = 1.0;
		constraintsJScrollPaneRoleGroup.weighty = 1.0;
		constraintsJScrollPaneRoleGroup.ipadx = 343;
		constraintsJScrollPaneRoleGroup.ipady = 172;
		constraintsJScrollPaneRoleGroup.insets = new java.awt.Insets(6, 7, 1, 5);
		add(getJScrollPaneRoleGroup(), constraintsJScrollPaneRoleGroup);

		java.awt.GridBagConstraints constraintsJPanelDescription = new java.awt.GridBagConstraints();
		constraintsJPanelDescription.gridx = 0; constraintsJPanelDescription.gridy = 2;
		constraintsJPanelDescription.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDescription.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDescription.weightx = 1.0;
		constraintsJPanelDescription.weighty = 1.0;
		constraintsJPanelDescription.ipadx = 340;
		constraintsJPanelDescription.ipady = 38;
		constraintsJPanelDescription.insets = new java.awt.Insets(4, 7, 2, 5);
		add(getJPanelDescription(), constraintsJPanelDescription);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	initConnections();

	initJTableCellComponents();
			
	// user code end
}

	/**
	 * Fired when a check box in the Roles table is clicked
	 * @param e
	 */
	private void checkBoxAction()
	{
		//validate all the selected roles against one another
		UniqueSet[] unqSet = validateRoles();					
					
		for( int i = 0; i < getTableModel().getRowCount(); i++ )
		{
			SelectableGroupRow row = getTableModel().getRowAt(i);
							
			//re-init our conflicts
			row.clearConflicts();

			for( int j = 0; j < unqSet.length; j++ )
			{
				UniqueSet us = unqSet[j];
							
				if( row.getLiteYukonGroup().equals(us.getKey()) )
				{
					row.addConflict( (LiteYukonRole)us.getValue() );
				}
														
			}

		}
					
//		UniqueSet p = (UniqueSet)conflictList.get(i);
//		LiteYukonGroup grp = (LiteYukonGroup)p.getKey();
//		LiteYukonRole r = (LiteYukonRole)p.getValue();
//		CTILogger.info( "  " + r.getRoleName() + "  in  " + grp.getGroupName() );
					
		fireInputUpdate();
		getTableModel().fireTableRowsUpdated( 0, getTableModel().getRowCount()-1 );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/15/00 10:52:29 AM)
	 */
	private void initJTableCellComponents() 
	{
		// Do any column specific initialization here
		TableColumn selColumn = getJTableRoleGroup().getColumnModel().getColumn(UserGroupTableModel.COL_SELECTED);
		selColumn.setMaxWidth(30);
	
		// Create and add the column renderers	
		//CheckBoxTableRenderer bxRender = new CheckBoxTableRenderer();
		CheckBoxColorRenderer bxRender = new CheckBoxColorRenderer();
		bxRender.setBackground( getJTableRoleGroup().getBackground() );
		bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
		bxRender.setBorderPainted( true );
		
		selColumn.setCellRenderer(bxRender);

	
		// Get the group role data from the cache	
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )	
		{
			java.util.List yukonGroups = cache.getAllYukonGroups();
		
			for( int i = 0; i < yukonGroups.size(); i++ )
				getTableModel().addRowValue( (LiteYukonGroup)yukonGroups.get(i), new Boolean(false) );
	
			// Create and add the column CellEditors
			javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
			chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			chkBox.setBackground( getJTableRoleGroup().getBackground() );
			chkBox.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					checkBoxAction();
				}
			});


			selColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
		}

	}
	
	/**
	 * Icky method to below, but what other way? May become a performance nightmare.
	 * 
	 * Returns an arry of UniqueSet instance in the following:
	 *   key<LiteYukonGroup>, value<LiteYukonRole>
	 * 
	 * If no duplicate roles are foud, a zero length array is ruturned
	 */
	private synchronized UniqueSet[] validateRoles()
	{
		//stores the conflicts
		ArrayList conflictList = new ArrayList(64);
		
		//stores all groups we already looked at
		ArrayList allOldGroups = new ArrayList(128);
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
		
			for( int i = 0; i < getTableModel().getRowCount(); i++ )
			{
				//if the row is selected, add its roles to our ALL_ROWS list
				if( getTableModel().getRowAt(i).isSelected().booleanValue() )
				{
					LiteYukonGroup currGrp = getTableModel().getRowAt(i).getLiteYukonGroup();
					
					Map currRoleMap = (Map)cache.getYukonGroupRolePropertyMap().get( currGrp );

					for( int j = 0; j < allOldGroups.size() && currRoleMap != null; j++ )
					{
						LiteYukonGroup oldGrp = (LiteYukonGroup)allOldGroups.get(j);
						Map oldMap = (Map)cache.getYukonGroupRolePropertyMap().get( oldGrp );
						
						Iterator it = currRoleMap.keySet().iterator();
						while( it.hasNext() )
						{				
							LiteYukonRole currRole = (LiteYukonRole)it.next();
							Object oldRole = ( oldMap == null ? null : oldMap.get( currRole ) ); 

							//if we have an old role, we have the role duplicated in at
							// least 2 Role Groups
							if( oldRole != null )
							{								
								UniqueSet p1 = new UniqueSet(oldGrp, currRole);
								if( !conflictList.contains(p1) )
									conflictList.add( p1 );
								
								UniqueSet p2 = new UniqueSet(currGrp, currRole);
								if( !conflictList.contains(p2) )
									conflictList.add( p2 );
							}
							
						}
									
					}

					//examined this group, put it into the old group mix
					allOldGroups.add( currGrp );
				}
				
			}
/*
			for( int i = 0; i < conflictList.size(); i++ )
			{
				UniqueSet p = (UniqueSet)conflictList.get(i);
				LiteYukonGroup grp = (LiteYukonGroup)p.getKey();
				LiteYukonRole r = (LiteYukonRole)p.getValue();
				CTILogger.info( "  " + r.getRoleName() + "  in  " + grp.getGroupName() );
			}
*/
		}
	
	
		//will return a zero lengh array if none are found
		UniqueSet[] us = new UniqueSet[ conflictList.size() ];
		return (UniqueSet[])conflictList.toArray( us );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (5/1/2001 9:11:36 AM)
	 * @return boolean
	 */
	public boolean isInputValid() 
	{
		return true;
	}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserGroupRoleEditorPanel aUserGroupRoleEditorPanel;
		aUserGroupRoleEditorPanel = new UserGroupRoleEditorPanel();
		frame.setContentPane(aUserGroupRoleEditorPanel);
		frame.setSize(aUserGroupRoleEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
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
	 * @param val java.lang.Object
	 */
	public void setValue(Object o) 
	{
		if( o == null )
			return;

		YukonUser login = (YukonUser)o;
		
		for( int i = 0; i < login.getYukonGroups().size(); i++ )
		{
			LiteYukonGroup group = 
				(LiteYukonGroup)LiteFactory.createLite((YukonGroup)login.getYukonGroups().get(i));

			int indx = getTableModel().indexOf( group ); 

			if( indx >= 0 ) {
				getTableModel().setValueAt(new Boolean(true), indx, UserGroupTableModel.COL_SELECTED);
			}
		}
		
		checkBoxAction();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/25/00 9:45:22 AM)
	 * @param e javax.swing.event.PopupMenuEvent
	 */
	public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
	{
	
		if( e.getSource() == getJPopupMenu() )
		{
			int rLoc = getJTableRoleGroup().getSelectedRow();
			SelectableGroupRow sRow = getTableModel().getRowAt( rLoc );

			if( sRow != null )
			{	
				getJMenuItemConflicts().setEnabled( sRow.hasConflict() ); 
			}
		}	
	}


	public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
	public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}


}