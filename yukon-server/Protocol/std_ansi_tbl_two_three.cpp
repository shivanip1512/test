
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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_two_three.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:54 $
*    History: 
      $Log: std_ansi_tbl_two_three.cpp,v $
      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_two_three.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoThree::CtiAnsiTableTwoThree( BYTE *dataBlob, int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag, int format1, int format2, int timefmat )
{
   int      index;
   int      count;
   int      track;
   BYTE     *ptr;
   int      bytes = 0;
   int      offset = 0;

   _totSize = 0;
   //don't forget to make the pointer point!
   //don't forget to use the reset when allocating mem

   //part 1
   if( reset_flag == true )
   {
      _nbr_demand_resets = new unsigned char;
      memcpy( _nbr_demand_resets, dataBlob + offset, sizeof( unsigned char ));
      offset += sizeof( unsigned char);
   }
   else
      _nbr_demand_resets = NULL;

   //part 2
   _tot_data_block.summations = new double[summations];

   for( index = 0; index < summations; index++ )
   {
      bytes = toDoubleParser( dataBlob + offset, _tot_data_block.summations[index], format1 );
      offset += bytes;
      _totSize += bytes;
   }

   _tot_data_block.demands = new DEMANDS_RCD[demands];
   _tot_data_block.coincidents = new COINCIDENTS_RCD[coinValues];

   //summations are not variable

   //demands record
   for( index = 0; index < demands; index++ )
   {
      if( time_flag == true )
      {
         _tot_data_block.demands[index].event_time = new ULONG[occur];

         for( count = 0; count < occur; count++ )
         {
            bytes = toUint32STime( dataBlob + offset, _tot_data_block.demands[index].event_time[count], timefmat );
            offset += bytes;
            _totSize += bytes;
         }
      }
      else
         _tot_data_block.demands[index].event_time = NULL;

      if( cum_demand_flag == true )
      {
         _tot_data_block.demands[index].cum_demand = new double;

         bytes = toDoubleParser( dataBlob + offset, *_tot_data_block.demands[index].cum_demand, format1 );
         offset += bytes;
         _totSize += bytes;
      }
      else
         _tot_data_block.demands[index].cum_demand = NULL;

      if( cum_cont_flag == true )
      {
         _tot_data_block.demands[index].cont_cum_demand = new double;

         bytes = toDoubleParser( dataBlob + offset, *_tot_data_block.demands[index].cont_cum_demand, format1 );
         offset += bytes;
         _totSize += bytes;
      }
      else
         _tot_data_block.demands[index].cont_cum_demand = NULL;

      _tot_data_block.demands[index].demand = new double[occur];

      for( count = 0; count < occur; count++ )
      {
         bytes = toDoubleParser( dataBlob + offset, _tot_data_block.demands[index].demand[count], format2 );
         offset += bytes;
         _totSize += bytes;
      }
   }

   for( index = 0; index < coinValues; index++ )
   {
      _tot_data_block.coincidents[index].coincident_values = new double[occur];

      for( count = 0; count < occur; count++ )
      {
         bytes = toDoubleParser( dataBlob + offset, _tot_data_block.coincidents[index].coincident_values[count], format2 );
         offset += bytes;
         _totSize += bytes;
      }
   }

/* we'll do the tier later

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
   _format1 = format1;
   _format2 = format2;
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
/*

*/
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

int CtiAnsiTableTwoThree::copyTotDataBlock( BYTE *ptr )
{
   memcpy( ptr, &_tot_data_block, _totSize );

   return _totSize;
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

