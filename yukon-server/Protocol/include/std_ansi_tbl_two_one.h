
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_ONE_H__
#define __STD_ANSI_TBL_TWO_ONE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_one
*
* Class:
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:41:35 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

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

class IM_EX_PROT CtiAnsiTableTwoOne
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
   int getTier( void );
   bool getDemandResetCtrFlag( void );
   bool getTimeDateFieldFlag( void );
   bool getCumDemandFlag( void );
   bool getContCumDemandFlag( void );

   CtiAnsiTableTwoOne( BYTE *dataBlob );
   virtual ~CtiAnsiTableTwoOne();
   CtiAnsiTableTwoOne& operator=(const CtiAnsiTableTwoOne& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_TWO_ONE_H__
