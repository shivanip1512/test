
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_FOUR_H__
#define __STD_ANSI_TBL_ONE_FOUR_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_four
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

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableOneFour
{
protected:

   struct DATA_RCD
   {
      unsigned char     *source_id;
   };

   struct DATA_CONTROL_RCD
   {
      DATA_RCD          *data_rcd;
   };

   DATA_CONTROL_RCD  _data_control_record;


private:

   int         _controlLength;
   int         _controlEntries;

public:

   CtiAnsiTableOneFour( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries );
   virtual ~CtiAnsiTableOneFour();
   CtiAnsiTableOneFour& operator=(const CtiAnsiTableOneFour& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ONE_FOUR_H__
