#pragma once

#include <windows.h>
#include <iostream>
#include <vector>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"

// possible load profile mappings
#define A1_DISABLED              0
#define A1_KW_DELIVERED          1
#define A1_KW_RECEIVED           2
#define A1_REACTIVE_DELIVERED    3
#define A1_REACTIVE_RECEIVED     4
#define A1_REACTIVE_QUADRANT4    5
#define A1_REACTIVE_QUADRANT3    6
#define A1_REACTIVE_QUADRANT2    7
#define A1_REACTIVE_QUADRANT1    8
#define A1_TOU_BLK1             10
#define A1_TOU_BLK2             20
#define A1_TOU_BLK3             30
#define A1_TOU_BLK4             40
#define A1_TOU_BLK5             50
#define A1_TOU_BLK6             60

// additional mappings for blocks 1 or 2
#define A1_KVA_DELIVERED        98
#define A1_KVA_RECEIVED         99

#define ALPHA_A1R                 0
#define ALPHA_A1K                 1

#pragma pack(push, alpha_packing, 1)

// class 0  15 bytes
typedef struct _AlphaA1Class0Raw_t
{
  UCHAR    UKH[3];
  UCHAR    UPR;
  UCHAR    UKE[5];
  UCHAR    INTNORM;
  UCHAR    INTTEST;
  UCHAR    DPLOCE;
  UCHAR    DPLOCD;
  UCHAR    NUMSBI;
  UCHAR    CLOCKS;
} AlphaA1Class0Raw_t;

typedef struct _AlphaA1Class8Raw_t
{
  UCHAR    SSPEC[5];
  UCHAR    PCODE[2];
  UCHAR    XUOMH;
} AlphaA1Class8Raw_t;

