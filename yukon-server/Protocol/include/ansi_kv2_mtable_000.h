#pragma once

#include "dlldefs.h"
#include "std_ansi_tbl_base.h"

class IM_EX_PROT CtiAnsiKV2ManufacturerTable000 : public CtiAnsiTableBase
{

public:
    typedef enum
    {
        CM21P,
        kV,
        kVModem,
        kV2

    } MeterType_e;

    typedef enum
    {
        demandOnlyMode,
        demandLPMode,
        timeOfUseMode
    } MeterMode_e;

    typedef enum {Filler0,
        Filler1,
        Filler2,
        Filler3,
        demandOnly,
        demandLP,
        timeOfUse,
        Filler4

    } RegisterFunction_e;


    CtiAnsiKV2ManufacturerTable000( BYTE *dataBlob );
    virtual ~CtiAnsiKV2ManufacturerTable000();

    // only things needed at this time
    MeterType_e getMeterType (void);
    MeterMode_e getMeterMode (void);
    RegisterFunction_e getRegisterFunction (void);

    void printResult(  );

    struct UPDGRADES
    {
       unsigned char        _upgrade0              :1;
       unsigned char        _upgrade1              :1;
       unsigned char        _upgrade2              :1;
       unsigned char        _upgrade3              :1;
       unsigned char        _upgrade4              :1;
       unsigned char        _upgrade5              :1;
       unsigned char        _upgrade6              :1;
       unsigned char        _upgrade7              :1;
       unsigned char        _upgrade8              :1;
       unsigned char        _upgrade9              :1;
       unsigned char        _upgrade10             :1;
       unsigned char        _upgrade11             :1;
       unsigned char        _upgrade12             :1;
       unsigned char        _upgrade13             :1;
       unsigned char        _upgrade14             :1;
       unsigned char        _upgrade15             :1;
       unsigned char        _upgrade16             :1;
       unsigned char        _upgrade17             :1;
       unsigned char        _filler1               :6;
       unsigned char        _filler2               :8;
    };


private:

    unsigned char           _mfgVersionNumber;
    unsigned char           _mfgRevionsNumber;
    unsigned char           _gePartNumber[5];
    unsigned char           _firmwareVersion;
    unsigned char           _firmwareRevision;
    unsigned char           _firmwareBuild;
    unsigned char           _mfgTestVector[4];

    unsigned char           _meterType;
    unsigned char           _meterMode;
    unsigned char           _registerFunction;

    unsigned char           _installedOption1;
    unsigned char           _installedOption2;
    unsigned char           _installedOption3;
    unsigned char           _installedOption4;
    unsigned char           _installedOption5;
    unsigned char           _installedOption6;

    UPDGRADES               _upgrades;

    unsigned char           _reservered[4];

    unsigned char           _flashConstantsPartNumber[5];
    unsigned char           _flashConstantsRomVersion;
    unsigned char           _flashConstantsRomRevision;
    unsigned char           _flashConstantsRomBuild;
    unsigned char           _flashConstantsChecksum[2];
    unsigned char           _flashConstantPatchFlags[2];

    unsigned char           _userCalcPartNumber[5];
    unsigned char           _userCalcRomVersion;
    unsigned char           _userCalcRomRevision;
    unsigned char           _userCalcRomBuild;
    unsigned char           _userCalcUserVersion;
    unsigned char           _userCalcUserRevision;
    unsigned char           _userCalcUserBuild;
    unsigned char           _userCalcChecksum[2];


};
