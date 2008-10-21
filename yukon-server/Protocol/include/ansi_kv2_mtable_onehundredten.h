/*---------------------------------------------------------------------------------*
*
* File:
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/ansi_kv2_mtable_110.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
    History
      $Log: ansi_kv2_mtable_onehundredten.h,v $
      Revision 1.2  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.1  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.2  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2


*----------------------------------------------------------------------------------*/
#ifndef __ANSI_KV2_MTABLE_110_H__
#define __ANSI_KV2_MTABLE_110_H__
#pragma warning( disable : 4786)

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

#endif // #ifndef ANSI_KV2_MTABLE_110_H

