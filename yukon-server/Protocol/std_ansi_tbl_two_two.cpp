/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_two
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:44 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "std_ansi_tbl_two_two.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoTwo::CtiAnsiTableTwoTwo( BYTE *dataBlob, int num_sums, int num_demands, int num_coins )
{
   int index;

   _summation_select = new unsigned char[num_sums];

   for( index = 0; index < num_sums; index++ )
   {
      memcpy( _summation_select, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }

   _demand_select = new unsigned char[num_demands];

   for( index = 0; index < num_demands; index++ )
   {
      memcpy( _demand_select, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }

   _demandSelectSize = sizeof( unsigned char ) * num_demands;

   _set = new unsigned char[( num_demands +7)/8];
   memcpy( _set, dataBlob, (( num_demands +7)/8));
   dataBlob += ((num_demands +7)/8);

   _coincident_select = new unsigned char[num_coins];

   for( index = 0; index < num_coins; index++ )
   {
      memcpy( _coincident_select, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }

   _coin_demand_assoc = new unsigned char[num_coins];

   for( index = 0; index < num_coins; index++ )
   {
      memcpy( _coin_demand_assoc, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
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
