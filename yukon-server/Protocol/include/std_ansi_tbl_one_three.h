
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


class IM_EX_PROT CtiAnsiTableOneThree
{
protected:

   int                  _numberEntries;

   DEMAND_CONTROL_RCD   *_demand_control_record;


private:

public:

   CtiAnsiTableOneThree( BYTE *dataBlob, int num_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude );
   virtual ~CtiAnsiTableOneThree();
   CtiAnsiTableOneThree& operator=(const CtiAnsiTableOneThree& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ONE_THREE_H__