// class 11   496 bytes
typedef struct _AlphaA1Class11Raw_t
{
   UCHAR          KWH1[3];
   UCHAR          AKWH1[3];
   UCHAR          AKW1[3];
   UCHAR          AKWCUM1[3];
   AlphaDateTime_t     ATD1;
   UCHAR          BKWH1[3];
   UCHAR          BKW1[3];
   UCHAR          BKWCUM1[3];
   AlphaDateTime_t     BTD1;
   UCHAR          CKWH1[3];
   UCHAR          CKW1[3];
   UCHAR          CKWCUM1[3];
   AlphaDateTime_t     CTD1;
   UCHAR          DKWH1[3];
   UCHAR          DKW1[3];
   UCHAR          DKWCUM1[3];
   AlphaDateTime_t     DTD1;
   UCHAR          SPARE1[3];

   UCHAR          KWH2[3];
   UCHAR          AKWH2[3];
   UCHAR          AKW2[3];
   UCHAR          AKWCUM2[3];
   AlphaDateTime_t     ATD2;
   UCHAR          BKWH2[3];
   UCHAR          BKW2[3];
   UCHAR          BKWCUM2[3];
   AlphaDateTime_t     BTD2;
   UCHAR          CKWH2[3];
   UCHAR          CKW2[3];
   UCHAR          CKWCUM2[3];
   AlphaDateTime_t     CTD2;
   UCHAR          DKWH2[3];
   UCHAR          DKW2[3];
   UCHAR          DKWCUM2[3];
   AlphaDateTime_t     DTD2;
   UCHAR          SPARE2[3];

   UCHAR          KWH3[3];
   UCHAR          AKWH3[3];
   UCHAR          AKW3[3];
   UCHAR          AKWCUM3[3];
   AlphaDateTime_t     ATD3;
   UCHAR          BKWH3[3];
   UCHAR          BKW3[3];
   UCHAR          BKWCUM3[3];
   AlphaDateTime_t     BTD3;
   UCHAR          CKWH3[3];
   UCHAR          CKW3[3];
   UCHAR          CKWCUM3[3];
   AlphaDateTime_t     CTD3;
   UCHAR          DKWH3[3];
   UCHAR          DKW3[3];
   UCHAR          DKWCUM3[3];
   AlphaDateTime_t     DTD3;
   UCHAR          SPARE3[3];

   UCHAR          KWH4[3];
   UCHAR          AKWH4[3];
   UCHAR          AKW4[3];
   UCHAR          AKWCUM4[3];
   AlphaDateTime_t     ATD4;
   UCHAR          BKWH4[3];
   UCHAR          BKW4[3];
   UCHAR          BKWCUM4[3];
   AlphaDateTime_t     BTD4;
   UCHAR          CKWH4[3];
   UCHAR          CKW4[3];
   UCHAR          CKWCUM4[3];
   AlphaDateTime_t     CTD4;
   UCHAR          DKWH4[3];
   UCHAR          DKW4[3];
   UCHAR          DKWCUM4[3];
   AlphaDateTime_t     DTD4;
   UCHAR          SPARE4[3];

   UCHAR          KWH5[3];
   UCHAR          AKWH5[3];
   UCHAR          AKW5[3];
   UCHAR          AKWCUM5[3];
   AlphaDateTime_t     ATD5;
   UCHAR          BKWH5[3];
   UCHAR          BKW5[3];
   UCHAR          BKWCUM5[3];
   AlphaDateTime_t     BTD5;
   UCHAR          CKWH5[3];
   UCHAR          CKW5[3];
   UCHAR          CKWCUM5[3];
   AlphaDateTime_t     CTD5;
   UCHAR          DKWH5[3];
   UCHAR          DKW5[3];
   UCHAR          DKWCUM5[3];
   AlphaDateTime_t     DTD5;
   UCHAR          SPARE5[3];

   UCHAR          KWH6[3];
   UCHAR          AKWH6[3];
   UCHAR          AKW6[3];
   UCHAR          AKWCUM6[3];
   AlphaDateTime_t     ATD6;
   UCHAR          BKWH6[3];
   UCHAR          BKW6[3];
   UCHAR          BKWCUM6[3];
   AlphaDateTime_t     BTD6;
   UCHAR          CKWH6[3];
   UCHAR          CKW6[3];
   UCHAR          CKWCUM6[3];
   AlphaDateTime_t     CTD6;
   UCHAR          DKWH6[3];
   UCHAR          DKW6[3];
   UCHAR          DKWCUM6[3];
   AlphaDateTime_t     DTD6;
   UCHAR          SPARE6[3];

   UCHAR          PFCB1[3];
   UCHAR          APFCB1[3];
   UCHAR          AKWCB1[3];
   UCHAR          SPARE7[3];
   AlphaDateTime_t     ATDCB1;
   UCHAR          BPFCB1[3];
   UCHAR          BKWCB1[3];
   UCHAR          SPARE8[3];
   AlphaDateTime_t     BTDCB1;
   UCHAR          CPFCB1[3];
   UCHAR          CKWCB1[3];
   UCHAR          SPARE9[3];
   AlphaDateTime_t     CTDCB1;
   UCHAR          DPFCB1[3];
   UCHAR          DKWCB1[3];
   UCHAR          SPARE10[3];
   AlphaDateTime_t     DTDCB1;
   UCHAR          SPARE11[3];

   UCHAR          PFCB2[3];
   UCHAR          APFCB2[3];
   UCHAR          AKWCB2[3];
   UCHAR          SPARE12[3];
   AlphaDateTime_t     ATDCB2;
   UCHAR          BPFCB2[3];
   UCHAR          BKWCB2[3];
   UCHAR          SPARE13[3];
   AlphaDateTime_t     BTDCB2;
   UCHAR          CPFCB2[3];
   UCHAR          CKWCB2[3];
   UCHAR          SPARE14[3];
   AlphaDateTime_t     CTDCB2;
   UCHAR          DPFCB2[3];
   UCHAR          DKWCB2[3];
   UCHAR          SPARE15[3];
   AlphaDateTime_t     DTDCB2;
   UCHAR          SPARE16[3];
} AlphaA1Class11Raw_t;

