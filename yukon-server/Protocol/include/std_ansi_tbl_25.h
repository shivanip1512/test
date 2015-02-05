#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"
#include "std_ansi_tbl_23.h"

#define BCD   unsigned char

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable25 : public CtiAnsiTableBase
{
    BOOL _dateTimeFieldFlag;
    BOOL _seasonInfoFieldFlag;

    ULONG _endDateTime;
    unsigned char _season;

    CtiAnsiTable23 *_prevDemandResetData;

public:

   CtiAnsiTable25( int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, bool season );
   CtiAnsiTable25( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, bool season );

   virtual ~CtiAnsiTable25();
   CtiAnsiTable25& operator=(const CtiAnsiTable25& aRef);

   CtiAnsiTable23 *getDemandResetDataTable( );
   double getEndDateTime();
   unsigned char getSeason();

   void printResult( const std::string& deviceName );
};
