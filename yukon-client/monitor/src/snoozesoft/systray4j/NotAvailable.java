/*********************************************************************************
                                  NotAvailable.java
                                  -----------------
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

class NotAvailable implements SysTrayAccess
{
    public boolean isAvailable()
    {
        return false;
    }

    public int addMainMenu( SysTrayMenu menu, String iconFileName, String toolTip )
    {
        printError();

        return 0;
    }

    public int addSubMenu( SubMenu menu )
    {
        printError();

        return 0;
    }

    public void setToolTip( int menuId, String tip )
    {
        printError();
    }

    public void showIcon( int menuId, boolean show )
    {
        printError();
    }

    public void setIcon( int menuId, String iconFileName )
    {
        printError();
    }

    public void enableItem( int menuId, int itemIndex, boolean enable )
    {
        printError();
    }

    public void checkItem( int menuId, int itemIndex, boolean check )
    {
        printError();
    }

    public void setItemLabel( int menuId, int itemIndex, String label )
    {
        printError();
    }

    public void addItem( int menuId,
                         int itemIndex,
                         String label,
                         boolean checkable,
                         boolean check,
                         boolean enable )
    {
        printError();
    }

    public void removeItem( int menuId, int itemIndex )
    {
        printError();
    }

    public void removeAll( int menuId )
    {
        printError();
    }

    public void dispose()
    {
        printError();
    }

    private void printError()
    {
        System.err.println( "SysTray for Java is not available." );
    }
}
