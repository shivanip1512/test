
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_one
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_two_one.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:54 $
*    History: 
      $Log: std_ansi_tbl_two_one.cpp,v $
      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_two_one.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoOne::CtiAnsiTableTwoOne( BYTE *dataBlob )
{
   memcpy(( void *)&_reg_func1_flags, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_reg_func2_flags, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_self_reads, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_summations, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_demands, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_coin_values, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_occur, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_tiers, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_present_demands, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);

   memcpy(( void *)&_nbr_present_values, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char);
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoOne::~CtiAnsiTableTwoOne()
{

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoOne& CtiAnsiTableTwoOne::operator=(const CtiAnsiTableTwoOne& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTableTwoOne::getNumberSummations( void )
{
   return _nbr_summations;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTableTwoOne::getNumberDemands( void )
{
   return _nbr_demands;
}

//=========================================================================================================================================
//used in table 22
//=========================================================================================================================================

int CtiAnsiTableTwoOne::getCoinValues( void )
{
   return _nbr_coin_values;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoOne::getOccur( void )
{
   return _occur;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoOne::getTiers( void )
{
   return _tiers;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoOne::getDemandResetCtrFlag( void )
{
   if( _reg_func1_flags.demand_reset_ctr_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoOne::getTimeDateFieldFlag( void )
{
   if( _reg_func1_flags.date_time_field_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoOne::getCumDemandFlag( void )
{
   if( _reg_func1_flags.cum_demand_flag & 0x01 )
      return true;
   else
      return false;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoOne::getContCumDemandFlag( void )
{
   if( _reg_func1_flags.cont_cum_demand_flag & 0x01 )
      return true;
   else
      return false;
}













