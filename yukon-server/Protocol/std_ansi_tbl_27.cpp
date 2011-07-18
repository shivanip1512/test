/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_27
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_27.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_two_seven.cpp,v $
      Revision 1.5  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.2.6.1  2005/12/12 19:50:39  jrichter
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
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_27.h"

using std::endl;
using std::string;
//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable27::CtiAnsiTable27() :
    _presentDemandSelect(NULL),
    _presentValueSelect(NULL),
    _nbrPresentDemands(0),
    _nbrPresentValues(0)
{
}

CtiAnsiTable27::CtiAnsiTable27( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues )
{
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;

    _presentDemandSelect = new unsigned char[_nbrPresentDemands];
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        memcpy(( void *)&_presentDemandSelect[i], dataBlob, sizeof( unsigned char ));
        dataBlob += sizeof( unsigned char);
    }
    _presentValueSelect = new unsigned char[_nbrPresentValues];
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        memcpy(( void *)&_presentValueSelect[i], dataBlob, sizeof( unsigned char ));
        dataBlob += sizeof( unsigned char);
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable27::~CtiAnsiTable27()
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

CtiAnsiTable27& CtiAnsiTable27::operator=(const CtiAnsiTable27& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned char* CtiAnsiTable27::getDemandSelect( )
{
    if (_presentDemandSelect != NULL)
    {
        return _presentDemandSelect;
    }
    else
        return NULL;
}

unsigned char* CtiAnsiTable27::getValueSelect(  )
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
void CtiAnsiTable27::printResult( const string& deviceName )
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
        dout << endl << "==================== "<<deviceName<<"  Std Table 27 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Present Register Select ** "<<endl;
        dout << "        Demand Src Indices: ";
    }
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(int)_presentDemandSelect[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<"         Value Src Indices: ";
    }
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(int)_presentValueSelect[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
    }
}











