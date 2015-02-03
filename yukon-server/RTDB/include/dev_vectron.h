#pragma once

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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceVectron(const CtiDeviceVectron&);
    CtiDeviceVectron& operator=(const CtiDeviceVectron&);

    typedef CtiDeviceSchlumberger Inherited;

    INT            iCommandPacket;

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

   INT                     getCommandPacket () const;
   CtiDeviceVectron&       setCommandPacket (INT aCommand);


   /*
    *  These guys initiate a scan based upon the type requested.
    */

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

   // interrogation routines
   YukonError_t generateCommandHandshake   (CtiXfer &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommand            (CtiXfer &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandScan        (CtiXfer &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandLoadProfile (CtiXfer &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandSelectMeter (CtiXfer &Transfer, CtiMessageList &traceList) override;

   YukonError_t decodeResponse            (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseHandshake   (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseScan        (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseSelectMeter (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;
   YukonError_t decodeResponseLoadProfile (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived) override;
   virtual INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes);

   INT allocateDataBins (OUTMESS *outMess) override;

   INT decodeResultScan       ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
   INT decodeResultLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   INT decodeResultMMConfig (VectronMMConfig_t *config);
   INT decodeResultRealTime (VectronRealTimeRegister_t *localTimeDate);

   USHORT getRate (int aOffset);
   USHORT getType (int aOffset);
   BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak, const VectronScanData_t *aScanData);
   BOOL getRateValueFromRegister (DOUBLE &aValue, USHORT aType, USHORT aRate, CtiTime &aPeak, const VectronScanData_t *data);
   BOOL getRateValueFromRegister1 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const VectronScanData_t *data);
   BOOL getRateValueFromRegister2 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const VectronScanData_t *data);
   BOOL getRateValueFromRegister3 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const VectronScanData_t *data);
   BOOL getRateValueFromRegister4 (DOUBLE &aValue, USHORT aRate, CtiTime &aPeak, const VectronScanData_t *data);
   INT ResultDisplay (const INMESS &InMessage);

};
