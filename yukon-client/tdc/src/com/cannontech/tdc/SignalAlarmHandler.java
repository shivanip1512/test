package com.cannontech.tdc;

import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.cache.functions.AlarmCatFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.bookmark.BookMarkBase;
import com.cannontech.tdc.bookmark.BookMarkSelectionListener;
import com.cannontech.tdc.utils.TDCDefines;
/**
 * @author rneuharth
 * Oct 15, 2002 at 3:01:29 PM
 * 
 * A undefined generated comment
 */
class SignalAlarmHandler
{
   private static final java.awt.Color ALARM_BG_COLOR = java.awt.Color.black;
   private static final java.awt.Color ALARM_FG_COLOR = java.awt.Color.red.brighter();
   
   private static java.awt.Color bgColor = java.awt.SystemColor.control;
   private static java.awt.Color fgColor = java.awt.SystemColor.controlText;
         
   private Thread alrmBlinker = null;
   private javax.swing.JMenu alarmMenu = null;
   //private int alarmCount = 0;

   //contains javax.swing.JMenuItem
   private java.util.Vector alarmVector = null;
   
   public static final int ALARMS_DISPLAYED = Integer.parseInt(
			ClientSession.getInstance().getRolePropertyValue(
                  TDCRole.TDC_ALARM_COUNT, 
                  "3") );

   
   private BookMarkSelectionListener bookmarkListener = null;

	/**
	 * Constructor for SignalAlarmHandler.
	 */
	public SignalAlarmHandler( javax.swing.JMenu menu_, BookMarkSelectionListener bookmarkListener_ )
	{
      super();
      
      if( menu_ == null || bookmarkListener_ == null )
         throw new IllegalArgumentException("Constuctor must take a non null values for: " + getClass().getName() );

      alarmMenu = menu_;
      bookmarkListener = bookmarkListener_;
      bgColor = alarmMenu.getBackground();
      fgColor = alarmMenu.getForeground();
      
      getJMenuAlarms().setHorizontalTextPosition( javax.swing.SwingConstants.RIGHT );
  
      alarmMenu.addMouseListener( new java.awt.event.MouseAdapter()
      {
         public void mousePressed( java.awt.event.MouseEvent e )
         {
            if( alrmBlinker != null )
               alrmBlinker.interrupt();
         }         
      });

	}

   private synchronized java.util.Vector getAlarmVector()
   {
      if( alarmVector == null )
         alarmVector = new java.util.Vector(10);
   
      return alarmVector;
   }
   
   private javax.swing.JMenu getJMenuAlarms()
   {
      return alarmMenu;
   }
   
   public synchronized void handleSignal( Signal sig )
   {

      boolean foundSig = false;
      int prevAlrmCnt = getAlarmVector().size();//alarmCount;
      boolean addAlarm = TagUtils.isAlarmUnacked(sig.getTags());
   
      for( int i = 0; i < getAlarmVector().size(); i++ )
      {
         javax.swing.JMenuItem menuItem = (javax.swing.JMenuItem)getAlarmVector().get(i);
         
          Signal storedSig = 
          	(Signal)menuItem.getClientProperty( SignalAlarmHandler.class.getName() );
   
         //we already have a JMenuItem for this signal
         if( storedSig != null )
         {
            if( storedSig.equals(sig) ) //update the sig value
            {
               if( addAlarm )  //update the underlying signal
               {
                  menuItem.putClientProperty( 
                        SignalAlarmHandler.class.getName(), sig );
                  
                  LitePoint lp =      
                     PointFuncs.getLitePoint( (int)sig.getPointID() );
                  
                  menuItem.setText(
								"(" + PAOFuncs.getYukonPAOName(lp.getPaobjectID()) +
                        " / " + lp.getPointName() + ") " + 
								sig.getDescription() +
								(TagUtils.isAlarmUnacked(sig.getTags())
								 ? "" : " (ACKED)") );
								
						menuItem.setToolTipText( sig.getAction() +
								" @ " + (new ModifiedDate(sig.getTimeStamp().getTime()).toString()) );
                        
               }
               else  //remove the JMenuItem
               {
                  removeAlarm( i );                  
               }
                     
               foundSig = true;
               break;
            }
               
         }      
      }
      
      //we may need to add this new signal to the list
      if( !foundSig && addAlarm )
      {
         addAlarm( sig );
      }
      
      
      //keep our list in order!
      java.util.Collections.sort(
         getAlarmVector(),
         new java.util.Comparator()
         {            
            public int compare(Object o1, Object o2)
            {
               long val1 = ((Signal)
                  ((javax.swing.JMenuItem)o1).getClientProperty(
                  SignalAlarmHandler.class.getName())).getTimeStamp().getTime();
                  
               long val2 = ((Signal)
                  ((javax.swing.JMenuItem)o2).getClientProperty(
                  SignalAlarmHandler.class.getName())).getTimeStamp().getTime();

               return ( val1 < val2 ? 1 : (val1 == val2 ? 0 : -1) );
            }
            
         });

      
      //set the color of our item
      if( getAlarmVector().size() <= 0 )
      {
         getJMenuAlarms().setForeground( fgColor );
         getJMenuAlarms().setBackground( bgColor );
      }
      else
      {
         getJMenuAlarms().setForeground( ALARM_FG_COLOR );         
         
         //only start blinking if we added an alarm
         if( getAlarmVector().size() > prevAlrmCnt )
            createMenuBlinker();
      }
         
         
      //update the text of our menu
      getJMenuAlarms().setText( "Alarms: " + getAlarmVector().size() );
      
      
      updateAlarmMenu();
   }
   
