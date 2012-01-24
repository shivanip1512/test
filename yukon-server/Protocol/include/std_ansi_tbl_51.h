#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct TIME_FUNC_FLAG1_BFLD
{
   unsigned char     tou_self_read_flag        :1;
   unsigned char     season_self_read_flag     :1;
   unsigned char     season_demand_reset_flag  :1;
   unsigned char     season_chng_armed_flag    :1;
   unsigned char     sort_dates_flag           :1;
   unsigned char     anchor_date_flag          :1;
   unsigned char     filler                    :2;
};

struct TIME_FUNC_FLAG2_BFLD
{
   unsigned char     cap_dst_auto_flag         :1;
   unsigned char     separate_weekdays_flag    :1;
   unsigned char     separate_sum_demands_flag :1;
   unsigned char     sort_tier_switches_flag   :1;
   unsigned char     cap_tm_zn_offset_flag     :1;
   unsigned char     filler                    :3;
};

struct CALENDAR_BFLD
{
   unsigned char     nbr_seasons          :4;
   unsigned char     nbr_special_sched    :4;
};

struct TIME_TOU_RCD
{
   TIME_FUNC_FLAG1_BFLD  time_func_flag1;
   TIME_FUNC_FLAG2_BFLD  time_func_flag2;
   CALENDAR_BFLD         calendar_func;

   unsigned char         nbr_non_recurr_dates;
   unsigned char         nbr_recurr_dates;
   unsigned short        nbr_tier_switches;
   unsigned short        calendar_tbl_size;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable51 : public CtiAnsiTableBase
{
protected:

   TIME_TOU_RCD      _time_tou;

private:

public:

   CtiAnsiTable51( BYTE *dataBlob );

   virtual ~CtiAnsiTable51();

   CtiAnsiTable51& operator=(const CtiAnsiTable51& aRef);
   void printResult( const std::string& deviceName);

};
