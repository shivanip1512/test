#include "yukon.h"

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_three_one
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_three_one.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:44:46 $
*    History: 
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_three_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableThreeOne::CtiAnsiTableThreeOne()
{
}
CtiAnsiTableThreeOne::CtiAnsiTableThreeOne( BYTE *dataBlob )
{

    memcpy( (void *)&_displayTable, dataBlob, sizeof( DISP_RCD ));
    dataBlob += sizeof( DISP_RCD ); //10 bytes
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableThreeOne::~CtiAnsiTableThreeOne()
{
    
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableThreeOne& CtiAnsiTableThreeOne::operator=(const CtiAnsiTableThreeOne& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

UINT16 CtiAnsiTableThreeOne::getNbrDispSources( )
{
    return _displayTable.nbrDispSources;
}

UINT8  CtiAnsiTableThreeOne::getWidthDispSources( )
{
    return _displayTable.widthDispSources;
}
UINT16 CtiAnsiTableThreeOne::getNbrPriDispListItems( )
{
    return _displayTable.nbrPriDispListItems;
}
UINT8 CtiAnsiTableThreeOne::getNbrPriDispLists( )
{
    return _displayTable.nbrPriDispLists;
}
UINT16 CtiAnsiTableThreeOne::getNbrSecDispListItems( )
{
    return _displayTable.nbrSecDispListItems;
}
UINT8  CtiAnsiTableThreeOne::getNbrSecDispLists( )
{
    return _displayTable.nbrSecDispLists;
}

bool CtiAnsiTableThreeOne::getOnTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.onTimeFlag;
}
bool CtiAnsiTableThreeOne::getOffTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.offTimeFlag;
}
bool CtiAnsiTableThreeOne::getHoldTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.holdTimeFlag;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableThreeOne::printResult(  )
{
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 31 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Actual Display Dimension Table ** "<<endl;
        dout << "                 On Time Flag: "<<getOnTimeFlag()<<endl;
        dout << "                Off Time Flag: "<<getOffTimeFlag()<<endl;
        dout << "               Hold Time Flag: "<<getHoldTimeFlag()<<endl;
        dout << "          Nbr Display Sources: "<<getNbrDispSources()<<endl;
        dout << "        Width Display Sources: "<<getWidthDispSources()<<endl;
        dout << "   Nbr Pri Display List Items: "<<getNbrPriDispListItems()<<endl;
        dout << "         Nbr Pri Diplay Lists: "<<getNbrPriDispLists()<<endl;
        dout << "   Nbr Sec Display List Items: "<<getNbrSecDispListItems()<<endl;
        dout << "         Nbr Sec Diplay Lists: "<<getNbrSecDispLists()<<endl;

    }

}











