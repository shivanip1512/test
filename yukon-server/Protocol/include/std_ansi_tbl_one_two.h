/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_two
*
* Class:
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_ONE_TWO_H__
#define __STD_ANSI_TBL_ONE_TWO_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"


#pragma pack( push, 1)

struct UOM_ENTRY_BFLD
{
   unsigned char     id_code                    :8;
   unsigned char     time_base                  :3;
   unsigned char     multiplier                 :3;
   unsigned char     q1_accountablility         :1;
   unsigned char     q2_accountablility         :1;
   unsigned char     q3_accountablility         :1;
   unsigned char     q4_accountablility         :1;
   unsigned char     net_flow_accountablility   :1;
   unsigned char     segmentation               :3;
   unsigned char     harmonic                   :1;
   unsigned char     reserved                   :8;
   unsigned char     nfs                        :1;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableOneTwo
{
protected:

   //can have 255 of these
   UOM_ENTRY_BFLD    *uom_entries;

private:

public:

   int getIdCode( void );
   int getTimeBase( void );
   int getMultiplier( void );

   CtiAnsiTableOneTwo( BYTE *dataBlob, int num_entries );
   virtual ~CtiAnsiTableOneTwo();
   CtiAnsiTableOneTwo& operator=(const CtiAnsiTableOneTwo& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ONE_TWO_H__
