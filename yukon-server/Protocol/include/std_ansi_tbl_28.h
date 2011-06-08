/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_28
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_28.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:32 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_28_H__
#define __STD_ANSI_TBL_28_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
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

class IM_EX_PROT CtiAnsiTable28 : public CtiAnsiTableBase
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
    bool _lsbDataOrder;

public:

   CtiAnsiTable28( );
   CtiAnsiTable28( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues,
                         bool timeRemainingFlag, int format1, int format2, int timefmt, bool lsbDataOrder = true );
   virtual ~CtiAnsiTable28();
   CtiAnsiTable28& operator=(const CtiAnsiTable28& aRef);
   void printResult( const std::string& deviceName );

   double getPresentDemand(int index );
   double getPresentValue(int index );


};

#endif // #ifndef __STD_ANSI_TBL_28_H__