// class 14   36 bytes
typedef struct _AlphaA1Class14Raw_t
{
   UCHAR          TBLKCF1;          // block 1 configuration
   UCHAR          TBLKCF2;          // block 2 configuration
   UCHAR          TBLKCF3;          // block 3 configuration
   UCHAR          TBLKCF4;          // block 4 configuration
   UCHAR          TBLKCF5;          // block 5 configuration
   UCHAR          TBLKCF6;          // block 6 configuration
   UCHAR          TBLKCF7;          // coincident block 1 configuration
   UCHAR          TBLKCF8;          // coincident block 2 configuration
   UCHAR          DTYFLG;           // demand type flags
   UCHAR          LCFLG;            //load control and eoi flags
   UCHAR          KYZDIV;           //kyz output order
   UCHAR          SPARE17[4];
   UCHAR          LPMEM[2];         //load profile memory size
   UCHAR          LPOUT;            // load profile outage threshold
   UCHAR          LPLEN;            // load profile interval length
   UCHAR          AIOFLG;           // channel A configuration
   UCHAR          BIOFLG;           // channel B configuration
   UCHAR          CIOFLG;           // channel C configuration
   UCHAR          DIOFLG;           // channel D configuration
   UCHAR          EIOFLG;
   UCHAR          FIOFLG;
   UCHAR          GIOFLG;
   UCHAR          HIOFLG;
   UCHAR          LPSCALE;          // load profile scaling factor
   UCHAR          EXSCALE;
   UCHAR          SPARE18[6];
   UCHAR          C14CKS;           // checksum
} AlphaA1Class14Raw_t;


typedef struct _AlphaA1Class0Real_t
{
   // class 0
  BOOL          valid;
  FLOAT        wattHoursPerRevolution;
  USHORT    pulsesPerRevolution;
  FLOAT         energyDecimals;
  FLOAT         demandDecimals;
} AlphaA1Class0Real_t;

typedef struct _AlphaA1Class8Real_t
{
    BOOL    valid;
    ULONG   firmwareSpec;
    USHORT  groupNo;
    USHORT  revisionNo;
    USHORT  meterType;
} AlphaA1Class8Real_t;

