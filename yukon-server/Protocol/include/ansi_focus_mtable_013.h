
#pragma once

#include "dlldefs.h"
#include "std_ansi_tbl_base.h"


/*
3.2 Q_X_DOT_Y_BFLD
Note:  meters with firmware version 5.27.0.11.4 and prior use the Q9.7 format. 
Starting with firmware version 5.28.0.0.5, meters now use the Q10.6 format. 
In 5.28, a new field (X_DOT_Y_FORMAT) was added to MFG table 24, so that the QX.Y format can be determined.
For the Q10.6 Format:
TYPE Q_X_DOT_Y_BFLD = BIT FIELD OF UINT16
FRACTION    : UINT(0..5);
INTEGER     : UINT(6..15);
END;
To convert this 16-bit packed value into a number, one can divide the number by 2^6 = 64 (since there are 6 fractional bits).  Thus, a packed value of 0xFFFF = 65535, is really 65535 / 64 = 1023.984375.

For the Q9.7 Format:
TYPE Q_X_DOT_Y_BFLD = BIT FIELD OF UINT16
FRACTION    : UINT(0..6);
INTEGER     : UINT(7..15);
END;
To convert this 16-bit packed value into a number, one can divide the number by 2^7 = 128 (since there are 7 fractional bits).  Thus, a packed value of 0xFFFF = 65535, is really 65535 / 128 = 511.9921875.
*/
typedef enum
{
    Q9_7,
    Q10_6
} XdotYformat;

struct Q_X_DOT_Y_BFLD
{
    union 
    {
        struct  
        {
            unsigned short fraction  :6;
            unsigned short integer   :10;
        } q10;
        struct  
        {
            unsigned short fraction  :7;
            unsigned short integer   :9;
        } q9;
        unsigned short rawValue;
    };

    float asValue(XdotYformat format) {
        if (format == Q10_6)
        {
            return rawValue / 64;
        }
        else
        {
            return rawValue / 127;
        }
    }
};

struct GyrboxPhaseInfoRcd
{
    Q_X_DOT_Y_BFLD  phaseVoltage[3];
    Q_X_DOT_Y_BFLD  phaseVoltageAngle[3];
    Q_X_DOT_Y_BFLD  phaseCurrent[3];
    Q_X_DOT_Y_BFLD  reserved[3];
};
struct InstantMeasureRcd
{
    INT32  instantPower;
    Q_X_DOT_Y_BFLD  neutralCurrent;
    UINT16 reserved[7];
};



class IM_EX_PROT CtiAnsiFocusMfgTable13 : public CtiAnsiTableBase
{

public:

    CtiAnsiFocusMfgTable13( BYTE *dataBlob, int fwVersion, int fwRevision, DataOrder dataOrder = MSB );
    virtual ~CtiAnsiFocusMfgTable13();

    void printResult(  );
    short getPhaseVoltage(PhaseType phase);
    short getPhaseCurrent(PhaseType phase);
    short getNeutralCurrent(  );
    

private:
    GyrboxPhaseInfoRcd _gyrboxPhaseInfo;
    InstantMeasureRcd  _instantMeasure;
    INT8               _temperature;
    UINT8              _unused;
    Q_X_DOT_Y_BFLD     _lineFrequency;
    UINT8              _reverseRotationPulseCnt;
    Q_X_DOT_Y_BFLD     _loadSidePhaseAVoltage;
    Q_X_DOT_Y_BFLD     _loadSidePhaseCVoltage;

    XdotYformat        _xyFormat;
    
};
