

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_FIVE_ONE_H__
#define __STD_ANSI_TBL_FIVE_ONE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_one
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_five_two.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/10/01 17:54:27 $
*    History: 
      $Log: std_ansi_tbl_five_one.h,v $
      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

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

class IM_EX_PROT CtiAnsiTableFiveOne : public CtiAnsiTableBase
{
protected:

   TIME_TOU_RCD      _time_tou;

private:

public:

   CtiAnsiTableFiveOne(  );
   CtiAnsiTableFiveOne( BYTE *dataBlob );
   
   virtual ~CtiAnsiTableFiveOne();

   CtiAnsiTableFiveOne& operator=(const CtiAnsiTableFiveOne& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

};
#endif // #ifndef __STD_ANSI_TBL_FIVE_ONE_H__
