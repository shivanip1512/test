/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_one
*
* Class:
* Date:   9/16/2002
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
#ifndef __STD_ANSI_TBL_ONE_ONE_H__
#define __STD_ANSI_TBL_ONE_ONE_H__
#pragma warning( disable : 4786)



#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

#pragma pack( push, 1)

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

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableOneOne
{
protected:

   SOURCE_RCD           *_source_record;

private:

public:

   int getNumberUOMEntries( void );
   int getNumberDemandControlEntries( void );
   int getDataControlLength( void );
   int getNumberDataControlEntries( void );
   int getNumberConstantsEntries( void );
   int getConstantsSelector( void );
   int getNumberConstants( void );

   bool getOffsetFlag( void );
   bool getSetOnePresentFlag( void );
   bool getSetTwoPresentFlag( void );
   bool getPFExcludeFlag( void );
   bool getSlidingDemandFlag( void );
   bool getResetExcludeFlag( void );

   CtiAnsiTableOneOne( BYTE *dataBlob );
   virtual ~CtiAnsiTableOneOne();
   CtiAnsiTableOneOne& operator=(const CtiAnsiTableOneOne& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ONE_ONE_H__
