#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_one
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_five_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableFiveOne::CtiAnsiTableFiveOne( )
{
}


CtiAnsiTableFiveOne::CtiAnsiTableFiveOne( BYTE *dataBlob )
{
    memcpy( (void *)&_time_tou, dataBlob, sizeof( TIME_TOU_RCD ));
    dataBlob +=  sizeof( TIME_TOU_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveOne::~CtiAnsiTableFiveOne()
{
   //delete clock_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveOne& CtiAnsiTableFiveOne::operator=(const CtiAnsiTableFiveOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveOne::generateResultPiece( BYTE **dataBlob )
{
    memcpy( *dataBlob, (void *)&_time_tou, sizeof( TIME_TOU_RCD ));
    *dataBlob +=  sizeof( TIME_TOU_RCD );

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveOne::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&_time_tou, *dataBlob, sizeof( TIME_TOU_RCD ));
    *dataBlob +=  sizeof( TIME_TOU_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveOne::printResult(  )
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
        dout << endl << "=======================  Std Table 51  ========================" << endl;
        dout << " ** Actual Time and TOU Table ** "<<endl;

        dout << "           tou_self_read_flag         "<<(bool)_time_tou.time_func_flag1.tou_self_read_flag<<endl;
        dout << "           season_self_read_flag      "<<(bool)_time_tou.time_func_flag1.season_self_read_flag<<endl;
        dout << "           season_demand_reset_flag   "<<(bool)_time_tou.time_func_flag1.season_demand_reset_flag<<endl;
        dout << "           season_chng_armed_flag     "<<(bool)_time_tou.time_func_flag1.season_chng_armed_flag<<endl;
        dout << "           sort_dates_flag            "<<(bool)_time_tou.time_func_flag1.sort_dates_flag<<endl;
        dout << "           anchor_date_flag           "<<(bool)_time_tou.time_func_flag1.anchor_date_flag<<endl;
        dout << "           cap_dst_auto_flag          "<<(bool)_time_tou.time_func_flag2.cap_dst_auto_flag<<endl;
        dout << "           separate_weekdays_flag     "<<(bool)_time_tou.time_func_flag2.separate_weekdays_flag<<endl;
        dout << "           separate_sum_demands_flag  "<<(bool)_time_tou.time_func_flag2.separate_sum_demands_flag<<endl;
        dout << "           sort_tier_switches_flag    "<<(bool)_time_tou.time_func_flag2.sort_tier_switches_flag<<endl;
        dout << "           cap_tm_zn_offset_flag      "<<(bool)_time_tou.time_func_flag2.cap_tm_zn_offset_flag<<endl;
        dout << "           nbr_seasons                "<<(int)_time_tou.calendar_func.nbr_seasons<<endl;
        dout << "           nbr_special_sched          "<<(int)_time_tou.calendar_func.nbr_special_sched<<endl;
        dout << "           nbr_non_recurr_dates       "<<(int)_time_tou.nbr_non_recurr_dates<<endl;
        dout << "           nbr_recurr_dates           "<<(int)_time_tou.nbr_recurr_dates<<endl;
        dout << "           nbr_tier_switches          "<<(int)_time_tou.nbr_tier_switches<<endl;
        dout << "           calendar_tbl_size          "<<(int)_time_tou.calendar_tbl_size<<endl;
    }
    
}