   private void removeAlarm( int menuIndx )
   {
      getAlarmVector().remove( menuIndx );      
   }

   private void updateAlarmMenu()
   {
      synchronized( getAlarmVector() )
      {
         getJMenuAlarms().removeAll();
         
         for( int i = 0; i < getAlarmVector().size(); i++ )
         {
            javax.swing.JMenuItem item = (javax.swing.JMenuItem)getAlarmVector().get(i);
            
            if( i < ALARMS_DISPLAYED )
            {
               getJMenuAlarms().add( item );
            }
            
         }
         
      }
      
   }
   
   private void addAlarm( Signal sig )
   {
   	if( sig == null )
   		return;

		LitePoint lp =      
			PointFuncs.getLitePoint( (int)sig.getPointID() );

		javax.swing.JMenuItem newItem = new javax.swing.JMenuItem(
			"(" + PAOFuncs.getYukonPAOName(lp.getPaobjectID()) +
			" / " + lp.getPointName() + ") " + 
			sig.getDescription() +
			(TagUtils.isAlarmUnacked(sig.getTags())
			 ? "" : " (ACKED)") );
								
		newItem.setToolTipText( sig.getAction() +
			" @ " + (new ModifiedDate(sig.getTimeStamp().getTime()).toString()) );

       
      newItem.putClientProperty( SignalAlarmHandler.class.getName(), sig );
      newItem.setBackground(java.awt.SystemColor.control);
      newItem.setForeground(java.awt.SystemColor.controlText);

      newItem.putClientProperty( TDCMainPanel.PROP_BOOKMARK, 
            Display.DISPLAY_TYPES[Display.ALARMS_AND_EVENTS_TYPE_INDEX] +
            BookMarkBase.BOOKMARK_TOKEN +
            AlarmCatFuncs.getAlarmCategoryName(
                  (int)sig.getCategoryID()) );


      newItem.addActionListener(
         new com.cannontech.tdc.bookmark.SelectionHandler( bookmarkListener ) );

      getAlarmVector().add( newItem );
   }
   
   private void createMenuBlinker()
   {            
      if( alrmBlinker == null )
      {
         Runnable r = new Runnable()
         {
            public void run()
            {
               try
               {
                  while( getAlarmVector().size() > 0 )
                  {
                     if( !TDCDefines.ICON_ALARM.equals(getJMenuAlarms().getIcon()) )
                        getJMenuAlarms().setIcon( TDCDefines.ICON_ALARM );
                     
                     Thread.currentThread().sleep(1000);

                     //below is code that uses only color and not the animated gif
                     /*getJMenuAlarms().setBackground( ALARM_BG_COLOR );
                     Thread.currentThread().sleep(1000);
                     
                     getJMenuAlarms().setBackground( bgColor );
                     Thread.currentThread().sleep(1000);*/

                  }
               }
               catch( Exception e ) {}
               finally
               {
                  //getJMenuAlarms().setBackground( bgColor );
                  getJMenuAlarms().setIcon( null );                  

                  alrmBlinker = null;
               }
               
            }
            
         };


         alrmBlinker = new Thread(r, "AlarmsCountThread" );
         alrmBlinker.start();
      }

      
   }
   
   
}
