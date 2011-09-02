#pragma once

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
