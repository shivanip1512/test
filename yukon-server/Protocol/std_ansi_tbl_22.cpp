/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_22
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_22.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*
*    History:
      $Log: std_ansi_tbl_two_two.cpp,v $
      Revision 1.10  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/12/12 20:34:30  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.7  2005/09/29 21:18:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.6  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_22.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22::CtiAnsiTable22( BYTE *dataBlob, int num_sums, int num_demands, int num_coins )
{
    int index;

    _numSums = num_sums;
    _numDemands = num_demands;
    _numCoins = num_coins;
    _totalTableSize = 0;

    _summation_select = new unsigned char[_numSums];
    _totalTableSize += sizeof( unsigned char ) * _numSums;

   for( index = 0; index < _numSums; index++ )
   {
      memcpy( (void *)&_summation_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _demand_select = new unsigned char[_numDemands];
   _totalTableSize += sizeof( unsigned char ) * _numDemands;

   for( index = 0; index < _numDemands; index++ )
   {
      memcpy( (void *)&_demand_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _demandSelectSize = sizeof( unsigned char ) * _numDemands;

   _set = new unsigned char[( _numDemands +7)/8];
   memcpy( _set, dataBlob, (( _numDemands +7)/8));
   dataBlob += ((_numDemands +7)/8);
   _totalTableSize += ((_numDemands +7)/8);

   _coincident_select = new unsigned char[_numCoins];
   _totalTableSize += sizeof( unsigned char ) * _numCoins;

   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( (void *)&_coincident_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _coin_demand_assoc = new unsigned char[_numCoins];
   _totalTableSize += sizeof( unsigned char ) * _numCoins;

   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( (void *)&_coin_demand_assoc[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22::~CtiAnsiTable22()
{
    if (_summation_select != NULL)
    {
        delete []_summation_select;
        _summation_select = NULL;
    }
    if (_demand_select != NULL)
    {
        delete []_demand_select;
        _demand_select = NULL;
    }
    if (_set != NULL)
    {
        delete []_set;
        _set = NULL;
    }
    if (_coincident_select != NULL)
    {
        delete []_coincident_select;
        _coincident_select = NULL;
    }
    if (_coin_demand_assoc)
    {
        delete []_coin_demand_assoc;
        _coin_demand_assoc = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22& CtiAnsiTable22::operator=(const CtiAnsiTable22& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::copyDemandSelect( BYTE *ptr )
{
   memcpy( ptr, _demand_select, _demandSelectSize );
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::getDemandSelectSize( void )
{
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned char* CtiAnsiTable22::getDemandSelect( void )
{
   return _demand_select;
}

//=========================================================================================================================================
//=========================================================================================================================================
unsigned char* CtiAnsiTable22::getSummationSelect( void )
{
   return _summation_select;
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::getTotalTableSize( void )
{
   return _totalTableSize;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable22::printResult( const string& deviceName )
{
    int index;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "==================== "<<deviceName<<"  Std Table 22 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Summation_Select ** "<<endl;
    }

    for( index = 0; index < _numSums; index++ )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<< (int)_summation_select[index];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<" ** Demand_Select ** "<<endl;
    }
    for( index = 0; index < _numDemands; index++ )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<< (int)_demand_select[index];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<" ** Min or Max Flags ** "<<endl;
    }
    for( index = 0; index < ((_numDemands + 7) / 8); index++ )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<< (int)_set[index];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<" ** Coincident_Select ** "<<endl;
    }
    for( index = 0; index < _numCoins; index++ )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<< (int)_coincident_select[index];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl<<" ** Coin_Demand_Assoc ** "<<endl;
    }
    for( index = 0; index < _numCoins; index++ )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<< (int)_coin_demand_assoc[index];
    }

}











