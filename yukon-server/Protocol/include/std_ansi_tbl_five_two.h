
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
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:41:34 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

#define BCD                unsigned char

#pragma pack( push, 1)

   struct TIME_DATE_QUAL_BFLD
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
         };

         struct CASE2
         {
            unsigned char  year;
            unsigned char  month;
            unsigned char  day;
            unsigned char  hour;
            unsigned char  minute;
            unsigned char  second;
         };

         struct CASE3
         {
            long           u_time;
            unsigned char  second;
         };

         struct CASE4
         {
            long           u_time_sec;
         };

      }cases;
   };

   struct CLOCK_STATE_RCD
   {
      LTIME_DATE              clock_calendar;
      TIME_DATE_QUAL_BFLD     time_date_qual;
   };

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableFiveTwo
{
protected:

   CLOCK_STATE_RCD      *clock_table;

private:

public:

   CtiAnsiTableFiveTwo( BYTE *dataBlob );

   virtual ~CtiAnsiTableFiveTwo();

   CtiAnsiTableFiveTwo& operator=(const CtiAnsiTableFiveTwo& aRef);

};
#endif // #ifndef __STD_ANSI_TBL_FIVE_TWO_H__
