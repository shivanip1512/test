#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define BCD                unsigned char

#pragma pack( push, 1)

struct STATUS_BFLD
{
   union seperate_sum
   {
      struct first
      {
         unsigned char curr_summ_tier      :3;
         unsigned char curr_demand_tier    :3;
      }s1;

      struct second
      {
         unsigned char curr_tier           :3;
         unsigned char filler              :3;
      }s2;
   }u;

   unsigned char  tier_drive              :2;
   unsigned char  special_schd_active     :4;
   unsigned char  season                  :4;
};

/*struct LTIME_DATE
{
   union// CASES
   {
      struct CASE1
      {
         BCD      year;
         BCD      month;
         BCD      day;
         BCD      hour;
         BCD      minute;
         BCD      second;
      }c1;

      struct CASE2
      {
         unsigned char  year;
         unsigned char  month;
         unsigned char  day;
         unsigned char  hour;
         unsigned char  minute;
         unsigned char  second;
      }c2;

      struct CASE3
      {
         long           u_time;
         unsigned char  second;
      }c3;

      struct CASE4
      {
         long           u_time_sec;
      }c4;

   }cases;
};

struct TIME_DATE_QUAL_BFLD
{
   unsigned char        day_of_week          :3;
   unsigned char        dst_flag             :1;
   unsigned char        gmt_flag             :1;
   unsigned char        tm_zn_applied_flag   :1;
   unsigned char        dst_applied_flag     :1;
   unsigned char        filler               :1;
};
*/
struct CLOCK_55_STATE_RCD
{
   LTIME_DATE           clock_calendar;
   TIME_DATE_QUAL_BFLD  time_date;
   STATUS_BFLD          status;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable055 : public CtiAnsiTableBase
{
protected:

   CLOCK_55_STATE_RCD    _clockStateTbl;

private:

public:

   CtiAnsiTable055( BYTE *dataBlob );

   virtual ~CtiAnsiTable055();

   CtiAnsiTable055& operator=(const CtiAnsiTable055& aRef);
   void printResult( const std::string& deviceName);

};
