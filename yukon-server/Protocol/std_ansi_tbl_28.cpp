/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_28
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_28.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_two_eight.cpp,v $
      Revision 1.5  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

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
#include "yukon.h"

#include "logger.h"
#include "std_ansi_tbl_28.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable28::CtiAnsiTable28() :
    _presentDemand(NULL),
    _presentValue(NULL),
     _nbrPresentDemands(0),
    _nbrPresentValues(0),
    _timeRemainingFlag(false),
    _format1(0),
    _format2(0),
    _timefmt(0),
    _lsbDataOrder(true)
{
}

CtiAnsiTable28::CtiAnsiTable28( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues,
                                bool timeRemainingFlag, int format1, int format2, int timefmt, bool lsbDataOrder )
{
    int bytes, offset;
    offset = 0;
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;
    _timeRemainingFlag = timeRemainingFlag;
    _format1 = format1;
    _format2 = format2;
    _timefmt = timefmt;
    _lsbDataOrder = lsbDataOrder;

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
        bytes = toDoubleParser( dataBlob, _presentDemand[i].demandValue, _format2, _lsbDataOrder );
        dataBlob += bytes;
        offset += bytes;

    }
    _presentValue = new double[_nbrPresentValues];
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        bytes = toDoubleParser( dataBlob, _presentValue[i], _format1, _lsbDataOrder );
        dataBlob += bytes;
        offset += bytes;
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable28::~CtiAnsiTable28()
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

CtiAnsiTable28& CtiAnsiTable28::operator=(const CtiAnsiTable28& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable28::getPresentDemand(int index)
{
    if (_presentDemand != NULL)
    {
        return (double )_presentDemand[index].demandValue;
    }
    else
        return -1;
}

double CtiAnsiTable28::getPresentValue(int index)
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
void CtiAnsiTable28::printResult( const string& deviceName )
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
        dout << endl << "==================== "<<deviceName<<" Std Table 28 ========================" << endl;
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
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_presentValue[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
    }

}











