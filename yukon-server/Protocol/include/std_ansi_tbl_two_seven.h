

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_SEVEN_H__
#define __STD_ANSI_TBL_TWO_SEVEN_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_seven
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_two_seven.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/01/25 18:33:51 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"


#define UINT16             __int16
#define UINT8               __int8
class IM_EX_PROT CtiAnsiTableTwoSeven : public CtiAnsiTableBase
{
protected:

  UINT8 *_presentDemandSelect;
  UINT8 *_presentValueSelect;

private:

    UINT8 _nbrPresentDemands;
    UINT8 _nbrPresentValues;

public:

   CtiAnsiTableTwoSeven(  );
   CtiAnsiTableTwoSeven( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues );
   virtual ~CtiAnsiTableTwoSeven();
   CtiAnsiTableTwoSeven& operator=(const CtiAnsiTableTwoSeven& aRef);
   void printResult(  );

   UINT8* getDemandSelect( );
   UINT8* getValueSelect( );
   
};
#endif // #ifndef __STD_ANSI_TBL_SIX_TWO_H__

