#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define BCD                unsigned char

#pragma pack( push, 1)

   /*struct TIME_DATE_QUAL_BFLD
   {
      unsigned char     day_of_week:         3;
      bool              dst_flag:            1;
      bool              gmt_flag:            1;
      bool              tm_zn_applied_flag:  1;
      bool              dst_applied_flag:    1;
      bool              filler:              1;
   };

   struct LTIME_DATE
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
   */
   struct CLOCK_STATE_RCD
   {
      ULONG              clock_calendar;
      TIME_DATE_QUAL_BFLD     time_date_qual;
   };

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable52 : public CtiAnsiTableBase
{
protected:

   CLOCK_STATE_RCD      clock_table;

private:

    int _timefmt;

    ULONG _meterServerTimeDifference;

    bool meterTimeIsGMT() const;

public:

   CtiAnsiTable52( BYTE *dataBlob, int timefmat );

   virtual ~CtiAnsiTable52();

   CtiAnsiTable52& operator=(const CtiAnsiTable52& aRef);
   void printResult( const std::string& deviceName);

   CtiTime getClkCldrTime();
   CtiDate getClkCldrDate();
   int getClkCldrYear();
   int getClkCldrMon();
   int getClkCldrDay();
   int getClkCldrHour();
   int getClkCldrMin();
   int getClkCldrSec();
   bool isCalendarValid();

   ULONG getMeterServerTimeDifference();
   bool getTimeZoneAppliedFlag();
   ULONG adjustTimeZoneAndDST(ULONG adjustedTime);
   bool adjustTimeForDST();

};
