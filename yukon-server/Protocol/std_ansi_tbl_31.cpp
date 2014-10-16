/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_31
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_31.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_31.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable31::CtiAnsiTable31( BYTE *dataBlob, DataOrder dataOrder)
{
    DISP_FLAG_BFLD displayCtrl;
    UINT16         nbrDispSources;
    UINT8          widthDispSources;
    UINT16         nbrPriDispListItems;
    UINT8          nbrPriDispLists;
    UINT16         nbrSecDispListItems;
    UINT8          nbrSecDispLists;

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.displayCtrl, sizeof( DISP_FLAG_BFLD )); //1 byte

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.nbrDispSources, sizeof( UINT16 ), dataOrder); //2bytes

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.widthDispSources, sizeof( UINT8 )); //1 byte

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.nbrPriDispListItems, sizeof( UINT16 ), dataOrder); //2bytes

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.nbrPriDispLists, sizeof( UINT8 )); //1 byte

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.nbrSecDispListItems, sizeof( UINT16 ), dataOrder); //2 bytes

    dataBlob += toAnsiIntParser(dataBlob, &_displayTable.nbrSecDispLists, sizeof( UINT8 )); //1 byte

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable31::~CtiAnsiTable31()
{

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable31& CtiAnsiTable31::operator=(const CtiAnsiTable31& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

UINT16 CtiAnsiTable31::getNbrDispSources( )
{
    return _displayTable.nbrDispSources;
}

UINT8  CtiAnsiTable31::getWidthDispSources( )
{
    return _displayTable.widthDispSources;
}
UINT16 CtiAnsiTable31::getNbrPriDispListItems( )
{
    return _displayTable.nbrPriDispListItems;
}
UINT8 CtiAnsiTable31::getNbrPriDispLists( )
{
    return _displayTable.nbrPriDispLists;
}
UINT16 CtiAnsiTable31::getNbrSecDispListItems( )
{
    return _displayTable.nbrSecDispListItems;
}
UINT8  CtiAnsiTable31::getNbrSecDispLists( )
{
    return _displayTable.nbrSecDispLists;
}

bool CtiAnsiTable31::getOnTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.onTimeFlag;
}
bool CtiAnsiTable31::getOffTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.offTimeFlag;
}
bool CtiAnsiTable31::getHoldTimeFlag( )
{
    return (bool) _displayTable.displayCtrl.holdTimeFlag;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable31::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("On Time Flag")               << getOnTimeFlag();
    itemList.add("Off Time Flag")              << getOffTimeFlag();
    itemList.add("Hold Time Flag")             << getHoldTimeFlag();
    itemList.add("Nbr Display Sources")        << getNbrDispSources();
    itemList.add("Width Display Sources")      << getWidthDispSources();
    itemList.add("Nbr Pri Display List Items") << getNbrPriDispListItems();
    itemList.add("Nbr Pri Display Lists")      << getNbrPriDispLists();
    itemList.add("Nbr Sec Display List Items") << getNbrSecDispListItems();
    itemList.add("Nbr Sec Diplay Lists")       << getNbrSecDispLists();

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 31") <<
            endl <<"** Actual Display Dimension Table **"<<
            itemList
            );
}
