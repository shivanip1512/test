
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_TWO_H__
#define __STD_ANSI_TBL_TWO_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_two
*
* Class:
* Date:   9/20/2002
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


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableTwoTwo
{
protected:

   unsigned char        *_summation_select;
   unsigned char        *_demand_select;
   unsigned char        *_set;
   unsigned char        *_coincident_select;
   unsigned char        *_coin_demand_assoc;


private:

   int   _demandSelectSize;

public:

   int getDemandSelectSize( void );
   unsigned char* getDemandSelect( void );

   CtiAnsiTableTwoTwo( BYTE *dataBlob, int num_sums, int num_demands, int num_coins );
   virtual ~CtiAnsiTableTwoTwo();
   CtiAnsiTableTwoTwo& operator=(const CtiAnsiTableTwoTwo& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_TWO_TWO_H__
