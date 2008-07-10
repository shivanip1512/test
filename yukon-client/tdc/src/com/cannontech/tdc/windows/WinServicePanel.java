package com.cannontech.tdc.windows;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.roles.yukon.SystemRole;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2002 9:47:26 PM)
 * @author: 
 */
public class WinServicePanel extends javax.swing.JPanel implements com.cannontech.tdc.SpecialTDCChild, java.awt.event.ActionListener, javax.swing.event.PopupMenuListener 
{
	private javax.swing.JTable ivjJTableServices = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
   private ServicePopUpMenu servicePopUpMenu = null;
   private javax.swing.JButton[] buttonsArray = new javax.swing.JButton[0];
   private javax.swing.JComboBox comboBox = null;

	private JButton jButtonStartAll = null;
	private JButton jButtonStopAll = null;
	

/**
 * WinServicePanel constructor comment.
 */
public WinServicePanel() {
	super();
	//initialize();
}

public void actionPerformed(java.awt.event.ActionEvent event) 
{

   if( event.getSource() == comboBox )
   {
      getJTableModel().setYukonFilter(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );
			
		getJButtonStartAll().setEnabled(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );			
		getJButtonStopAll().setEnabled(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );			
   }
	else if( event.getSource() == getJButtonStartAll() )
	{
		
		for( int i = 0; i < getJTableModel().getRowCount(); i++ )
		{
			NTService serv = getJTableModel().getRowAt(i);
			
			if( getJTableModel().isServiceIdle(serv) )
			{
				getServicePopUpMenu().setNtServices( serv, true );
				getServicePopUpMenu().execute_Start( event );
			}
				
		}
		
	}
	else if( event.getSource() == getJButtonStopAll() )
	{

		for( int i = 0; i < getJTableModel().getRowCount(); i++ )
		{
			NTService serv = getJTableModel().getRowAt(i);
			
			if( getJTableModel().isServiceIdle(serv) )
			{
				getServicePopUpMenu().setNtServices( serv, true );
				getServicePopUpMenu().execute_Stop( event );
			}
				
		}
	}

}


public void addActionListenerToJComponent( javax.swing.JComponent component ) 
{
   if( component instanceof javax.swing.JComboBox )
   {
      comboBox = (javax.swing.JComboBox)component;

		comboBox.removeAllItems();
      comboBox.addItem("Yukon Servers Only");
      comboBox.addItem("All Servers");

      comboBox.addActionListener( this );
   }

}

public void executeRefreshButton() 
{
   getJTableModel().initTable();
}


public void exportDataSet() {}

public javax.swing.JButton[] getJButtons() 
{  
   return buttonsArray;
}


// Overide this method so TDC knows what the JLabel should display
// for the JCombo box that normally displays "Display:"
public String getJComboLabel()
{
   return "Sort By:";
}


/**
 * Return the JScrollPaneServices property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneTable().setViewportView(getJTableServices());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 10:07:00 PM)
 * @return com.comopt.windows.ServiceTableModel
 */
private ServiceTableModel getJTableModel() 
{
	return (ServiceTableModel)
		((com.cannontech.common.gui.util.SortTableModelWrapper)
				getJTableServices().getModel()).getRealDataModel();
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 * @return JTable[]
 */
public javax.swing.JTable[] getJTables()
{
   javax.swing.JTable[] tables =
   {
      getJTableServices()
   };
   
   return tables;
}


/**
 * Return the JTableServices property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableServices() {
	if (ivjJTableServices == null) {
		try {
			ivjJTableServices = new javax.swing.JTable();
			ivjJTableServices.setName("JTableServices");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableServices.getTableHeader());
			ivjJTableServices.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableServices.createDefaultColumnsFromModel();
         
         try
         {
   			ivjJTableServices.setModel( 
   				new com.cannontech.common.gui.util.SortTableModelWrapper(
   					new ServiceTableModel()) );
				
				getJTableModel().initTable();
         }
         catch( Exception e )
         {
            com.cannontech.clientutils.CTILogger.error(
               "Exception occurred when opening the Service Manager." + System.getProperty("line.separator") +
               "Be sure you have access and user rights to operate the Yukon Server services",
               e );
 				
 				JOptionPane.showMessageDialog( this, "Be sure you have access and user rights to operate the Yukon Server services" );
         }

			
			ivjJTableServices.setDefaultRenderer( 
					Object.class, 
					new ServiceRenderer() );

			setTableHeaderListener();

         // init the popup box connections
         java.awt.event.MouseListener subListener = new com.cannontech.clientutils.popup.PopUpMenuShower( getServicePopUpMenu() );
         ivjJTableServices.addMouseListener( subListener );
         getServicePopUpMenu().addPopupMenuListener( this );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableServices;
}


/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public javax.swing.JPanel getMainJPanel()
{
   return this;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getName()
{
   return "Windows Services";
}


private ServicePopUpMenu getServicePopUpMenu()
{
   if( servicePopUpMenu == null )
      servicePopUpMenu = new ServicePopUpMenu();
      
   return servicePopUpMenu;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getVersion() 
{
   return "1.0.0";
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


public void initChild()
{
	initialize();
}


private void initConnections()
{

   getJTableServices().addMouseListener( new java.awt.event.MouseAdapter()
   {
      public void mousePressed(java.awt.event.MouseEvent e) 
      {
         int rowLocation = getJTableServices().rowAtPoint( e.getPoint() );
         getJTableServices().getSelectionModel().setSelectionInterval(
                     rowLocation, rowLocation );
      }
   });


	getJButtonStartAll().addActionListener( this );
	getJButtonStopAll().addActionListener( this );
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
      
      JNTServices.getInstance().setMachineName(
			DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE ) );
      
      getServicePopUpMenu().addPropertyChangeListener( "PopUpChange", getJTableModel() );
      
		//add any JButtons here
		javax.swing.JButton[] buttons =
		{
			getJButtonStartAll(),
			getJButtonStopAll()		
		};
		setJButtons( buttons );
      
		// user code end
		setName("WinServicePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(366, 287);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 0; constraintsJScrollPaneTable.gridy = 0;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.ipadx = 342;
		constraintsJScrollPaneTable.ipady = 235;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
   
   initConnections();        
   
	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public JButton getJButtonStartAll() 
{
	if( jButtonStartAll == null )
	{
		jButtonStartAll = new JButton("Start All");
		jButtonStartAll.setActionCommand("StartAll");
		jButtonStartAll.setMnemonic('t');
		jButtonStartAll.setPreferredSize( new java.awt.Dimension(80,23) );
		jButtonStartAll.setToolTipText("Starts all available services");
	}

	return jButtonStartAll;
}

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public JButton getJButtonStopAll() 
{
	if( jButtonStopAll == null )
	{
		jButtonStopAll= new JButton("Stop All");
		jButtonStopAll.setActionCommand("StopAll");
		jButtonStopAll.setMnemonic('s');
		jButtonStopAll.setPreferredSize( new java.awt.Dimension(80,23) );
		jButtonStopAll.setToolTipText("Stops all available services");
	}

	return jButtonStopAll;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		WinServicePanel aWinServicePanel;
		aWinServicePanel = new WinServicePanel();
		frame.setContentPane(aWinServicePanel);
		frame.setSize(aWinServicePanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


public boolean needsComboIniting()
{
	return false;
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	if( e.getSource() == getServicePopUpMenu()
		 && getJTableServices().getSelectedRow() >= 0 ) 
	{      
      NTService service = getJTableModel().getRowAt( getJTableServices().getSelectedRow() );
   
      if( service != null )
      {
         getServicePopUpMenu().setNtServices( 
                  service,
                  getJTableModel().isServiceIdle(service) );
      }
      
   }

}


public void print() {}


public void printPreview() {}


public void removeActionListenerFromJComponent( javax.swing.JComponent component ) 
{
   if( component instanceof javax.swing.JComboBox )
   {
      comboBox.removeActionListener( this );
      comboBox = null;
   }

}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:52:15 PM)
 * @param hgridLines boolean
 */
public void setGridLines(boolean hGridLines, boolean vGridLines ) 
{
   int vLines = ((vGridLines == true) ? 1 : 0);
   int hLines = ((hGridLines == true) ? 1 : 0);

   for( int i = 0; i < getJTables().length; i++ )
   {
      getJTables()[i].setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
      getJTables()[i].setShowHorizontalLines( hGridLines );
      getJTables()[i].setShowVerticalLines( vGridLines );

      getJTables()[i].revalidate();
      getJTables()[i].repaint();
   }
   
}


public void setInitialTitle()
{
   // we must have the panel realize its the first time the connection
   //  is being observed
   java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   if( f != null )
      f.setTitle("Yukon Servers");
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:08:24 AM)
 * @param buttons javax.swing.JButton[]
 */
public void setJButtons(javax.swing.JButton[] buttons) 
{
   buttonsArray = buttons; 
}


public void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor ) {}
public void setAlarmMute( boolean muteToggle ) {}
public void silenceAlarms() {}


public void setTableFont(java.awt.Font font ) 
{
   super.setFont( font );

   // Set the values for the table model
   //get.getSubBusTableModel().setFontValues( font.getName(), font.getSize() );
      
   for( int i = 0; i < getJTables().length; i++ )
   {
      getJTables()[i].setFont( font );
      getJTables()[i].setRowHeight( font.getSize() + 3 );
      // set the table headers font
      getJTables()[i].getTableHeader().setFont( font );


      getJTables()[i].revalidate();
      getJTables()[i].repaint();
   }

}


/**
 * Return the JScrollPaneServices property value.
 * @return javax.swing.JScrollPane
 */
private void setTableHeaderListener() 
{
	javax.swing.table.JTableHeader hdr = 
			(javax.swing.table.JTableHeader)getJTableServices().getTableHeader();

/*	hdr.setToolTipText("Dbl Click on a column header to sort");

	// The actual listener is defined here
	hdr.addMouseListener( new java.awt.event.MouseAdapter() 
	{
		public void mouseClicked(java.awt.event.MouseEvent e)
		{
			if( e.getClickCount() == 2 )
			{
				int vc = getJTableServices().getColumnModel().getColumnIndexAtX( e.getX() );
				int mc = getJTableServices().convertColumnIndexToModel( vc );

				java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( WinServicePanel.this );
				
				java.awt.Cursor original = owner.getCursor();	
				owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
				
				try
				{
					((com.cannontech.common.gui.util.SortTableModelWrapper)
									getJTableServices().getModel()).sort( mc );

					getJTableServices().repaint();
				}
				finally
				{
					owner.setCursor( original );
				}
				
			}
			
		};
		
	});	
*/
	
}
}