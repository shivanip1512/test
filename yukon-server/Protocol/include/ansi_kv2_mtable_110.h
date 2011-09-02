#pragma once

#include "dlldefs.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct PRESENT_REG_RCD
{
    //total size = 166 bytes
    UINT32  previousIntvlDemand[5];
    UINT32  demands[5];
    UINT32  kwDmdFundPlus[3];
    UINT32  kwDmdFundOnly[3];
    UINT32  kvarDmdFundPlus[3];
    UINT32  kvarDmdFundOnly[3];
    UINT32  distortionKVADmd[3];
    UINT32  apparentKVADmd[3];
    UINT16  vlnFundPlus[3];
    UINT16  vlnFundOnly[3];
    UINT16  vllFundPlus[3];
    UINT16  vllFundOnly[3];
    UINT16  currFundPlus[3];
    UINT16  currFundOnly[3];
    UINT16  imputedNeutralCurr;
    UINT8   powerFactor;
    UINT16  frequency;
    UINT8   tdd[3];
    UINT8   ithd[3];
    UINT8   vthd[3];
    UINT8   distortionPF[4];

};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiKV2ManufacturerTable110 : public CtiAnsiTableBase
{

public:
    CtiAnsiKV2ManufacturerTable110( BYTE *dataBlob );
    virtual ~CtiAnsiKV2ManufacturerTable110();

    void printResult(  );

    UINT32* getPreviousIntvlDemands();
    UINT32* getDemands();
    UINT32* getKWDmdFundPlus();
    UINT32* getKWDmdFundOnly();
    UINT32* getKVARDmdFundPlus();
    UINT32* getKVARDmdFundOnly();
    UINT32* getDistortionKVADmd();
    UINT32* getApparentKVADmd();
    UINT16* getVlnFundPlus();
    UINT16* getVlnFundOnly();
    UINT16* getVllFundPlus();
    UINT16* getVllFundOnly();
    UINT16* getCurrFundPlus();
    UINT16* getCurrFundOnly();
    UINT16 getImputedNeutralCurr();
    UINT8 getPowerFactor();
    UINT16 getFrequency();
    UINT8* getTDD();
    UINT8* getITHD();
    UINT8* getVTHD();
    UINT8* getDistortionPF();

protected:
private:

    PRESENT_REG_RCD  _presentRegTbl;
};


