
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_SIX_H__
#define __STD_ANSI_TBL_ONE_SIX_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_six
*
* Class:
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_six.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/04/25 15:09:54 $
*    History: 
      $Log: std_ansi_tbl_one_six.h,v $
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

struct SOURCE_LINK_BFLD
{
   unsigned char     uom_entry_flag          :1;
   unsigned char     demand_ctrl_flag        :1;
   unsigned char     data_ctrl_flag          :1;
   unsigned char     constants_flag          :1;
   unsigned char     pulse_engr_flag         :1;
   unsigned char     constant_to_be_applied  :1;
   unsigned char     filler                  :2;
};


#pragma pack( pop )


class IM_EX_PROT CtiAnsiTableOneSix : public CtiAnsiTableBase
{
protected:

   SOURCE_LINK_BFLD  *_source_link;

private:

public:

   CtiAnsiTableOneSix( BYTE *dataBlob, int num_sources );
   virtual ~CtiAnsiTableOneSix();
   CtiAnsiTableOneSix& operator=(const CtiAnsiTableOneSix& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ONE_SIX_H__
