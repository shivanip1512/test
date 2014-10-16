/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_51
*
* Date:   05/21/2004
*
* Author: Julie Richter
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_51.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable51::CtiAnsiTable51( BYTE *dataBlob )
{
    dataBlob += toAnsiIntParser(dataBlob, &_time_tou, sizeof( TIME_TOU_RCD ));
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable51::~CtiAnsiTable51()
{
   //delete clock_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable51& CtiAnsiTable51::operator=(const CtiAnsiTable51& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable51::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("tou_self_read_flag")        << (bool)_time_tou.time_func_flag1.tou_self_read_flag;
    itemList.add("season_self_read_flag")     << (bool)_time_tou.time_func_flag1.season_self_read_flag;
    itemList.add("season_demand_reset_flag")  << (bool)_time_tou.time_func_flag1.season_demand_reset_flag;
    itemList.add("season_chng_armed_flag")    << (bool)_time_tou.time_func_flag1.season_chng_armed_flag;
    itemList.add("sort_dates_flag")           << (bool)_time_tou.time_func_flag1.sort_dates_flag;
    itemList.add("anchor_date_flag")          << (bool)_time_tou.time_func_flag1.anchor_date_flag;
    itemList.add("cap_dst_auto_flag")         << (bool)_time_tou.time_func_flag2.cap_dst_auto_flag;
    itemList.add("separate_weekdays_flag")    << (bool)_time_tou.time_func_flag2.separate_weekdays_flag;
    itemList.add("separate_sum_demands_flag") << (bool)_time_tou.time_func_flag2.separate_sum_demands_flag;
    itemList.add("sort_tier_switches_flag")   << (bool)_time_tou.time_func_flag2.sort_tier_switches_flag;
    itemList.add("cap_tm_zn_offset_flag")     << (bool)_time_tou.time_func_flag2.cap_tm_zn_offset_flag;
    itemList.add("nbr_seasons")               <<       _time_tou.calendar_func.nbr_seasons;
    itemList.add("nbr_special_sched")         <<       _time_tou.calendar_func.nbr_special_sched;
    itemList.add("nbr_non_recurr_dates")      <<       _time_tou.nbr_non_recurr_dates;
    itemList.add("nbr_recurr_dates")          <<       _time_tou.nbr_recurr_dates;
    itemList.add("nbr_tier_switches")         <<       _time_tou.nbr_tier_switches;
    itemList.add("calendar_tbl_size")         <<       _time_tou.calendar_tbl_size;

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 51") <<
            endl <<"** Actual Time and TOU Table **"<<
            itemList
            );

}


