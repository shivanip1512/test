#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_eight
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_two_eight.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*    History: 
      $Log: std_ansi_tbl_two_eight.cpp,v $
      Revision 1.2  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

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
#include "std_ansi_tbl_two_eight.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoEight::CtiAnsiTableTwoEight()
{
}
CtiAnsiTableTwoEight::CtiAnsiTableTwoEight( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues,
                                            bool timeRemainingFlag, int format1, int format2, int timefmt )
{
    int bytes, offset;
    offset = 0;
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;
    _timeRemainingFlag = timeRemainingFlag;
    _format1 = format1;
    _format2 = format2;
    _timefmt = timefmt;

    _presentDemand = new PRESENT_DEMAND_RCD[_nbrPresentDemands];
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        if (_timeRemainingFlag)
        {
             bytes = toTime( dataBlob, _presentDemand[i].timeRemaining, _timefmt );
            dataBlob += bytes;
            offset += bytes;
        }
        else
            _presentDemand[i].timeRemaining = 0;
        bytes = toDoubleParser( dataBlob, _presentDemand[i].demandValue, _format2 );
        dataBlob += bytes;
        offset += bytes;
        
    }
    _presentValue = new double[_nbrPresentValues];
    for (i = 0; i < _nbrPresentValues; i++)
    {
        bytes = toDoubleParser( dataBlob, _presentValue[i], _format1 );
        dataBlob += bytes;
        offset += bytes;
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoEight::~CtiAnsiTableTwoEight()
{
    if (_presentDemand != NULL)
    {
        delete []_presentDemand;
        _presentDemand = NULL;
    }
    if (_presentValue != NULL)
    {
        delete []_presentValue;
        _presentValue = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoEight& CtiAnsiTableTwoEight::operator=(const CtiAnsiTableTwoEight& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTableTwoEight::getPresentDemand(int index)
{
    if (_presentDemand != NULL)
    {
        return (double )_presentDemand[index].demandValue;
    }
    else
        return -1;
}

double CtiAnsiTableTwoEight::getPresentValue(int index)
{
    if (_presentDemand != NULL)
    {
        return (double )_presentValue[index];
    }
    else
        return -1;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoEight::printResult(  )
{
    int integer;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 28 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Present Register Data ** "<<endl;
        dout << "        Demand Data: ";
    }
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_presentDemand[i].demandValue;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<"         Value Data: ";
    }
    for (i = 0; i < _nbrPresentValues; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_presentValue[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
    }

}











