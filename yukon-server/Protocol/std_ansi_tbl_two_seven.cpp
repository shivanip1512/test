


#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_seven
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_two_seven.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/01/25 18:33:51 $
*    History: 
      $Log: std_ansi_tbl_two_seven.cpp,v $
      Revision 1.1  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_two_seven.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoSeven::CtiAnsiTableTwoSeven()
{
}
CtiAnsiTableTwoSeven::CtiAnsiTableTwoSeven( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues )
{
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;

    _presentDemandSelect = new UINT8[_nbrPresentDemands];
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        memcpy(( void *)&_presentDemandSelect[i], dataBlob, sizeof( unsigned char ));
        dataBlob += sizeof( unsigned char);
    }
    _presentValueSelect = new UINT8[_nbrPresentValues];
    for (i = 0; i < _nbrPresentValues; i++)
    {
        memcpy(( void *)&_presentValueSelect[i], dataBlob, sizeof( unsigned char ));
        dataBlob += sizeof( unsigned char);
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoSeven::~CtiAnsiTableTwoSeven()
{
     if (_presentDemandSelect != NULL)
     {
         delete []_presentDemandSelect;
         _presentDemandSelect = NULL;
     }
     if (_presentValueSelect != NULL)
     {
         delete []_presentValueSelect;
         _presentValueSelect = NULL;
     }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoSeven& CtiAnsiTableTwoSeven::operator=(const CtiAnsiTableTwoSeven& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

UINT8* CtiAnsiTableTwoSeven::getDemandSelect( )
{
    if (_presentDemandSelect != NULL)
    {
        return _presentDemandSelect;
    }
    else
        return NULL;
}

UINT8* CtiAnsiTableTwoSeven::getValueSelect(  )
{
    if (_presentDemandSelect != NULL)
    {
        return _presentValueSelect;
    }
    else
        return NULL;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoSeven::printResult(  )
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
        dout << endl << "=======================  Std Table 27 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Present Register Select ** "<<endl;
        dout << "        Demand Src Indices: ";
    }
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_presentDemandSelect[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<"         Value Src Indices: ";
    }
    for (i = 0; i < _nbrPresentValues; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_presentValueSelect[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
    }
}











