#pragma once

#include "dlldefs.h"
#include "std_ansi_tbl_base.h"

typedef struct
{
    UINT8   xBits;
    UINT8   yBits;
} XDotYRcd;

typedef struct
{
    UINT16   numerator;
    UINT16   denominator;
} DspSamplePeriodRcd;

typedef struct
{
    char  ch[8];
    std::string asString() {
        return std::string(ch, 8);
    }
    float asValue() {
        return atof(ch);
    }
} VhIhFhConstantsRcd;

class IM_EX_PROT CtiAnsiFocusMfgTable24 : public CtiAnsiTableBase
{

public:

    CtiAnsiFocusMfgTable24( BYTE *dataBlob, DataOrder dataOrder = MSB );
    virtual ~CtiAnsiFocusMfgTable24();

    void printResult(  );
    float getDSPSamplePeriod();
    float getVhIhFhConstantsRcd();

private:
    VhIhFhConstantsRcd  _vh_ih_fh_const;
    UINT8               _nbr_of_relays;
    XDotYRcd            _x_dot_y_format;
    DspSamplePeriodRcd  _dsp_sample_period;
};


