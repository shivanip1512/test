#include "yukon.h"

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_three_two
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_three_two.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:44:47 $

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_three_two.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableThreeTwo::CtiAnsiTableThreeTwo()
{
}
CtiAnsiTableThreeTwo::CtiAnsiTableThreeTwo( BYTE *dataBlob, UINT16 nbrDispSources, UINT8 widthDispSources  )
{
    _nbrDispSources = nbrDispSources;
    _widthDispSources = widthDispSources;
    
    _displaySources = new DISP_SOURCE_DESC_RCD[_nbrDispSources];
    for (int i = 0; i < _nbrDispSources; i++)
    {
         _displaySources[i].displaySource = new UINT8[_widthDispSources];
         for (int j = 0; j < _widthDispSources; j++)
         {
             memcpy((void *)&_displaySources[i].displaySource[j], dataBlob, sizeof(UINT8));
             dataBlob += sizeof(UINT8);
         }
    }
    

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableThreeTwo::~CtiAnsiTableThreeTwo()
{
    if (_displaySources != NULL)
    {
        for (int i = 0; i < _nbrDispSources; i++)
        {
            if (_displaySources[i].displaySource != NULL)
            {
                delete  _displaySources[i].displaySource;
                _displaySources[i].displaySource = NULL;
            }
        }
        delete []_displaySources;
        _displaySources = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableThreeTwo& CtiAnsiTableThreeTwo::operator=(const CtiAnsiTableThreeTwo& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

UINT8 CtiAnsiTableThreeTwo::getDisplaySources(int sourceIndex, int widthIndex)
{
    if (_displaySources[sourceIndex].displaySource[widthIndex] != NULL)
    {
        return _displaySources[sourceIndex].displaySource[widthIndex];
    }
    else
        return -1;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableThreeTwo::printResult(  )
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
        dout << endl << "=======================  Std Table 32 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Display Source Table ** "<<endl;
    }
    for (int i = 0; i < _nbrDispSources; i++)
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "        Display Source["<<i<<"] : ";
        }
        for (int j = 0; j < _widthDispSources; j++)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << " "<<_displaySources[i].displaySource[j];
            }
        }
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
        }


    }
    

}