typedef struct _AlphaA1Class11Real_t
{
   // class 11
   BOOL           valid;
   FLOAT          KWH1;
   FLOAT          AKWH1;
   FLOAT          AKW1;
   AlphaDateTime_t     ATD1;
   FLOAT          AKWCUM1;
   FLOAT          BKWH1;
   FLOAT          BKW1;
   AlphaDateTime_t     BTD1;
   FLOAT          BKWCUM1;
   FLOAT          CKWH1;
   FLOAT          CKW1;
   AlphaDateTime_t     CTD1;
   FLOAT          CKWCUM1;
   FLOAT          DKWH1;
   FLOAT          DKW1;
   AlphaDateTime_t     DTD1;
   FLOAT          DKWCUM1;

   FLOAT          KWH2;
   FLOAT          AKWH2;
   FLOAT          AKW2;
   AlphaDateTime_t     ATD2;
   FLOAT          AKWCUM2;
   FLOAT          BKWH2;
   FLOAT          BKW2;
   AlphaDateTime_t     BTD2;
   FLOAT          BKWCUM2;
   FLOAT          CKWH2;
   FLOAT          CKW2;
   AlphaDateTime_t     CTD2;
   FLOAT          CKWCUM2;
   FLOAT          DKWH2;
   FLOAT          DKW2;
   AlphaDateTime_t     DTD2;
   FLOAT          DKWCUM2;

   FLOAT          KWH3;
   FLOAT          AKWH3;
   FLOAT          AKW3;
   AlphaDateTime_t     ATD3;
   FLOAT          AKWCUM3;
   FLOAT          BKWH3;
   FLOAT          BKW3;
   AlphaDateTime_t     BTD3;
   FLOAT          BKWCUM3;
   FLOAT          CKWH3;
   FLOAT          CKW3;
   AlphaDateTime_t     CTD3;
   FLOAT          CKWCUM3;
   FLOAT          DKWH3;
   FLOAT          DKW3;
   AlphaDateTime_t     DTD3;
   FLOAT          DKWCUM3;

   FLOAT          KWH4;
   FLOAT          AKWH4;
   FLOAT          AKW4;
   AlphaDateTime_t     ATD4;
   FLOAT          AKWCUM4;
   FLOAT          BKWH4;
   FLOAT          BKW4;
   AlphaDateTime_t     BTD4;
   FLOAT          BKWCUM4;
   FLOAT          CKWH4;
   FLOAT          CKW4;
   AlphaDateTime_t     CTD4;
   FLOAT          CKWCUM4;
   FLOAT          DKWH4;
   FLOAT          DKW4;
   AlphaDateTime_t     DTD4;
   FLOAT          DKWCUM4;

   FLOAT          KWH5;
   FLOAT          AKWH5;
   FLOAT          AKW5;
   AlphaDateTime_t     ATD5;
   FLOAT          AKWCUM5;
   FLOAT          BKWH5;
   FLOAT          BKW5;
   AlphaDateTime_t     BTD5;
   FLOAT          BKWCUM5;
   FLOAT          CKWH5;
   FLOAT          CKW5;
   AlphaDateTime_t     CTD5;
   FLOAT          CKWCUM5;
   FLOAT          DKWH5;
   FLOAT          DKW5;
   AlphaDateTime_t     DTD5;
   FLOAT          DKWCUM5;

   FLOAT          KWH6;
   FLOAT          AKWH6;
   FLOAT          AKW6;
   AlphaDateTime_t     ATD6;
   FLOAT          AKWCUM6;
   FLOAT          BKWH6;
   FLOAT          BKW6;
   AlphaDateTime_t     BTD6;
   FLOAT          BKWCUM6;
   FLOAT          CKWH6;
   FLOAT          CKW6;
   AlphaDateTime_t     CTD6;
   FLOAT          CKWCUM6;
   FLOAT          DKWH6;
   FLOAT          DKW6;
   AlphaDateTime_t     DTD6;
   FLOAT          DKWCUM6;

   FLOAT          PFCB1;
   FLOAT          APFCB1;
   FLOAT          AKWCB1;
   AlphaDateTime_t     ATDCB1;
   FLOAT          BPFCB1;
   FLOAT          BKWCB1;
   AlphaDateTime_t     BTDCB1;
   FLOAT          CPFCB1;
   FLOAT          CKWCB1;
   AlphaDateTime_t     CTDCB1;
   FLOAT          DPFCB1;
   FLOAT          DKWCB1;
   AlphaDateTime_t     DTDCB1;

   FLOAT          PFCB2;
   FLOAT          APFCB2;
   FLOAT          AKWCB2;
   AlphaDateTime_t     ATDCB2;
   FLOAT          BPFCB2;
   FLOAT          BKWCB2;
   AlphaDateTime_t     BTDCB2;
   FLOAT          CPFCB2;
   FLOAT          CKWCB2;
   AlphaDateTime_t     CTDCB2;
   FLOAT          DPFCB2;
   FLOAT          DKWCB2;
   AlphaDateTime_t     DTDCB2;

} AlphaA1Class11Real_t;

// class 14
typedef struct _AlphaA1Class14Real_t
{
   BOOL           valid;
   USHORT         touInput[8];      // tou input flags (TBLKCF1 - TBLKCF8)
   USHORT         lpMemory;     // LPMEM
   USHORT         outageThreshold;  // LPOUT
   USHORT         intervalLength;   // LPLEN
   USHORT         channelInput[8];  // load profile input flags (AIOFLG -- HIOFLG)
   USHORT         scalingFactor;    // LPSCALE
   USHORT         checkSum;

   // calculated, not part of the download
   USHORT           numberOfChannels;
} AlphaA1Class14Real_t;

