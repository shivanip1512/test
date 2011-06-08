/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_21
*
* Class:
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_21.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_two_one.h,v $
      Revision 1.10  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.8  2005/12/20 17:20:01  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:48  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.6  2005/09/29 21:19:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.5  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_21_H__
#define __STD_ANSI_TBL_21_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct REG_FUNC1_BFLD
{
   unsigned char        season_info_field_flag  :1;
   unsigned char        date_time_field_flag    :1;
   unsigned char        demand_reset_ctr_flag   :1;
   unsigned char        demand_reset_lock_flag  :1;
   unsigned char        cum_demand_flag         :1;
   unsigned char        cont_cum_demand_flag    :1;
   unsigned char        time_remaining_flag     :1;
   unsigned char        filler                  :1;
};

struct REG_FUNC2_BFLD
{
   unsigned char        self_read_inhibit_overflow_flag  :1;
   unsigned char        self_read_seq_nbr_flag           :1;
   unsigned char        daily_self_read_flag             :1;
   unsigned char        weekly_self_read_flag            :1;
   unsigned char        self_read_demand_reset           :2;
   unsigned char        filler                           :2;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable21 : public CtiAnsiTableBase
{
protected:

   REG_FUNC1_BFLD       _reg_func1_flags;
   REG_FUNC2_BFLD       _reg_func2_flags;
   unsigned char        _nbr_self_reads;
   unsigned char        _nbr_summations;
   unsigned char        _nbr_demands;
   unsigned char        _nbr_coin_values;
   unsigned char        _occur;
   unsigned char        _tiers;
   unsigned char        _nbr_present_demands;
   unsigned char        _nbr_present_values;

private:

   int                  _tier;

public:

   int getNumberSummations( void );
   int getNumberDemands( void );
   int getCoinValues( void );
   int getOccur( void );
   int getTiers( void );
   int getNbrPresentDemands(void);
   int getNbrPresentValues(void);
   bool getTimeRemainingFlag(void);
   bool getDemandResetCtrFlag( void );
   bool getSeasonInfoFieldFlag( void);
   bool getTimeDateFieldFlag( void );
   bool getCumDemandFlag( void );
   bool getContCumDemandFlag( void );

   CtiAnsiTable21( );
   CtiAnsiTable21( BYTE *dataBlob );
   virtual ~CtiAnsiTable21();
   CtiAnsiTable21& operator=(const CtiAnsiTable21& aRef);

   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );


};

#endif // #ifndef __STD_ANSI_TBL_21_H__
