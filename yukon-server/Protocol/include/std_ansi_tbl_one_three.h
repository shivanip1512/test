
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_THREE_H__
#define __STD_ANSI_TBL_ONE_THREE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_three
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_three.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:54 $
*    History: 
      $Log: std_ansi_tbl_one_three.h,v $
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

union INT_CONTROL_RCD
{
   struct CONTROL_RECORD
   {
      unsigned char  sub_int;
      unsigned char  int_mulitplier;
   }cntl_rec;
   UINT  int_length;
};

struct DEMAND_CONTROL_RCD
{
   unsigned char  reset_exclusion;

   struct PF_EXCLUDE
   {
      unsigned char  p_fail_recogntn_tm;
      unsigned char  p_fail_exclusion;
      unsigned char  cold_load_pickup;
   }excludes;

   INT_CONTROL_RCD   *_int_control_rec;
};


#pragma pack( pop )


class IM_EX_PROT CtiAnsiTableOneThree : public CtiAnsiTableBase
{

public:

   CtiAnsiTableOneThree( BYTE *dataBlob, int num_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude );
   virtual ~CtiAnsiTableOneThree();
   CtiAnsiTableOneThree& operator=(const CtiAnsiTableOneThree& aRef);

private:

    int                  _numberDemandCtrlEntries;

    DEMAND_CONTROL_RCD   *_demand_control_record;


};

#endif // #ifndef __STD_ANSI_TBL_ONE_THREE_H__