// temporary Alpha 1
typedef struct _AlphaA1ScanData_t
{
   struct {
       // order in this struct matters, do not move anything around !!!!!
       AlphaA1Class0Raw_t   class0;
       AlphaA1Class8Raw_t   class8;
       AlphaA1Class11Raw_t  class11;
       AlphaA1Class14Raw_t  class14;
   } Byte;

   struct
   {
       AlphaA1Class0Real_t    class0;
       AlphaA1Class8Real_t   class8;
       AlphaA1Class11Real_t   class11;
       AlphaA1Class14Real_t   class14;
   } Real;

   INT      totalByteCount;
   BOOL     bDataIsReal;

} AlphaA1ScanData_t;

typedef struct _AlphaA1LoadProfile_t
{
    ULONG                  porterLPTime;
    ULONG                  bytesRequested;
    ULONG                  dayRecordSize;
    AlphaA1Class0Real_t    class0;
    AlphaA1Class8Real_t    class8;
    AlphaA1Class14Real_t   class14;
    BOOL                   finalDataFlag;
    ULONG                  dataBytesActual;
    BYTE                   loadProfileData[2500];
} AlphaA1LoadProfile_t;


#pragma pack(pop, alpha_packing)     // Restore the prior packing alignment..


class IM_EX_DEVDB CtiDeviceAlphaA1 : public CtiDeviceAlpha
{
    typedef CtiDeviceAlpha Inherited;

    // only needed in the A1
    std::vector<int>     _sLPPulseVector;

public:

   CtiDeviceAlphaA1(BYTE         *dataPtr  = NULL,
                  BYTE          *lpPtr = NULL,
                  BYTE          *wPtr = NULL,
                  ULONG         cnt=0) :
       Inherited (dataPtr,lpPtr,wPtr,cnt)
   {
   }

   // all routines required by base class dev_meter
   INT ResultDisplay (const INMESS &InMessage);
   INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived) override;
   INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes) override;
   INT allocateDataBins (OUTMESS *outMess) override;
   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority = MAXPRIORITY - 4) override;

   YukonError_t generateCommandLoadProfile(CtiXfer  &Transfer, CtiMessageList &traceList ) override;
   YukonError_t generateCommandScan       (CtiXfer  &Transfer, CtiMessageList &tListr ) override;

   YukonError_t decodeResponseScan        (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseLoadProfile (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   INT decodeResultLoadProfile (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
   INT decodeResultScan        (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   INT   getA1ClassOffset(UINT Key, void *ptr);
   INT   getA1FuncOffset (UINT Key, void *ptr);

   LONG findLPDataPoint (AlphaLPPointInfo_t &point, USHORT aMapping, const AlphaA1Class14Real_t &class14);

   BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak, const AlphaA1ScanData_t *aScanData);

   BOOL getRateValueFromBlock (DOUBLE &aValue, USHORT aValueType,USHORT aBlockMapping, USHORT aRate, CtiTime &aPeak,  const AlphaA1ScanData_t *data);
   BOOL getDemandValueFromBlock1 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getDemandValueFromBlock2 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getDemandValueFromBlock3 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getDemandValueFromBlock4 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getDemandValueFromBlock5 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getDemandValueFromBlock6 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock1 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock2 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock3 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock4 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock5 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   BOOL getEnergyValueFromBlock6 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const AlphaA1ScanData_t *aScanData);
   USHORT getOffsetMapping (int aOffset);
   USHORT getRate (int aOffset);

   // from alpha base class in dev_alpha.cpp
   UCHAR touBlockMapping (UCHAR config, USHORT type) override;
   INT freeDataBins () override;
   USHORT calculateStartingByteCountForCurrentScanState (int aClass);
};



