
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_two
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_two_two.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/09/30 21:37:19 $
*
*    History: 
      $Log: std_ansi_tbl_two_two.cpp,v $
      Revision 1.5  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_two_two.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoTwo::CtiAnsiTableTwoTwo( int num_sums, int num_demands, int num_coins )
{
    _numSums = num_sums;
    _numDemands = num_demands;
    _numCoins = num_coins;

}
CtiAnsiTableTwoTwo::CtiAnsiTableTwoTwo( BYTE *dataBlob, int num_sums, int num_demands, int num_coins )
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

CtiAnsiTableTwoTwo::~CtiAnsiTableTwoTwo()
{
   delete []_summation_select;
   delete []_demand_select;
   delete []_set;
   delete []_coincident_select;
   delete []_coin_demand_assoc;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoTwo& CtiAnsiTableTwoTwo::operator=(const CtiAnsiTableTwoTwo& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoTwo::copyDemandSelect( BYTE *ptr )
{
   memcpy( ptr, _demand_select, _demandSelectSize );
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoTwo::getDemandSelectSize( void )
{
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned char* CtiAnsiTableTwoTwo::getDemandSelect( void )
{
   return _demand_select;
}

//=========================================================================================================================================
//=========================================================================================================================================
unsigned char* CtiAnsiTableTwoTwo::getSummationSelect( void )
{
   return _summation_select;
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoTwo::getTotalTableSize( void )
{
   return _totalTableSize;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoTwo::generateResultPiece( BYTE **dataBlob )
{
    
    int index;

   for( index = 0; index < _numSums; index++ )
   {
      memcpy( *dataBlob, (void *)&_summation_select[index], sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   for( index = 0; index < _numDemands; index++ )
   {
      memcpy( *dataBlob, (void *)&_demand_select[index], sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   memcpy( *dataBlob, _set, (( _numDemands +7)/8));
   *dataBlob += ((_numDemands +7)/8);

   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( *dataBlob, (void *)&_coincident_select[index], sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
  
   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( *dataBlob, (void *)&_coin_demand_assoc[index], sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoTwo::decodeResultPiece( BYTE **dataBlob )
{
    int index;

    _summation_select = new unsigned char[_numSums];

   for( index = 0; index < _numSums; index++ )
   {
      memcpy( (void *)&_summation_select[index], *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   _demand_select = new unsigned char[_numDemands];

   for( index = 0; index < _numDemands; index++ )
   {
      memcpy( (void *)&_demand_select[index], *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   _demandSelectSize = sizeof( unsigned char ) * _numDemands;

   _set = new unsigned char[( _numDemands +7)/8];
   memcpy( _set, *dataBlob, (( _numDemands +7)/8));
   *dataBlob += ((_numDemands +7)/8);

   _coincident_select = new unsigned char[_numCoins];

   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( (void *)&_coincident_select[index], *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   _coin_demand_assoc = new unsigned char[_numCoins];

   for( index = 0; index < _numCoins; index++ )
   {
      memcpy( (void *)&_coin_demand_assoc[index], *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoTwo::printResult(  )
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
        dout << endl << "=======================  Std Table 22 ========================" << endl;
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











