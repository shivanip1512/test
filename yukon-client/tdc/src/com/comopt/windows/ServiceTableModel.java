package com.comopt.windows;

import java.awt.Color;
import java.util.Arrays;
import java.util.Vector;

import javax.jms.IllegalStateException;
import javax.sound.midi.SysexMessage;
import javax.swing.table.AbstractTableModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;


/**
 * @author Owner
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServiceTableModel extends AbstractTableModel implements com.cannontech.tdc.alarms.gui.AlarmTableModel, com.cannontech.common.gui.util.SortableTableModel, java.beans.PropertyChangeListener
{
	private Vector rows = null;
   private boolean showOnlyYukon = true;
   private Vector serviceChangers = new Vector(10);
   private boolean hasConnected = false;

	//The columns and their column index	
	//public static final int COL_INDEX = 0;
	public static final int COL_FULL_NAME = 0;
	public static final int COL_SHORT_NAME = 1;
	public static final int COL_TYPE = 2;
	public static final int COL_STATE = 3;
	

	private static final String[] COLUMN_NAMES = 
	{ 
		//"Index", 
		"Full Name", 
		"Short Name", 
		"Type", 
		"State" 
	};

	//The color schemes - based on the service status
	private final static Color[] CELL_COLORS =
	{
		//Stopped
		Color.white,
		//Stop Pending
		Color.yellow,			//1

		//Running
		Color.green,

		//Paused
		Color.orange,
		
		//Disabled program
		Color.red				//4
	};
   
   public static final Color DISABLED_COLOR = Color.gray;
   
   /**
    * Insert the method's description here.
    * Creation date: (10/9/2002 10:10:00 PM)
    */
   public ServiceTableModel() 
   {
   	super();      
   }

   public void initTable() throws java.lang.IllegalStateException
   {
      setServiceData();
      
      if( !hasConnected )
      	throw new java.lang.IllegalStateException("Unable to connect to the service control manager");
   }
   
   public int findServiceIndex( String serviceName )
   {
      for( int i = 0; i < getRowCount(); i++ )
         if( getRowAt(i).getServiceName().equalsIgnoreCase(serviceName) )
            return i;
      
      return -1;
   }   

   public void propertyChange( java.beans.PropertyChangeEvent e )
   {
      javax.swing.JComponent src = (javax.swing.JComponent)e.getSource();
      String destState = e.getOldValue().toString();
      String serviceName = e.getNewValue().toString();
      
      startRefresh( destState, serviceName, src );
   }
   
   private void startRefresh( final String destState, final String serviceName, final javax.swing.JComponent src )
   {
   	if( !hasConnected )
   		return;
    
      //start the refresh thread of the service that changed
      new Thread( new Runnable()
      {
         public void run()
         {                 
            try
            {
               refreshRow(serviceName, false, -1);
                              
               int ret = 0;
               boolean refresh = "refresh".equalsIgnoreCase(destState);

               if( IServiceConstants.STATE_RUNNING.equalsIgnoreCase(destState) )
               {
                  ret = JNTServices.getInstance().start(
                                 serviceName,
                                 0,
                                 null );                  
               }
               else if( IServiceConstants.STATE_STOPPED.equalsIgnoreCase(destState) )
               {
               	//does not return until the service has stopped or timed out
                  ret = JNTServices.getInstance().stop(
                                 serviceName,
                                 6,
                                 10000);

               }


               if( ret != 0 )
               {
                  String error = NTService.createErrorString( ret, serviceName );
                  com.cannontech.clientutils.CTILogger.info( error );
                  
                  javax.swing.JOptionPane.showMessageDialog( src, error );
                  CTILogger.info( "ERROR : " + error + " (" + ret + ")" );
                  
                  //force a refresh of the row
                  refresh = true;
               }


              int secsTimeOut = 60, to = 0;

              do
              {
                 int state = JNTServices.getInstance().getStatus( serviceName );
                 
                 refreshRow(serviceName, false, state );
                 
                 Thread.currentThread().sleep(1000);
              }
              while( to++ < secsTimeOut 
                      && (!getRowAt(findServiceIndex(serviceName)).getCurrentStateString().equalsIgnoreCase(destState))
                      && (!refresh) );
              
              CTILogger.info("Refresh of service done");
            }
            catch( Exception e ) 
            {
               CTILogger.error( "Error in refresh service.", e );
            }
            finally
            {
               //since we are done with this service, remove it from the op list
               refreshRow(serviceName, true, -1 );
            }
            
            //we timed out
            //if( to >= timeOut )
            
         }
      }, "ServiceRefreshThread").start();
      
      
   }
   
   private synchronized void refreshRow( String serviceName, boolean remove, int state )
   {
      int i = findServiceIndex(serviceName);
      
      if( state >= 0 )
         getRowAt(i).setCurrentState( state );


      if( remove )
      {
         serviceChangers.removeElement( serviceName );
      }
      else if( !serviceChangers.contains(serviceName) )
      {
         serviceChangers.add( serviceName );
      }

      fireTableRowsUpdated( i, i );
   }
   
   
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public void forcePaintTableRowUpdated(int minLocation, int maxLocation)
	{
		//no
	}

	public synchronized java.awt.Color getCellBackgroundColor(int row, int col) 
	{
      if( row >= 0 && row < getRowCount()
          && col >= 0 && col <= getColumnCount() )
      {
         NTService service = getRowAt(row);
         
         if( serviceChangers.contains(service.getServiceName()) )
            return DISABLED_COLOR;
      }

		return Color.black;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public synchronized java.awt.Color getCellForegroundColor(int row, int col) 
	{
		if( row >= 0 && row < getRowCount()
			 && col >= 0 && col <= getColumnCount() )
		{
			NTService service = getRowAt(row);
			
			if( service.getServiceTypeString().equalsIgnoreCase("Disabled") )
			{
				return CELL_COLORS[4];
			}
			else if( IServiceConstants.STATE_RUNNING.equalsIgnoreCase(service.getCurrentStateString()) )
			{
				return CELL_COLORS[2];
			}
			else if( IServiceConstants.STATE_CONT_PENDING.equalsIgnoreCase(service.getCurrentStateString())
					    || IServiceConstants.STATE_PAUSE_PENDING.equalsIgnoreCase(service.getCurrentStateString())
					    || IServiceConstants.STATE_START_PENDIONG.equalsIgnoreCase(service.getCurrentStateString())
					    || IServiceConstants.STATE_STOP_PENDING.equalsIgnoreCase(service.getCurrentStateString()) )
			{
				return CELL_COLORS[1];
			}
			else if( IServiceConstants.STATE_PAUSED.equalsIgnoreCase(service.getCurrentStateString()) )
			{
				return CELL_COLORS[3];
			}

		}
	
		return CELL_COLORS[0];
	}
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public String getColumnName(int column) 
	{
		return COLUMN_NAMES[column];
	}
	public NTService getRowAt( int rowIndex )
	{
		return (NTService)getRows().get( rowIndex );
	}
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return getRows().size();
	}
	/**
	 * Returns the rows.
	 * @return Vector
	 */
	public Vector getRows()
	{
		if( rows == null )
			rows = new Vector(10);

		return rows;
	}
	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		NTService service = getRowAt( rowIndex );
		
		switch( columnIndex )
		{
			case COL_FULL_NAME:
				return service.getDisplayName();

			case COL_SHORT_NAME:
				return service.getServiceName();

			case COL_TYPE:
				return service.getServiceTypeString();

			case COL_STATE:
				return service.getCurrentStateString();
			
		}
		
		return " ";
	}
	/**
	 * Returns the rows.
	 * @return Vector
	 */
	public boolean isPlayingSound()
	{
		return false;
	}
	public boolean isRowSelectedBlank(int rowNumber)
	{
		return false;
	}
	public void rowDataSwap(int i, int j)
	{
		Object tmp = getRows().elementAt(i);
		getRows().setElementAt( getRows().elementAt(j), i );
		getRows().setElementAt( tmp, j );		
	}
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public boolean setBGRowColor(int rowNumber, int color)
	{
		return false;
	}

   /**
    * Insert the method's description here.
    * Creation date: (10/9/2002 10:03:14 PM)
    * @param data java.lang.String[]
    */
   private void setServiceData() 
   {
   	
   	//remove all the current rows
   	getRows().clear();
   	
   	try
   	{
	   	String[] data = JNTServices.getInstance().getAllServices();
	   	
System.out.println("*** = " + data );

	   	if( data == null)
	   		return;
	   
	   	//sort by the string values of each serice (full name first)   	
	   	Arrays.sort( data );
	
	   	for( int i = 0; i < data.length; i++ )
	   	{
	   		NTService service = new NTService( data[i] );
	   
	         if( isServiceValid(service) )
	            getRows().add( service );
	   	}
	   	
	   	hasConnected = true;
   	}
   	catch( Exception e )
   	{
   		CTILogger.error( "Problem with the data returned from the Service Manager", e );
   	}   	
		
      fireTableDataChanged();
   }
   
   public synchronized boolean isServiceIdle( NTService service )
   {
      return !serviceChangers.contains( service.getServiceName() );
   }

   public void setYukonFilter( boolean value )
   {
      showOnlyYukon = value;


   	if( !hasConnected )
   		return;
   
      setServiceData();
   }
   
   private boolean isServiceValid( NTService service )
   {
      if( showOnlyYukon )
      {
         return IServiceConstants.SERVICE_GROUP.equalsIgnoreCase(service.getLoadGroup());
      }
      else
         //only have services of type SERVICE_TYPE_WIN32
         return (service.getServiceType() & IServiceConstants.SERVICE_TYPE_WIN32) != 0;
   }


}
