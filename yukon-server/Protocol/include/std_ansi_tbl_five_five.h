/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_five
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:49 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_FIVE_FIVE_H__
#define __STD_ANSI_TBL_FIVE_FIVE_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

#define BCD                unsigned char

#pragma pack( push, 1)

struct STATUS_BFLD
{
   union seperate_sum
   {
      struct first
      {
         unsigned charcurr_summ_tier      :3;
         unsigned charcurr_demand_tier    :3;
      };

      struct sec
      {
         unsigned charcurr_tier           :3;
         unsigned charfiller              :3;
      };
   };

   unsigned char  tier_drive              :2;
   unsigned char  special_schd_active     :4;
   unsigned char  season                  :4;
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

struct TIME_DATE_QUAL_BFLD
{
   unsigned char        day_of_week          :3;
   unsigned char        dst_flag             :1;
   unsigned char        gmt_flag             :1;
   unsigned char        tm_zn_applied_flag   :1;
   unsigned char        dst_applied_flag     :1;
   unsigned char        filler               :1;
};

struct TABLE_55_CLOCK_STATE
{
   LTIME_DATE           clock_calendar;
   TIME_DATE_QUAL_BFLD  time_date;
   STATUS_BFLD          status;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableFiveFive
{
protected:

   TABLE_55_CLOCK_STATE    *_tableFiveFive;

private:

public:

   CtiAnsiTableFiveFive( BYTE *dataBlob );

   virtual ~CtiAnsiTableFiveFive();

   CtiAnsiTableFiveFive& operator=(const CtiAnsiTableFiveFive& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_FIVE_FIVE_H__
