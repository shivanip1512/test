


#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_EIGHT_H__
#define __STD_ANSI_TBL_TWO_EIGHT_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_eight
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_two_eight.h-arc  $
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

#pragma pack( push, 1)


struct PRESENT_DEMAND_RCD
{
    ULONG timeRemaining;
    double demandValue;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableTwoEight : public CtiAnsiTableBase
{
protected:

  PRESENT_DEMAND_RCD *_presentDemand;
  double *_presentValue;

private:

    UINT8 _nbrPresentDemands;
    UINT8 _nbrPresentValues;
    bool _timeRemainingFlag;
    int _format1;
    int _format2;
    int _timefmt;

public:

   CtiAnsiTableTwoEight( );
   CtiAnsiTableTwoEight( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues,
                         bool timeRemainingFlag, int format1, int format2, int timefmt );
   virtual ~CtiAnsiTableTwoEight();
   CtiAnsiTableTwoEight& operator=(const CtiAnsiTableTwoEight& aRef);
   void printResult(  );

   double getPresentDemand(int index );
   double getPresentValue(int index );

   
};

#endif // #ifndef __STD_ANSI_TBL_TWO_EIGHT_H__
