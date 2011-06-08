/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_52
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_52.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_five_two.h,v $
      Revision 1.9  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.7  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_52_H__
#define __STD_ANSI_TBL_52_H__
#pragma warning( disable : 4786)

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

   CtiAnsiTable52(int timefmat  );
   CtiAnsiTable52( BYTE *dataBlob, int timefmat );

   virtual ~CtiAnsiTable52();

   CtiAnsiTable52& operator=(const CtiAnsiTable52& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );

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
#endif // #ifndef __STD_ANSI_TBL_52_H__
