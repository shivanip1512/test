/*********************************************************************************
                                    KDESysTray.java
                                    ---------------
    author               : Tamas Bara
    copyright            : (C) 2002-2003 by SnoozeSoft
    email                : snoozesoft@compuserve.de
 *********************************************************************************/

/*********************************************************************************
 *                                                                               *
 *   This library is free software; you can redistribute it and/or               *
 *   modify it under the terms of the GNU Lesser General Public                  *
 *   License as published by the Free Software Foundation; either                *
 *   version 2.1 of the License, or (at your option) any later version.          *
 *                                                                               *
 *   This library is distributed in the hope that it will be useful,             *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 *   Lesser General Public License for more details.                             *
 *                                                                               *
 *   You should have received a copy of the GNU Lesser General Public            *
 *   License along with this library; if not, write to the Free Software         *
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA   *
 *                                                                               *
 *********************************************************************************/

package snoozesoft.systray4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

class KDESysTray implements SysTrayAccess, Runnable
{
    private final static String REQ_ADD_MAINMENU = "ADD MAINMENU";
    private final static String REQ_ADD_SUBMENU = "ADD SUBMENU";
    private final static String REQ_SET_ICON = "SET ICON";
    private final static String REQ_SHOW_ICON = "SHOW ICON";
    private final static String REQ_SET_TOOLTIP = "SET TOOLTIP";
    private final static String REQ_ADD_ITEM = "ADD ITEM";
    private final static String REQ_ENABLE_ITEM = "ENABLE ITEM";
    private final static String REQ_CHECK_ITEM = "CHECK ITEM";
    private final static String REQ_SET_ITEMLABEL = "SET ITEMLABEL";
    private final static String REQ_REMOVE_ITEM = "REMOVE ITEM";
    private final static String REQ_REMOVE_ALL = "REMOVE ALL";

    private final static String MSG_ICON_CLICKED = "ICON CLICKED";
    private final static String MSG_ICON_DBLCLICKED = "ICON DBLCLICKED";
    private final static String MSG_ITEM_SELECTED = "ITEM SELECTED";

    private HashMap menus;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean available;
    private int idCounter;
    private boolean ignoreException;

    KDESysTray()
    {
        available = true;
        try
        {
            int port = Integer.parseInt( SysTrayManager.properties.getProperty( "KDESysTray.port" ) );
            socket = new Socket( "localhost", port );
            reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            writer = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );

            menus = new HashMap();
            idCounter = 1;
            ignoreException = false;

            new Thread( this, "KDESysTray" ).start();
        }
        catch( Exception e ) { available = false; }
    }

    public void run()
    {
        try
        {
            int index = 0;
            String line = null;
            Integer menuId = null;
            Object menu = null;
            SysTrayMenu mainMenu = null;
            SubMenu subMenu = null;
            String message = reader.readLine();
            while( message != null )
            {
                if( message.equals( MSG_ICON_CLICKED ) )
                {
                    line = reader.readLine();
                    menuId = Integer.decode( line );
                    mainMenu = ( SysTrayMenu ) menus.get( menuId );
                    mainMenu.iconLeftClicked( false );
                }
                else if( message.equals( MSG_ITEM_SELECTED ) )
                {
                    line = reader.readLine();
                    menuId = Integer.decode( line );

                    line = reader.readLine();
                    index = Integer.parseInt( line );

                    menu = menus.get( menuId );
                    if( menu instanceof SysTrayMenu )
                    {
                        mainMenu = ( SysTrayMenu ) menu;
                        mainMenu.menuItemSelected( index );
                    }
                    else
                    {
                        subMenu = ( SubMenu ) menu;
                        subMenu.menuItemSelected( index );
                    }
                }
                else if( message.equals( MSG_ICON_DBLCLICKED ) )
                {
                    line = reader.readLine();
                    menuId = Integer.decode( line );
                    mainMenu = ( SysTrayMenu ) menus.get( menuId );
                    mainMenu.iconLeftClicked( true );
                }
                else
                {
                    System.out.println( "KDESysTray - unknown message: " + message );

                    break;
                }

                message = reader.readLine();
            }
        }
        catch( IOException e )
        {
            if( !ignoreException ) e.printStackTrace();
        }
    }

    public boolean isAvailable()
    {
        return available;
    }

    public int addMainMenu( SysTrayMenu menu, String iconFileName, String toolTip )
    {
       int id = -1;

        try
        {
            writer.write( REQ_ADD_MAINMENU + "\n" );
            writer.write( idCounter + "\n" );
            writer.write( iconFileName + "\n" );
            writer.write( toolTip + "\n" );
            writer.flush();

            menus.put( new Integer( idCounter ), menu );

            id = idCounter++;
        }
        catch( IOException e ) { e.printStackTrace(); }

        return id;
    }

    public int addSubMenu( SubMenu menu )
    {
        int id = -1;

        try
        {
            writer.write( REQ_ADD_SUBMENU + "\n" );
            writer.write( idCounter + "\n" );
            writer.flush();

            menus.put( new Integer( idCounter ), menu );

            id = idCounter++;
        }
        catch( IOException e ) { e.printStackTrace(); }

        return id;
    }

    public void setToolTip( int menuId, String tip )
    {
        try
        {
            writer.write( REQ_SET_TOOLTIP + "\n" );
            writer.write( menuId + "\n" );
            writer.write( tip + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void showIcon( int menuId, boolean show )
    {
        try
        {
            writer.write( REQ_SHOW_ICON + "\n" );
            writer.write( menuId + "\n" );
            writer.write( show + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void setIcon( int menuId, String iconFileName )
    {
        try
        {
            writer.write( REQ_SET_ICON + "\n" );
            writer.write( menuId + "\n" );
            writer.write( iconFileName + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void enableItem( int menuId, int itemIndex, boolean enable )
    {
        try
        {
            writer.write( REQ_ENABLE_ITEM + "\n" );
            writer.write( menuId + "\n" );
            writer.write( itemIndex + "\n" );
            writer.write( enable + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void checkItem( int menuId, int itemIndex, boolean check )
    {
        try
        {
            writer.write( REQ_CHECK_ITEM + "\n" );
            writer.write( menuId + "\n" );
            writer.write( itemIndex + "\n" );
            writer.write( check + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void setItemLabel( int menuId, int itemIndex, String label )
    {
        try
        {
            writer.write( REQ_SET_ITEMLABEL + "\n" );
            writer.write( menuId + "\n" );
            writer.write( itemIndex + "\n" );
            writer.write( label + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void addItem( int menuId,
                         int itemIndex,
                         String label,
                         boolean checkable,
                         boolean check,
                         boolean enable )
    {
        try
        {
            writer.write( REQ_ADD_ITEM + "\n" );
            writer.write( menuId + "\n" );
            writer.write( itemIndex + "\n" );
            writer.write( label + "\n" );
            writer.write( checkable + "\n" );
            writer.write( check + "\n" );
            writer.write( enable + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void removeItem( int menuId, int itemIndex )
    {
        try
        {
            writer.write( REQ_REMOVE_ITEM + "\n" );
            writer.write( menuId + "\n" );
            writer.write( itemIndex + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void removeAll( int menuId )
    {
        try
        {
            writer.write( REQ_REMOVE_ALL + "\n" );
            writer.write( menuId + "\n" );
            writer.flush();
        }
        catch( IOException e ) { e.printStackTrace(); }
    }

    public void dispose()
    {
        ignoreException = true;
        try{ socket.close(); }
        catch( IOException e ) { e.printStackTrace(); }
    }
}
