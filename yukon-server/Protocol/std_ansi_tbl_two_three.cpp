
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_three
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:36:21 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_two_three.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoThree::CtiAnsiTableTwoThree( BYTE *dataBlob, int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag )
{
   int   index;
   int   count;
   int   track;
   BYTE  *ptr;

   _totSize = 0;
   //don't forget to make the pointer point!
   //don't forget to use the reset when allocating mem

   //part 1
   if( reset_flag == true )
   {
      _nbr_demand_resets = new unsigned char;
      memcpy( _nbr_demand_resets, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
      _totSize += sizeof( unsigned char );
   }
   else
      _nbr_demand_resets = NULL;

   //part 2
   _tot_data_block.summations = new double[summations];

   for( index = 0; index < summations; index++ )
   {
      memcpy( ( void*)&(_tot_data_block.summations[index] ), dataBlob, sizeof( double ));
      dataBlob += sizeof( double );
      _totSize += sizeof( double );
   }

   _tot_data_block.demands = new DEMANDS_RCD[demands];
   _tot_data_block.coincidents = new COINCIDENTS_RCD[coinValues];

   //summations are not variable

   //demands record
   for( index = 0; index < demands; index++ )
   {
      if( time_flag == true )
      {
         _tot_data_block.demands[index].event_time = new STIME_DATE[occur];

         for( count = 0; count < occur; count++ )
         {
            memcpy( (void *)&(_tot_data_block.demands[index].event_time[count] ), dataBlob, sizeof( STIME_DATE ));
            dataBlob += sizeof( STIME_DATE );
            _totSize += sizeof( STIME_DATE );
         }
      }
      else
         _tot_data_block.demands[index].event_time = NULL;

      if( cum_demand_flag == true )
      {
         _tot_data_block.demands[index].cum_demand = new double;
         memcpy(  _tot_data_block.demands[index].cum_demand, dataBlob, sizeof( double ));
         dataBlob += sizeof( double );
         _totSize += sizeof( double );
      }
      else
         _tot_data_block.demands[index].cum_demand = NULL;

      if( cum_cont_flag == true )
      {
         _tot_data_block.demands[index].cont_cum_demand = new double;
         memcpy( _tot_data_block.demands[index].cont_cum_demand, dataBlob, sizeof( double ));
         dataBlob += sizeof( double );
         _totSize += sizeof( double );
      }
      else
         _tot_data_block.demands[index].cont_cum_demand = NULL;

      _tot_data_block.demands[index].demand = new double[occur];

      for( count = 0; count < occur; count++ )
      {
         memcpy( (void *)&(_tot_data_block.demands[index].demand[count]), dataBlob, sizeof( double ));
         dataBlob += sizeof( double );
         _totSize += sizeof( double );
      }
   }

   for( index = 0; index < coinValues; index++ )
   {
      _tot_data_block.coincidents[index].coincident_values = new double[occur];

      for( count = 0; count < occur; count++ )
      {
         memcpy( (void *)&(_tot_data_block.coincidents[index].coincident_values[count]), dataBlob, sizeof( double ));
         dataBlob += sizeof( double );
         _totSize += sizeof( double );
      }
   }

/* we'll do the tier later
   //part 3
   _tier_data_block = new DATA_BLK_RCD[tier];


   for( index = 0; index < tier; index++ )
   {
      _tier_data_block[index].summations = new double[summations];

      for( count = 0; count < summations; count++ )
      {
         memcpy(_tier_data_block[index].summations[count], dataBlob, sizeof( double ));
         dataBlob += sizeof( double );
      }


      _tier_data_block[index].demands = new DEMANDS_RCD[demands];

      for( count = 0; count < demands; count++ )
      {
         _tier_data_block[index].demands[count].event_time = new new STIME_DATE[occur];

         for( track = 0; track < occur; track++ )
         {
            memcpy( _tier_data_block[index].demands[count].event_time[track], dataBlob, sizeof( STIME_DATE ));
            dataBlob += sizeof( STIME_DATE );
         }

      }
      _tier_data_block[index].coincidents = new COINCIDENTS_RCD[coinValues];
   }

*/
   //keep our numbers for later
   _ocNums = occur;
   _sumNums = summations;
   _demandNums = demands;
   _coinNums = coinValues;
   _tierNums = tier;
   _reset = reset_flag;
   _time = time_flag;
   _cumd = cum_demand_flag;
   _cumcont = cum_cont_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoThree::~CtiAnsiTableTwoThree()
{
   int index;
   int count;

   if( getReset() == true )
      delete _nbr_demand_resets;

   //part 2

   delete _tot_data_block.summations;

   //demands record
   for( index = 0; index < getDemands(); index++ )
   {
      if( getTime() == true )
         delete _tot_data_block.demands[index].event_time;

      if( getCumd() == true )
         delete _tot_data_block.demands[index].cum_demand;

      if( getCumcont() == true )
         delete _tot_data_block.demands[index].cont_cum_demand;

      delete _tot_data_block.demands[index].demand;

   }
   delete _tot_data_block.demands;


   //part 3

   for( index = 0; index < getTier(); index++ )
   {
      delete _tier_data_block[index].summations;

      //demands record
      for( count = 0; count < getDemands(); count++ )
      {
         if( getTime() == true )
            delete _tier_data_block[index].demands[count].event_time;

         if( getCumd() == true )
            delete _tier_data_block[index].demands[count].cum_demand;

         if( getCumcont() == true )
            delete _tier_data_block[index].demands[count].cont_cum_demand;

         delete _tier_data_block[index].demands[count].demand;
      }
      delete _tier_data_block[index].demands;

      for( count = 0; count < getCoins(); index++)
      {
         delete _tier_data_block[index].coincidents[index].coincident_values;
      }
      delete _tier_data_block[index].coincidents;
   }

   delete _tot_data_block.coincidents;

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoThree& CtiAnsiTableTwoThree::operator=(const CtiAnsiTableTwoThree& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

DATA_BLK_RCD CtiAnsiTableTwoThree::getTotDataBlock( void )
{
   return _tot_data_block;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getOccurs( void )
{
   return _ocNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getSums( void )
{
   return _sumNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getDemands( void )
{
   return _demandNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getCoins( void )
{
   return _coinNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getTier( void )
{
   return _tierNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getReset( void )
{
   return _reset;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getTime( void )
{
   return _time;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getCumd( void )
{
   return _cumd;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getCumcont( void )
{
   return _cumcont;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getTotSize( void )
{
   return _totSize;
}

