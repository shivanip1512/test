
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_ZERO_H__
#define __STD_ANSI_TBL_ONE_ZERO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_zero
*
* Class:
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_zero.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/10/07 18:16:46 $
*    History:
      $Log: std_ansi_tbl_one_zero.h,v $
      Revision 1.4  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)
/*
struct SOURCE_FLAGS_BFLD
{
   unsigned char        pf_exclude_flag       :1;
   unsigned char        reset_exclude_flag    :1;
   unsigned char        block_demand_flag     :1;
   unsigned char        sliding_demand_flag   :1;
   unsigned char        thermal_demand_flag   :1;
   unsigned char        set1_preset_flag      :1;
   unsigned char        set2_preset_flag      :1;
   unsigned char        no_offset_flag        :1;
};

struct SOURCE_RCD
{
   SOURCE_FLAGS_BFLD    source_flags;
   char                 nbr_uom_entries;
   unsigned char        nbr_demand_ctrl_entries;
   unsigned char        data_ctrl_length;
   unsigned char        nbr_data_ctrl_entries;
   unsigned char        nbr_constants_entries;
   unsigned char        constants_selector;
   unsigned char        nbr_sources;
};
*/
#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableOneZero : public CtiAnsiTableBase
{
protected:

//   SOURCE_RCD           *source_record;

private:

public:

   int getNumberUOMEntries( void );

   CtiAnsiTableOneZero( BYTE *dataBlob );
   virtual ~CtiAnsiTableOneZero();
   CtiAnsiTableOneZero& operator=(const CtiAnsiTableOneZero& aRef);

};
#endif // #ifndef __STD_ANSI_TBL_ONE_ZERO_H__
