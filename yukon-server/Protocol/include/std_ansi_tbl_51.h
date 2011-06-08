/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_51
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_five_two.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_five_one.h,v $
      Revision 1.5  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.3  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_51_H__
#define __STD_ANSI_TBL_51_H__
#pragma warning( disable : 4786)


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

   CtiAnsiTable51(  );
   CtiAnsiTable51( BYTE *dataBlob );

   virtual ~CtiAnsiTable51();

   CtiAnsiTable51& operator=(const CtiAnsiTable51& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );

};
#endif // #ifndef __STD_ANSI_TBL_51_H__
