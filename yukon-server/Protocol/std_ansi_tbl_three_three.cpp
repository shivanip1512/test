#include "yukon.h"

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_three_three
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_three_three.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:44:46 $

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_three_three.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableThreeThree::CtiAnsiTableThreeThree()
{
}
CtiAnsiTableThreeThree::CtiAnsiTableThreeThree( BYTE *dataBlob, UINT8 nbrPriDispLists, UINT16 nbrPriDispListItems  )
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
    for (i = 0; i < _nbrPriDispListItems; i++)
    {
         memcpy((void *)&_priDispListTable.priDispSources[i], dataBlob, sizeof(UINT16));
         dataBlob += sizeof(UINT16);    //2 bytes
    }
    

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableThreeThree::~CtiAnsiTableThreeThree()
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

CtiAnsiTableThreeThree& CtiAnsiTableThreeThree::operator=(const CtiAnsiTableThreeThree& aRef)
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
void CtiAnsiTableThreeThree::printResult(  )
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
        dout << endl << "=======================  Std Table 33 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Primary Display List Table ** "<<endl;
        dout << "        Display Source[ ] : ";
    }
    for (int i = 0; i < _nbrPriDispLists; i++)
    {
       
    }
    

}











