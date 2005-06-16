
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_FIVE_TWO_H__
#define __STD_ANSI_TBL_FIVE_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_two
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_five_two.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/06/16 19:18:00 $
*    History: 
      $Log: std_ansi_tbl_five_two.h,v $
      Revision 1.5  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
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

class IM_EX_PROT CtiAnsiTableFiveTwo : public CtiAnsiTableBase
{
protected:

   CLOCK_STATE_RCD      clock_table;

private:

    int _timefmt;

    ULONG _meterServerTimeDifference;
public:

   CtiAnsiTableFiveTwo(int timefmat  );
   CtiAnsiTableFiveTwo( BYTE *dataBlob, int timefmat );
   
   virtual ~CtiAnsiTableFiveTwo();

   CtiAnsiTableFiveTwo& operator=(const CtiAnsiTableFiveTwo& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

   int getClkCldrYear();
   int getClkCldrMon();
   int getClkCldrDay();
   int getClkCldrHour();
   int getClkCldrMin();
   int getClkCldrSec();

   ULONG getMeterServerTimeDifference();
   bool adjustTimeForDST();

};
#endif // #ifndef __STD_ANSI_TBL_FIVE_TWO_H__
