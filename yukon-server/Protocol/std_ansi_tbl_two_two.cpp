
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/04/22 21:12:54 $
*
*    History: 
      $Log: std_ansi_tbl_two_two.cpp,v $
      Revision 1.4  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_two_two.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoTwo::CtiAnsiTableTwoTwo( BYTE *dataBlob, int num_sums, int num_demands, int num_coins )
{
   int index;

   _totalTableSize = 0;

   _summation_select = new unsigned char[num_sums];
   _totalTableSize += sizeof( unsigned char ) * num_sums;

   for( index = 0; index < num_sums; index++ )
   {
      memcpy( (void *)&_summation_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _demand_select = new unsigned char[num_demands];
   _totalTableSize += sizeof( unsigned char ) * num_demands;

   for( index = 0; index < num_demands; index++ )
   {
      memcpy( (void *)&_demand_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _demandSelectSize = sizeof( unsigned char ) * num_demands;

   _set = new unsigned char[( num_demands +7)/8];
   memcpy( _set, dataBlob, (( num_demands +7)/8));
   dataBlob += ((num_demands +7)/8);
   _totalTableSize += ((num_demands +7)/8);

   _coincident_select = new unsigned char[num_coins];
   _totalTableSize += sizeof( unsigned char ) * num_coins;

   for( index = 0; index < num_coins; index++ )
   {
      memcpy( (void *)&_coincident_select[index], dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totalTableSize += sizeof( unsigned char );
   }

   _coin_demand_assoc = new unsigned char[num_coins];
   _totalTableSize += sizeof( unsigned char ) * num_coins;

   for( index = 0; index < num_coins; index++ )
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
