package com.cannontech.tdc;

import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.bookmark.BookMarkSelectionListener;

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
   private int alarmCount = 0;
   
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
  
      alarmMenu.addMouseListener( new java.awt.event.MouseAdapter()
      {
         public void mousePressed( java.awt.event.MouseEvent e )
         {
            if( alrmBlinker != null )
               alrmBlinker.interrupt();
         }         
      });

	}

   private javax.swing.JMenu getJMenuAlarms()
   {
      return alarmMenu;
   }
   
   public synchronized void handleSignal( Signal sig )
   {

      boolean foundSig = false;
      int prevAlrmCnt = alarmCount;
      boolean addAlarm = TagUtils.isAnyAlarm(sig.getTags())
                          && TagUtils.isAlarm(sig.getTags()); // Is Alarm UnAcked??
   
      for( int i = 0; i < getJMenuAlarms().getItemCount(); i++ )
      {
          com.cannontech.message.dispatch.message.Signal storedSig = 
             (com.cannontech.message.dispatch.message.Signal)
               getJMenuAlarms().getItem(i).getClientProperty("com.cannontech.Signal");
   
         //we already have a JMenuItem for this signal
         if( storedSig != null )
         {
            if( storedSig.getId() == sig.getId() ) //update the sig value
            {
               if( addAlarm )  //update the underlying signal
               {
                  getJMenuAlarms().getItem(i).putClientProperty( 
                        "com.cannontech.Signal", sig );
                  
                  com.cannontech.database.data.lite.LitePoint lp =      
                     com.cannontech.database.cache.functions.PointFuncs.getLitePoint( (int)sig.getId() );
                  
                  getJMenuAlarms().getItem(i).setText(
                        "[" + (new ModifiedDate(sig.getTimeStamp().getTime()).toString()) + "] " +
                        com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(lp.getPaobjectID()) +
                        " : " +
                        lp.getPointName() );
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
      
      
      //set the color of our item
      if( getJMenuAlarms().getItemCount() <= 0 )
      {
         getJMenuAlarms().setForeground( fgColor );
         getJMenuAlarms().setBackground( bgColor );
      }
      else
      {
         getJMenuAlarms().setForeground( ALARM_FG_COLOR );         
         
         //only start blinking if we added an alarm
         if( alarmCount > prevAlrmCnt )
            createMenuBlinker();
      }
         
         
      //update the text of our menu
      getJMenuAlarms().setText( "Alarms: " + alarmCount );
   }
   
   private void removeAlarm( int menuIndx )
   {
      
      getJMenuAlarms().remove( menuIndx );

      //decrement the alarm count
      alarmCount = (alarmCount <= 0 ? 0 : alarmCount - 1);
      
   }

   private void addAlarm( Signal sig )
   {
       javax.swing.JMenuItem newItem = new javax.swing.JMenuItem(
            "[" + (new ModifiedDate(sig.getTimeStamp().getTime()).toString()) + "] " +
            com.cannontech.database.cache.functions.PointFuncs.getPointName((int)sig.getId()) );
       
      newItem.putClientProperty( "com.cannontech.Signal", sig );
      newItem.setBackground(java.awt.SystemColor.control);
      newItem.setForeground(java.awt.SystemColor.controlText);

      newItem.putClientProperty( TDCMainPanel.PROP_BOOKMARK, 
            Display.DISPLAY_TYPES[Display.ALARMS_AND_EVENTS_TYPE_INDEX] +
            com.cannontech.tdc.bookmark.BookMarkBase.BOOKMARK_TOKEN +
            com.cannontech.database.cache.functions.AlarmCatFuncs.getAlarmCategoryName(
                  (int)sig.getAlarmStateID()) );


      newItem.addActionListener(
         new com.cannontech.tdc.bookmark.SelectionHandler( bookmarkListener ) );

      getJMenuAlarms().add( newItem );


      //increment the alarm count      
      alarmCount++;      
   }
   
   private void createMenuBlinker()
   {

      javax.swing.BorderFactory.createLineBorder( ALARM_BG_COLOR );
      if( alrmBlinker == null )
      {
         Runnable r = new Runnable()
         {
            public void run()
            {
               try
               {
                  int i = 0;
                  while( getJMenuAlarms().getItemCount() > 0 )
                  {
                     getJMenuAlarms().setBackground( ALARM_BG_COLOR );
                     Thread.currentThread().sleep(1000);
                     
                     getJMenuAlarms().setBackground( bgColor );
                     Thread.currentThread().sleep(1000);



/*getJMenuAlarms().getGraphics().setColor( ALARM_BG_COLOR );

      getJMenuAlarms().getGraphics().drawLine(
         i,
         0,
         i,
         getJMenuAlarms().getHeight() );

i++;
if( i > getJMenuAlarms().getWidth() )
{
   i = 0;   
}

System.out.println("i="+i+", wid="+getJMenuAlarms().getWidth() );

Thread.currentThread().sleep(100);
*/
                  }
               }
               catch( Exception e ) {}
               finally
               {
                  getJMenuAlarms().setBackground( bgColor );

                  alrmBlinker = null;
               }
               
            }
            
         };
         
         alrmBlinker = new Thread(r, "AlarmsCountThread" );
         alrmBlinker.start();
      }
      
   }
   
   
}
