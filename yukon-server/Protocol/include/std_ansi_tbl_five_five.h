
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_FIVE_FIVE_H__
#define __STD_ANSI_TBL_FIVE_FIVE_H__

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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_five_five.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:20 $
*
*    History: 
      $Log: std_ansi_tbl_five_five.h,v $
      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
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

class IM_EX_PROT CtiAnsiTableFiveFive : public CtiAnsiTableBase
{
protected:

   CLOCK_55_STATE_RCD    _clockStateTbl;

private:

public:

   CtiAnsiTableFiveFive( BYTE *dataBlob );

   virtual ~CtiAnsiTableFiveFive();

   CtiAnsiTableFiveFive& operator=(const CtiAnsiTableFiveFive& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

};

#endif // #ifndef __STD_ANSI_TBL_FIVE_FIVE_H__
