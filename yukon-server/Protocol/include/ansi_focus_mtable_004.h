
#pragma once

#include "dlldefs.h"
#include "std_ansi_tbl_base.h"

class IM_EX_PROT CtiAnsiFocusMfgTable04 : public CtiAnsiTableBase
{

public:

    CtiAnsiFocusMfgTable04( BYTE *dataBlob, DataOrder dataOrder = MSB );
    virtual ~CtiAnsiFocusMfgTable04();

    void printResult(  );
    UINT16 getInstantVoltage( int index );
    UINT16 getInstantPower( );

private:
    UINT16 _instantVoltage1;
    UINT16 _instantVoltage2;
    UINT32 _instantPower;
};


