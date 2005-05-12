/*-----------------------------------------------------------------------------*
*
* File:   dev_alpah
*
* Class:  CtiDeviceAlphaPPlus
* Date:   02/21/2000
*
* Author: David Sutton
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_APLUS_H__
#define __DEV_APLUS_H__
#pragma warning( disable : 4786 )


#include <windows.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "mgr_point.h"
#include "device.h"
#include "dev_alpha.h"


// possible load profile mappings
#define PPLUS_DISABLED              0
#define PPLUS_KW_DELIVERED          1
#define PPLUS_KW_RECEIVED           2
#define PPLUS_REACTIVE_DELIVERED    3
#define PPLUS_REACTIVE_RECEIVED     4
#define PPLUS_REACTIVE_QUADRANT4    5
#define PPLUS_REACTIVE_QUADRANT3    6
#define PPLUS_REACTIVE_QUADRANT2    7
#define PPLUS_REACTIVE_QUADRANT1    8
#define PPLUS_TOU_BLK1              9
#define PPLUS_TOU_BLK2             10

// additional mappings for blocks 1 or 2
#define PPLUS_KVA_DELIVERED        98
#define PPLUS_KVA_RECEIVED         99

// function definitions
#define PPLUS_PULSE_FUNCTIONALITY_NONE    0
#define PPLUS_PULSE_FUNCTIONALITY_KWH     1
#define PPLUS_PULSE_FUNCTIONALITY_KVAH    2
#define PPLUS_PULSE_FUNCTIONALITY_KVARH   3


#pragma pack(push, alpha_packing, 1)

// class 0
typedef struct _AlphaPPlusClass0Raw_t
{
    UCHAR    UKH[3];
    UCHAR    UPR;
    UCHAR    UKE[5];
    UCHAR    INTNORM;
    UCHAR    INTTEST;
    UCHAR    DPLOCE;
    UCHAR    DPLOCD;
    UCHAR    NUMSBI;
    UCHAR    VTRATIO[3];
    UCHAR    CTRATIO[3];
    UCHAR    XFACTOR[4];
    UCHAR    SPARES[15];
    UCHAR    CLOCKS;

} AlphaPPlusClass0Raw_t;

// class 2     64 bytes
typedef struct _AlphaPPlusClass2Raw_t
{
    UCHAR    UMTRSN[5];
    UCHAR    ACCTID[14];
    UCHAR    USER[11];
    UCHAR    DFOVAL;
    UCHAR    DFGVAL;
    UCHAR    DRLVAL;
    UCHAR    KWOVRL[3];
    UCHAR    KWTHRSA[3];
    UCHAR    KWTHRSB[3];
    UCHAR    KWTHRSC[3];
    UCHAR    KWTHRSD[3];
    UCHAR    E2KYZDV;
    UCHAR    EMETFLG[2];
    UCHAR    EATRVAL;
    UCHAR    EBLKCF1;
    UCHAR    EBLKCF2;
    UCHAR    SPARE[9];
    UCHAR    CL2CKS;
} AlphaPPlusClass2Raw_t;


typedef struct _AlphaPPlusClass6Raw_t
{
    UCHAR   XREV;
    UCHAR   XPGMREV;
    UCHAR   XUOM[2];
    UCHAR   RESERVED[3];
    UCHAR   DSPFUNC;
    UCHAR   XUOM1;
    UCHAR   XUOM2;
    UCHAR   RESERVED2[3];
    UCHAR   XPGMREV2[2];
    UCHAR   RESERVED3[272];
    UCHAR   CL6CKS;
} AlphaPPlusClass6Raw_t;

// class 11   212 bytes
typedef struct _AlphaPPlusClass11Raw_t
{
    UCHAR          AKWH1[7];
    UCHAR          AKW1[3];
    AlphaDateTime_t     ATD1;
    UCHAR          AKWCUM1[3];
    UCHAR          AKWC1[3];
    UCHAR          BKWH1[7];
    UCHAR          BKW1[3];
    AlphaDateTime_t     BTD1;
    UCHAR          BKWCUM1[3];
    UCHAR          BKWC1[3];
    UCHAR          CKWH1[7];
    UCHAR          CKW1[3];
    AlphaDateTime_t     CTD1;
    UCHAR          CKWCUM1[3];
    UCHAR          CKWC1[3];
    UCHAR          DKWH1[7];
    UCHAR          DKW1[3];
    AlphaDateTime_t     DTD1;
    UCHAR          DKWCUM1[3];
    UCHAR          DKWC1[3];
    UCHAR          AKWH2[7];
    UCHAR          AKW2[3];
    AlphaDateTime_t     ATD2;
    UCHAR          AKWCUM2[3];
    UCHAR          AKWC2[3];
    UCHAR          BKWH2[7];
    UCHAR          BKW2[3];
    AlphaDateTime_t     BTD2;
    UCHAR          BKWCUM2[3];
    UCHAR          BKWC2[3];
    UCHAR          CKWH2[7];
    UCHAR          CKW2[3];
    AlphaDateTime_t     CTD2;
    UCHAR          CKWCUM2[3];
    UCHAR          CKWC2[3];
    UCHAR          DKWH2[7];
    UCHAR          DKW2[3];
    AlphaDateTime_t     DTD2;
    UCHAR          DKWCUM2[3];
    UCHAR          DKWC2[3];
    UCHAR          EKVARH4[7];
    UCHAR          EKVARH3[7];
    UCHAR          EKVARH2[7];
    UCHAR          EKVARH1[7];
    UCHAR          ETKWH1[7];
    UCHAR          ETKWH2[7];
    UCHAR          EAVGPF[2];
} AlphaPPlusClass11Raw_t;

typedef struct _AlphaPPlusClass14Raw_t
{
    UCHAR    Spare1[3];
    UCHAR    scalingFactor;   // RLPSCAL     load profile pulse scaling factor
    UCHAR    intervalLength;  // LPLEN       load profile interval length
    UCHAR    dayRecordSize[2];// DASIZE[2]   load profile day record size
    UCHAR    lpMemory;        // LPMEM       amount of load profile memory in days
    UCHAR    numberOfChannels;// CHANS;      number channels
    UCHAR    channel1Input;   // IO01FLG     channel 1 selection
    UCHAR    channel2Input;   // IO02FLG     channel 2 selection
    UCHAR    channel3Input;   // IO03FLG     channel 3 selection
    UCHAR    channel4Input;   // IO04FLG     channel 4 selection
    UCHAR    Spare2[28];
    UCHAR    checkSum;        //CL14CKS      class checksum
} AlphaPPlusClass14Raw_t;


// class 82     107 bytes
typedef struct _AlphaPPlusClass82Raw_t
{
    UCHAR    Filler1[9];    // some filler
    UCHAR    BLK1[3];       // KW Demand last interval
    UCHAR    Filler2[51];   // some filler
    UCHAR    BLK2[3];       // Last interval KVAR
    UCHAR    Filler3[51];
} AlphaPPlusClass82Raw_t;


typedef struct _AlphaPPlusClass0Real_t
{
    BOOL          valid;
    FLOAT     wattHoursPerRevolution;
    USHORT       pulsesPerRevolution;   //
    FLOAT         energyDecimals;
    FLOAT         demandDecimals;
} AlphaPPlusClass0Real_t;


typedef struct _AlphaPPlusClass2Real_t
{
    BOOL           valid;
    UCHAR          configTOUBlk1;
    UCHAR          configTOUBlk2;
} AlphaPPlusClass2Real_t;


typedef struct _AlphaPPlusClass6Real_t
{
    BOOL        valid;
    USHORT      primaryFunction;
    USHORT      secondaryFunction;
} AlphaPPlusClass6Real_t;

// class 11
typedef struct _AlphaPPlusClass11Real_t
{
    BOOL           valid;
    DOUBLE         AKWH1;
    FLOAT          AKW1;
    AlphaDateTime_t     ATD1;
    FLOAT          AKWCUM1;
    FLOAT          AKWC1;
    DOUBLE         BKWH1;
    FLOAT          BKW1;
    AlphaDateTime_t     BTD1;
    FLOAT          BKWCUM1;
    FLOAT          BKWC1;
    DOUBLE         CKWH1;
    FLOAT          CKW1;
    AlphaDateTime_t     CTD1;
    FLOAT          CKWCUM1;
    FLOAT          CKWC1;
    DOUBLE         DKWH1;
    FLOAT          DKW1;
    AlphaDateTime_t     DTD1;
    FLOAT          DKWCUM1;
    FLOAT          DKWC1;
    DOUBLE         AKWH2;
    FLOAT          AKW2;
    AlphaDateTime_t     ATD2;
    FLOAT          AKWCUM2;
    FLOAT          AKWC2;
    DOUBLE         BKWH2;
    FLOAT          BKW2;
    AlphaDateTime_t     BTD2;
    FLOAT          BKWCUM2;
    FLOAT          BKWC2;
    DOUBLE         CKWH2;
    FLOAT          CKW2;
    AlphaDateTime_t     CTD2;
    FLOAT          CKWCUM2;
    FLOAT          CKWC2;
    DOUBLE         DKWH2;
    FLOAT          DKW2;
    AlphaDateTime_t     DTD2;
    FLOAT          DKWCUM2;
    FLOAT          DKWC2;
    DOUBLE         EKVARH4;
    DOUBLE         EKVARH3;
    DOUBLE         EKVARH2;
    DOUBLE         EKVARH1;
    DOUBLE         ETKWH1;
    DOUBLE         ETKWH2;
    FLOAT          EAVGPF;
} AlphaPPlusClass11Real_t;


typedef struct _AlphaPPlusClass14Real_t
{
    USHORT    scalingFactor;   // RLPSCAL     load profile pulse scaling factor
    USHORT    intervalLength;  // LPLEN       load profile interval length
    USHORT    dayRecordSize;   // DASIZE[2]   load profile day record size
    USHORT    lpMemory;        // LPMEM       amount of load profile memory in days
    USHORT    numberOfChannels;// CHANS;      number channels
    USHORT    channelInput[4];   // IO01FLG     channel 1 selection
    USHORT    checkSum;        //CL14CKS      class checksum

} AlphaPPlusClass14Real_t;


typedef struct _AlphaPPlusClass18Real_t
{
    AlphaDateTime_t      recordDateTime;
    USHORT          dstFlag;
    USHORT          checkSum;
    BYTE            data[2500]; // assuming only 4 channels are available
} AlphaPPlusClass18Real_t;

// class 82
typedef struct _AlphaPPlusClass82Real_t
{
    BOOL           valid;
    FLOAT          BLK1Demand;
    FLOAT          BLK2Demand;
} AlphaPPlusClass82Real_t;

typedef struct _AlphaPPlusScanData_t
{
    struct
    {
        // order in this struct matters, do not move anything around !!!!!
        AlphaPPlusClass0Raw_t  class0;
        AlphaPPlusClass6Raw_t  class6;
        AlphaPPlusClass2Raw_t  class2;
        AlphaPPlusClass82Raw_t class82;
        AlphaPPlusClass11Raw_t class11;
    } Byte;


    struct
    {
        AlphaPPlusClass0Real_t  class0;
        AlphaPPlusClass6Real_t  class6;
        AlphaPPlusClass2Real_t  class2;
        AlphaPPlusClass82Real_t class82;
        AlphaPPlusClass11Real_t class11;

    } Real;

    INT      totalByteCount;
    BOOL     bDataIsReal;

} AlphaPPlusScanData_t;


typedef struct _AlphaPPlusLoadProfile_t
{
    ULONG                       porterLPTime;
    ULONG                       daysRequested;
    ULONG                       dayRecordSize;
    AlphaPPlusClass0Real_t      class0;
    AlphaPPlusClass2Real_t      class2;
    AlphaPPlusClass6Real_t      class6;
    AlphaPPlusClass14Real_t     class14;
    AlphaPPlusClass18Real_t     class18;
} AlphaPPlusLoadProfile_t;

#pragma pack(pop, alpha_packing)     // Restore the prior packing alignment..


class IM_EX_DEVDB CtiDeviceAlphaPPlus : public CtiDeviceAlpha
{
protected:

    private:

    CtiDeviceAlphaPPlus & operator=(const CtiDeviceAlphaPPlus & aRef)
    {
        cout << __FILE__ << " = operator is invalid for this device" << endl;
        return *this;
    }

    CtiDeviceAlphaPPlus (const CtiDeviceAlphaPPlus & aRef)
    {
        cout << __FILE__ << " copy constructor is invalid for this device" << endl;
    }


public:

    typedef CtiDeviceAlpha Inherited;

    CtiDeviceAlphaPPlus(BYTE         *dataPtr  = NULL,
                        BYTE          *lpPtr = NULL,
                        BYTE          *wPtr = NULL,
                        ULONG         cnt=0) :

    Inherited (dataPtr,lpPtr,wPtr,cnt)
    {
    }


    virtual ~CtiDeviceAlphaPPlus()
    {
        // all the databuffers are destroyed in base if needed
    }

    virtual INT ResultDisplay (INMESS *InMessage);
    virtual INT GeneralScan(CtiRequestMsg *pReq,
                            CtiCommandParser &parse,
                            OUTMESS *&OutMessage,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS > &outList,
                            INT ScanPriority = MAXPRIORITY - 4);

    virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived);
    virtual INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes);
    virtual INT allocateDataBins (OUTMESS *outMess);

    virtual INT generateCommandScan( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT generateCommandLoadProfile( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList );

    virtual INT decodeResponseScan (CtiXfer  &Transfer,INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

    virtual INT decodeResultLoadProfile (INMESS *InMessage,
                                           RWTime &TimeNow,
                                           RWTPtrSlist< CtiMessage >   &vgList,
                                           RWTPtrSlist< CtiMessage > &retList,
                                           RWTPtrSlist< OUTMESS > &outList);
    virtual INT decodeResultScan (INMESS *InMessage,
                                    RWTime &TimeNow,
                                    RWTPtrSlist< CtiMessage >   &vgList,
                                    RWTPtrSlist< CtiMessage > &retList,
                                    RWTPtrSlist< OUTMESS > &outList);



    //  are these ever used?  2001-oct-04  mskf
    //INT ResultFailureDisplay (INT FailError);
    //UCHAR printPowerPlusStatus(UCHAR Status, BOOL traceFlag);

    INT   getAPlusClassOffset(UINT Key, VOID *ptr);
    INT   getAPlusFuncOffset(UINT Key, VOID *ptr);

    LONG findLPDataPoint (AlphaLPPointInfo_t &point, USHORT aMapping, AlphaPPlusClass2Real_t class2);

    BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, AlphaPPlusScanData_t *aScanData);

    BOOL getRateValueFromBlock (DOUBLE &aValue, USHORT aValueType,USHORT aBlockMapping, USHORT aRate, RWTime &aPeak,  AlphaPPlusScanData_t *data);
    BOOL getDemandValueFromBlock1 (DOUBLE &aValue, USHORT aRate, RWTime &aPeak, AlphaPPlusScanData_t *aScanData);
    BOOL getDemandValueFromBlock2 (DOUBLE &aValue, USHORT aRate, RWTime &aPeak, AlphaPPlusScanData_t *aScanData);
    BOOL getEnergyValueFromBlock1 (DOUBLE &aValue, USHORT aRate, RWTime &aPeak, AlphaPPlusScanData_t *aScanData);
    BOOL getEnergyValueFromBlock2 (DOUBLE &aValue, USHORT aRate, RWTime &aPeak, AlphaPPlusScanData_t *aScanData);
    USHORT getOffsetMapping (int aOffset);
    USHORT getRate (int aOffset);
    USHORT primaryFunction (UCHAR function);
    USHORT secondaryFunction (UCHAR function);

    USHORT calculateStartingByteCountForCurrentScanState (int aClass);

    // defined first in dev_alph.cpp
    virtual UCHAR touBlockMapping (UCHAR config, USHORT type);
};
#endif // #ifndef __DEV_APLUS_H__


