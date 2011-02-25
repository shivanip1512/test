/*-----------------------------------------------------------------------------*
*
* File:   dev_vectron
*
* Class:  CtiDeviceVectron
* Date:   09/07/2000
*
* Author: David Sutton
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_vectron.h-arc  $
* REVISION     :  $Revision: 1.11.10.2 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_VECTRON_H__
#define __DEV_VECTRON_H__
#pragma warning( disable : 4786 )



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "dev_schlum.h"

// possible load profile mappings
#define VECTRON_NO_MAPPING              0
#define VECTRON_WATTHOUR_ENERGY         1
#define VECTRON_VARH_LAG_ENERGY         2
#define VECTRON_VARH_LEAD_ENERGY        3
#define VECTRON_VAH_ENERGY              4
#define VECTRON_WATT_DEMAND             5
#define VECTRON_WATT_TOU_DEMAND         6
#define VECTRON_VAR_LAG_DEMAND          7
#define VECTRON_VA_LAG_DEMAND           8
#define VECTRON_VA_TOTAL_DEMAND         9



#pragma pack(push, schlumberger_packing, 1)

typedef struct {
   BYTE     Year;       // Mod-100 year anyway....
   BYTE     Month;
   BYTE     DayOfMonth;
   BYTE     Hours;
   BYTE     Minutes;
   BYTE     Seconds;
   BYTE     DayOfWeek;
} VectronRealTimeRegister_t;


typedef struct {

   DOUBLE                  pulseWeight;   //   4
   USHORT                  registerAddress;
   USHORT                  energyRegister;
   USHORT                  pulseCount;
} VectronProgram_t;

typedef struct {

   struct
   {
      BYTE                 energyRegisterChannel1;
      BYTE                 energyRegisterChannel2;
      BYTE                 pulseWeightChannel1[4]; //   4
      BYTE                        intervalLength;         //   1
      BYTE                        logicalStart[2];        //   3
      BYTE                        logicalEnd[2];          //   3
      BYTE                 outageLength[2];     //   2
      BYTE                 unused01;
      BYTE                 loadResearchID[14];
      BYTE                       currentRecord[2];       //   2
      BYTE                          currentInterval;        //   1
      BYTE                 reserved01[2];
      BYTE                 leftoverOutageTime[3];
      BYTE                 currentRecordPtr[2];
      BYTE                 reserved02[5];
      BYTE                 reserved03[3];
      BYTE                 coldStartTime[7];
      BYTE                 rserved04[2];
      BYTE                 numberOfChannels;
      BYTE                 registerAddressChannel1;
      BYTE                 registerAddressChannel2;
      BYTE                 pulseWeightChannel2[4];
      BYTE                 intervalTimer;
      BYTE                 pulseCountChannel1[2];
      BYTE                 pulseCountChannel2[2];

   }  Byte;

   struct
   {
      VectronProgram_t        program[2];
      USHORT                      intervalLength;         //   1
      USHORT                      logicalStart;        //   3
      USHORT                      logicalEnd;          //   3
      USHORT                      logicalCurrent;          //   3
      BYTE                 loadResearchID[14];

      USHORT                        currentRecord;       //   2
      USHORT                      recordLength;          //   3
      USHORT                        currentInterval;        //   1

      DOUBLE                  leftoverOutageTime;
      USHORT                  currentRecordPtr;
      DOUBLE                  coldStartTime;

      USHORT                  numberOfChannels;
      USHORT                  intervalTimer;

   }  Real;

   BOOL     dataIsReal;

} VectronMMConfig_t;

typedef struct
{
    FLOAT  voltage;
    FLOAT  current;
} VectronPhaseInfo_t;

typedef struct {
   FLOAT    PeakValue;
   FLOAT    Cumulative;
   BYTE     Minute;
   BYTE     Hour;
   BYTE     Day;
   BYTE     Month;
} VectronMaximumRegister_t;

typedef struct
{
   struct {
        BYTE                             phaseAVoltage[4];
        BYTE                             phaseBVoltage[4];
        BYTE                             phaseCVoltage[4];
        BYTE                             phaseACurrent[4];
        BYTE                             phaseBCurrent[4];
        BYTE                             phaseCCurrent[4];

      BYTE                             unitType[3];
      BYTE                             unitId[8];
      BYTE                             registerMultiplier[4];
      BYTE                             registerMapping[6];

        BYTE                             register2Info1[8]; // either rate E Max or E energy
        BYTE                             register1RateE[12]; // e E Max, Cum
      BYTE                             demandResetCount[2];
      BYTE                             outageCount[2];
        BYTE                             register3RateECum[4]; // rate E Cum
        BYTE                             register1RateA[8]; // e E Max, Cum
        BYTE                             register4RateECum[4]; // rate E Cum
        BYTE                             register1RateB[8]; //  B Max, Cum
        BYTE                             register1RateC[8]; //  C Max, Cum
        BYTE                             register1RateD[8]; //  D Max, Cum

        BYTE                             register2Info2[28];  // either rate A, B max or A-D energy
        BYTE                             registerConfiguration;
        BYTE                             register3Info1[8]; // either rate E Max or E energy
        BYTE                             register4Info1[8]; // either rate E Max or E energy


      BYTE                             sWRev[2];
      BYTE                             fWRev[2];
      BYTE                             progId[2];
      BYTE                             meterId[9];

   }  Byte;

   struct {

        VectronPhaseInfo_t               phaseA;
        VectronPhaseInfo_t               phaseB;
        VectronPhaseInfo_t               phaseC;

      BYTE                             unitType[3];
      BYTE                             unitId[8];
      FLOAT                            registerMultiplier;
      USHORT                           demandResetCount;
      USHORT                           outageCount;
      FLOAT                            sWRev;
      FLOAT                            fWRev;
      USHORT                           progId;
      BYTE                             meterId[9];

        struct
        {
            struct
            {
                struct
                {
                    VectronMaximumRegister_t     rateE;
                    VectronMaximumRegister_t     rateA;
                    VectronMaximumRegister_t     rateB;
                    VectronMaximumRegister_t     rateC;
                    VectronMaximumRegister_t     rateD;
                } demand;
            } data;

            USHORT configFlag;
            USHORT mapping;
        } register1;

        struct
        {
            union
            {
                struct
                {
                    VectronMaximumRegister_t     rateE;
                    VectronMaximumRegister_t     rateA;
                    VectronMaximumRegister_t     rateB;
                } demand;

                struct
                {
                    DOUBLE                       rateE;
                    DOUBLE                       rateA;
                    DOUBLE                       rateB;
                    DOUBLE                       rateC;
                    DOUBLE                       rateD;

                } energy;

            } data;

            USHORT configFlag;
            USHORT mapping;
        } register2;

        struct
        {
            union
            {
                struct
                {
                    VectronMaximumRegister_t     rateE;
                } demand;

                struct
                {
                    DOUBLE                       rateE;
                } energy;

            } data;
            USHORT configFlag;
            USHORT mapping;
        } register3;

        struct
        {
            union
            {
                struct
                {
                    VectronMaximumRegister_t     rateE;
                } demand;

                struct
                {
                    DOUBLE                       rateE;
                } energy;

            } data;
            USHORT configFlag;
            USHORT mapping;
        } register4;

   }  Real;

   BOOL     dataIsReal;

} VectronScanData_t;


typedef struct {
   time_t                     LastFileTime;
   time_t                     RecordTime;
   time_t                     NextRecordTime;
   ULONG                      porterLPTime;
   ULONG                      Flags;               // Misc Flags
   VectronRealTimeRegister_t  RealTime;
   VectronMMConfig_t          MMConfig;
   BYTE                       MMBuffer[1500];
}  VectronLoadProfileMessage_t;                    // To fit in a DIALREPLY we cannot be > 2kB

#pragma pack(pop, schlumberger_packing)     // Restore the prior packing alignment..

class IM_EX_DEVDB CtiDeviceVectron : public CtiDeviceSchlumberger
{
private:

    typedef CtiDeviceSchlumberger Inherited;

    INT            iCommandPacket;

    CtiDeviceVectron & operator=(const CtiDeviceVectron & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - operator=() is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
        }

        return *this;
    }

    CtiDeviceVectron (const CtiDeviceVectron & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - copy constructor is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
        }
    }

protected:

public:

   // default constructor that takes 2 optional parameters
   CtiDeviceVectron ( BYTE         *dataPtr  = NULL,
                      BYTE         *mmPtr  = NULL,
                      BYTE         *timeDatePtr = NULL,
                      BYTE         *mmlProfilePtr = NULL,
                      BYTE         *mmlProfileInputPtr = NULL,
                      BYTE         *lProfilePtr = NULL,
                      ULONG        totalByteCount = 0 );/* :
       iCommandPacket (0),
      CtiDeviceSchlumberger(dataPtr,
                     mmPtr,
                     timeDatePtr,
                     mmlProfilePtr,
                     mmlProfileInputPtr,
                     lProfilePtr,
                     totalByteCount)
   {
      setRetryAttempts(SCHLUMBERGER_RETRIES);
   }
   */

   virtual ~CtiDeviceVectron();/*
   {
      // all the databuffers are destroyed in base if needed

   }                             */


   INT                     getCommandPacket () const;
   CtiDeviceVectron&       setCommandPacket (INT aCommand);


   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

   // interrogation routines
   virtual INT generateCommandHandshake (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommand    (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandScan (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandLoadProfile (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandSelectMeter( CtiXfer  &Transfer, list< CtiMessage* > &traceList);

   virtual INT decodeResponse (CtiXfer &Transfer,INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseHandshake (CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseScan (CtiXfer  &Transfer,INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseSelectMeter ( CtiXfer  &Transfer, INT commReturnValue, list< CtiMessage* > &traceList );
   virtual INT decodeResponseLoadProfile (CtiXfer  &Transfer,INT commReturnValue, list< CtiMessage* > &traceList);

   virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived);
   virtual INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes);

   virtual INT allocateDataBins (OUTMESS *outMess);

   virtual INT decodeResultScan ( INMESS *InMessage,
                          CtiTime &TimeNow,
                          list< CtiMessage* >   &vgList,
                          list< CtiMessage* > &retList,
                          list< OUTMESS* > &outList);

   virtual INT decodeResultLoadProfile ( INMESS *InMessage,
                                 CtiTime &TimeNow,
                                 list< CtiMessage* >   &vgList,
                                 list< CtiMessage* > &retList,
                                 list< OUTMESS* > &outList);

   INT decodeResultMMConfig (VectronMMConfig_t *config);
   INT decodeResultRealTime (VectronRealTimeRegister_t *localTimeDate);

   USHORT getRate (int aOffset);
   USHORT getType (int aOffset);
   BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak, VectronScanData_t *aScanData);
   BOOL getRateValueFromRegister (DOUBLE &aValue, USHORT aType, USHORT aRate, CtiTime &aPeak, VectronScanData_t *data);
   BOOL getRateValueFromRegister1 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, VectronScanData_t *data);
   BOOL getRateValueFromRegister2 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, VectronScanData_t *data);
   BOOL getRateValueFromRegister3 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, VectronScanData_t *data);
   BOOL getRateValueFromRegister4 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, VectronScanData_t *data);
   INT ResultDisplay (INMESS *InMessage);

};

#endif // #ifndef __DEV_VECTRON_H__

