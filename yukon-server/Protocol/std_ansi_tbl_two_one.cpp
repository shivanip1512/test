
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/01/25 18:33:51 $
*    History: 
      $Log: std_ansi_tbl_two_one.cpp,v $
      Revision 1.5  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_two_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoOne::CtiAnsiTableTwoOne()
{
}
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

int CtiAnsiTableTwoOne::getNbrPresentDemands(void)
{
    return _nbr_present_demands;
}

int CtiAnsiTableTwoOne::getNbrPresentValues(void)
{
    return _nbr_present_values;
}

bool CtiAnsiTableTwoOne::getTimeRemainingFlag(void)
{
    if( _reg_func1_flags.time_remaining_flag  & 0x01 )
      return true;
   else
      return false;
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



//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoOne::generateResultPiece( BYTE **dataBlob )
{
    memcpy(*dataBlob, ( void *)&_reg_func1_flags, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_reg_func2_flags, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_self_reads, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_summations, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_demands, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_coin_values, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_occur, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_tiers, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_present_demands, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(*dataBlob, ( void *)&_nbr_present_values, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoOne::decodeResultPiece( BYTE **dataBlob )
{
    memcpy(( void *)&_reg_func1_flags, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_reg_func2_flags, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_self_reads, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_summations, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_demands, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_coin_values, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_occur, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_tiers, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_present_demands, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);

    memcpy(( void *)&_nbr_present_values, *dataBlob, sizeof( unsigned char ));
    *dataBlob += sizeof( unsigned char);
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoOne::printResult(  )
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
        dout << endl << "=======================  Std Table 21 ========================" << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " ** Reg_Func1_Flags ** "<<endl;
        dout << "       Season_Info_Field_Flag "<<(bool) _reg_func1_flags.season_info_field_flag <<endl;
        dout << "       Date_Time_Field_Flag   "<<(bool) _reg_func1_flags.date_time_field_flag<<endl;
        dout << "       Demand_Reset_Ctr_Flag  "<<(bool) _reg_func1_flags.demand_reset_ctr_flag<<endl;
        dout << "       Demand_Reset_Lock_Flag "<<(bool) _reg_func1_flags.demand_reset_lock_flag<<endl;
        dout << "       Cum_Demand_Flag        "<<(bool) _reg_func1_flags.cum_demand_flag<<endl;
        dout << "       Cont_Cum_Demand_Flag   "<<(bool) _reg_func1_flags.cont_cum_demand_flag<<endl;
        dout << "       Time_Remaining_Flag    "<<(bool) _reg_func1_flags.time_remaining_flag<<endl;
        dout << " ** Reg_Func2_Flags ** "<<endl;
        dout << "       Self_Read_Inhibit_Overflow_Flag "<<(bool) _reg_func2_flags.self_read_inhibit_overflow_flag<<endl;
        dout << "       Self_Read_Seq_Nbr_Flag "<<(bool) _reg_func2_flags.self_read_seq_nbr_flag<<endl;
        dout << "       Daily_Self_Read_Flag   "<<(bool) _reg_func2_flags.daily_self_read_flag<<endl;
        dout << "       Weekly_Self_Read_Flag  "<<(bool) _reg_func2_flags.weekly_self_read_flag<<endl;
        dout << "       Self_Read_Demand_Reset "<<(int) _reg_func2_flags.self_read_demand_reset<<endl;
        dout << " ***** "<<endl;
        dout << "    Nbr_Self_Reads      "<<(int) _nbr_self_reads<<endl;
        dout << "    Nbr_Summations      "<<(int) _nbr_summations<<endl;
        dout << "    Nbr_Demands         "<<(int) _nbr_demands<<endl;
        dout << "    Nbr_Coin_Values     "<<(int) _nbr_coin_values<<endl;
        dout << "    Nbr_Occur           "<<(int) _occur<<endl;
        dout << "    Nbr_Tiers           "<<(int) _tiers<<endl;
        dout << "    Nbr_Present_Demands "<<(int) _nbr_present_demands<<endl;
        dout << "    Nbr_Present_Values  "<<(int) _nbr_present_values<<endl;
    }
}











