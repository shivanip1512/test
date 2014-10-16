/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_33
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_33.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:31 $

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_33.h"


using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable33::CtiAnsiTable33( BYTE *dataBlob, UINT8 nbrPriDispLists, UINT16 nbrPriDispListItems, DataOrder dataOrder )
{
    _nbrPriDispLists = nbrPriDispLists;
    _nbrPriDispListItems = nbrPriDispListItems;

    _priDispListTable.priDispList = new DISP_LIST_DESC_RCD[_nbrPriDispLists];
    for (int i = 0; i < _nbrPriDispLists; i++)
    {
         dataBlob += toAnsiIntParser(dataBlob, &_priDispListTable.priDispList[i], sizeof(DISP_LIST_DESC_RCD), dataOrder); //3 bytes
    }

    _priDispListTable.priDispSources = new UINT16[_nbrPriDispListItems];
    for (int i = 0; i < _nbrPriDispListItems; i++)
    {
        dataBlob += toAnsiIntParser(dataBlob, &_priDispListTable.priDispSources[i], sizeof(UINT16), dataOrder);//2 bytes
    }


}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable33::~CtiAnsiTable33()
{
    if (_priDispListTable.priDispList != NULL)
    {
        delete []_priDispListTable.priDispList;
        _priDispListTable.priDispList = NULL;
    }
    if (_priDispListTable.priDispSources != NULL)
    {
        delete []_priDispListTable.priDispSources;
        _priDispListTable.priDispSources = NULL;
    }
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable33& CtiAnsiTable33::operator=(const CtiAnsiTable33& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable33::printResult( const string& deviceName )
{
    Cti::FormattedTable table;
    table.setCell(0, 0) << "Display Source[ ]";
    table.setCell(0, 1) << "OnTime";
    table.setCell(0, 2) << "OffTime";
    table.setCell(0, 3) << "HoldTime";
    table.setCell(0, 4) << "DefaultList";
    table.setCell(0, 5) << "NbrListItems";

    for( int index = 0; index < _nbrPriDispLists; index++ )
    {
        const unsigned row = index + 1;
        table.setCell(row, 0) << index;
        table.setCell(row, 1) << _priDispListTable.priDispList[index].dispScroll1.onTime;
        table.setCell(row, 2) << _priDispListTable.priDispList[index].dispScroll1.offTime;
        table.setCell(row, 3) << _priDispListTable.priDispList[index].dispScroll2.holdTime;
        table.setCell(row, 4) << _priDispListTable.priDispList[index].dispScroll2.defaultList;
        table.setCell(row, 5) << _priDispListTable.priDispList[index].nbrListItems;
    }

    Cti::FormattedTable itemsTable;
    itemsTable.setCell(0, 0) << "Primary Display Source[ ]";

    for( int index = 0; index < _nbrPriDispListItems; index++ )
    {
        const unsigned row = index + 1;
        itemsTable.setCell(row, 0) << _priDispListTable.priDispSources[index];
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 33") <<
            endl <<"** Primary Display List Table **"<<
            table <<
            endl <<"** Primary Display Items **"<<
            itemsTable
            );
}
