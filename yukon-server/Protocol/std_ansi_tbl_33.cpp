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
         memcpy((void *)&_priDispListTable.priDispList[i], dataBlob, sizeof(DISP_LIST_DESC_RCD));
         dataBlob += sizeof(DISP_LIST_DESC_RCD);    //3 bytes
    }

    _priDispListTable.priDispSources = new UINT16[_nbrPriDispListItems];
    for (int i = 0; i < _nbrPriDispListItems; i++)
    {
        if (dataOrder == MSB)
        {
            reverseOrder(dataBlob, sizeof(UINT16));
        }
         memcpy((void *)&_priDispListTable.priDispSources[i], dataBlob, sizeof(UINT16));
         dataBlob += sizeof(UINT16);    //2 bytes
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
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 33 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Primary Display List Table ** "<<endl;
        dout << "        Display Source[ ] OnTime OffTime HoldTime DefaultList NbrListItems" <<endl;
    }
   /* for (int i = 0; i < _nbrPriDispLists; i++)
    {
        {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "          " << i << "    " 
                                     <<_priDispListTable.priDispList[i].dispScroll1.onTime << "    "     
                                     <<_priDispListTable.priDispList[i].dispScroll1.offTime << "    "    
                                     <<_priDispListTable.priDispList[i].dispScroll2.holdTime << "    "   
                                     <<_priDispListTable.priDispList[i].dispScroll2.defaultList << "    "
                                     <<_priDispListTable.priDispList[i].nbrListItems << "    " << endl;           
        }
        
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "        Primary Display Source [ ] " <<endl;
    }
    for (int i = 0; i < _nbrPriDispListItems; i++)
    {
        
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "          " <<  _priDispListTable.priDispSources[i] << endl;
    }*/




}











