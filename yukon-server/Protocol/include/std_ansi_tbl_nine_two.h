



#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_NINE_TWO_H__
#define __STD_ANSI_TBL_NINE_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_nine_two
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_nine_two.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:45:15 $
*    History: 
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define INT16             __int16
#define INT8               __int8
#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct SETUP_STRINGS_RCD
{
    unsigned char *setup_string;
};
struct GLOBAL_PARMS_RCD
{
    UINT8 psem_identity;
    UINT32 bit_rate;
    SETUP_STRINGS_RCD *modem_setup_strings;
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableNineTwo : public CtiAnsiTableBase
{
protected:

   GLOBAL_PARMS_RCD      _globalParmsTbl;

private:


public:

   CtiAnsiTableNineTwo(  );
   CtiAnsiTableNineTwo( BYTE *dataBlob );
   
   virtual ~CtiAnsiTableNineTwo();

   CtiAnsiTableNineTwo& operator=(const CtiAnsiTableNineTwo& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );
};
#endif // #ifndef __STD_ANSI_TBL_NINE_TWO_H__
